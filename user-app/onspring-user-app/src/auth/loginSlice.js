import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {jwtDecode} from 'jwt-decode';
import { API_SERVER_HOST } from '../api/myInfoApi';

// 로그인
export const loginUser = createAsyncThunk(
  'auth/loginUser',
  async ({ credentials }, { rejectWithValue }) => {
    console.log(credentials);
    try {
      const endpoint = '/api/user/login';
      const response = await fetch(`${API_SERVER_HOST}${endpoint}`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
      });

      if (!response.ok) throw new Error('Network response was not ok');
      const data = await response.json();
      
      console.log(data);
      const decodedToken = jwtDecode(data.accessToken);
      console.log("decodedToken:", decodedToken);
      const expTime = decodedToken.exp * 1000;
   
      console.log("data", data);

      await AsyncStorage.multiSet([
        ['accessToken', data.accessToken],
        ['refreshToken', data.refreshToken],
        ['tokenExp', expTime.toString()], 
        ['id', String(data.id)]
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
      const refreshToken = await AsyncStorage.getItem('refreshToken');
      const response = await fetch(`${API_SERVER_HOST}/api/token/refresh`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({refreshToken}
        ),
      });

      const data = await response.json();
      const newAccessToken = data.accessToken;
      const decodedToken = jwtDecode(newAccessToken);
      const newExpTime = decodedToken.exp * 1000;
      

      await AsyncStorage.multiSet([
        ['accessToken', newAccessToken],
        ['tokenExp', newExpTime.toString()]
      ]);

      return newAccessToken;
    } catch (error) {
      await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
      return rejectWithValue(error.response?.data || '토큰 갱신 실패');
    }
  }
);

// 로그아웃
export const logoutUser = createAsyncThunk(
  'auth/logoutUser',
  async () => {
    await AsyncStorage.multiRemove(['accessToken', 'refreshToken', 'tokenExp', 'id']);
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