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
  async () => {
    await AsyncStorage.multiRemove(['FranchiseAccessToken', 'FranchiseRefreshToken', 'FranchiseTokenExp', 'FranchiseId']);
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