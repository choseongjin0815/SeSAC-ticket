// loginSlice.js
import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import AsyncStorage from '@react-native-async-storage/async-storage';
import jwtDecode from 'jwt-decode';
import { API_SERVER_HOST } from '../api/myPageApi';

// 로그인
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async ({ credentials }, { rejectWithValue }) => {
    try {
      const endpoint = '/api/franchise/login';
      const response = await fetch(`${API_SERVER_HOST}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) throw new Error('Network response was not ok');
      const data = await response.json();
      const decodedToken = jwtDecode(data.accessToken);
      console.log("decode", decodedToken);
      const expTime = decodedToken.exp * 1000;

      await AsyncStorage.multiSet([
        ['FranchiseAccessToken', data.accessToken],
        ['FranchiseRefreshToken', data.refreshToken],
        ['FranchiseTokenExp', expTime.toString()], 
        ['FranchiseId', String(data.id)]
      ]);

      return data;
    } catch (error) {
      return rejectWithValue(error.response?.data || '로그인 실패');
    }
  }
);

// 토큰 리프레시
export const refreshToken = createAsyncThunk(
  'auth/refreshToken',
  async (_, { rejectWithValue }) => {
    try {
      const refreshToken = await AsyncStorage.getItem('FranchiseRefreshToken');
      const response = await fetch(`${API_SERVER_HOST}/api/token/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({refreshToken}
        ),
      });

      const data = await response.json();
      // const response = await axios.post(`${API_SERVER_HOST}/api/token/refresh`, { refreshToken });
      const newAccessToken = data.accessToken;
      const decodedToken = jwtDecode(newAccessToken);
      const newExpTime = decodedToken.exp * 1000;
      

      await AsyncStorage.multiSet([
        ['FranchiseAccessToken', newAccessToken],
        ['FranchiseTokenExp', newExpTime.toString()]
      ]);

      return newAccessToken;
    } catch (error) {
      await AsyncStorage.multiRemove(['FranchiseAccessToken', 'FranchiseRefreshToken', 'FranchiseTokenExp', 'FranchiseId']);
      return rejectWithValue(error.response?.data || '토큰 갱신 실패');
    }
  }
);

// 로그아웃
export const logoutUser = createAsyncThunk(
  'auth/logoutUser',
  async (_, { rejectWithValue }) => {
    try {
      const accessToken = await AsyncStorage.getItem('FranchiseAccessToken');
      const refreshToken = await AsyncStorage.getItem('FranchiseRefreshToken');


      console.log("accessToken" , accessToken);
      // 서버에 로그아웃 요청
      await fetch(`${API_SERVER_HOST}/api/franchise/logout`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${accessToken}`,
        },
        body: JSON.stringify({ refreshToken }),
      });

      // 클라이언트 토큰 삭제
      await AsyncStorage.multiRemove(['FranchiseAccessToken', 'FranchiseRefreshToken', 'FranchiseTokenExp', 'FranchiseId']);
    } catch (error) {
      return rejectWithValue('서버 로그아웃 실패');
    }
  }
);

const loginSlice = createSlice({
  name: 'auth',
  initialState: {
    user: null,
    accessToken: null,
    refreshToken: null,
    id: null,
    isLoading: false,
    error: null,
  },
  reducers: {
    restoreToken: (state, action) => {
      state.accessToken = action.payload.accessToken;
      state.refreshToken = action.payload.refreshToken;
      state.id = action.payload.id;
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginUser.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(loginUser.fulfilled, (state, action) => {
        state.isLoading = false;
        state.accessToken = action.payload.accessToken;
        state.refreshToken = action.payload.refreshToken;
        state.id = action.payload.id;
      })
      .addCase(loginUser.rejected, (state, action) => {
        state.isLoading = false;
        state.error = action.payload;
      })
      .addCase(refreshToken.fulfilled, (state, action) => {
        state.accessToken = action.payload;
      })
      .addCase(refreshToken.rejected, (state) => {
        state.accessToken = null;
        state.refreshToken = null;
      })
      .addCase(logoutUser.fulfilled, (state) => {
        state.user = null;
        state.accessToken = null;
        state.refreshToken = null;
        state.id = null;
      });
  }
});

export const { restoreToken } = loginSlice.actions;
export default loginSlice.reducer;