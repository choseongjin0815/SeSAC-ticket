import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { refreshToken, logoutUser } from './loginSlice';
import store from '../store'; // store를 직접 참조

const setupInterceptors = () => {
  axios.interceptors.request.use(
    async (config) => {
      const accessToken = await AsyncStorage.getItem('FranchiseAccessToken');
      const tokenExp = await AsyncStorage.getItem('FranchiseTokenExp');
      const now = Date.now();

      // 토큰 만료 확인 후 자동 갱신
      if (accessToken && tokenExp && now >= parseInt(tokenExp, 10)) {
        try {
          console.log(' Token expired, refreshing...');
          await store.dispatch(refreshToken()).unwrap();
        } catch (error) {
          console.log(' Token refresh failed, logging out...');
          store.dispatch(logoutUser());
        }
      }

      // 최신 토큰 가져와서 요청에 추가
      const newAccessToken = await AsyncStorage.getItem('FranchiseAccessToken');
      if (newAccessToken) {
        config.headers['Authorization'] = `Bearer ${newAccessToken}`;
      }

      return config;
    },
    (error) => Promise.reject(error)
  );

  axios.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;

      // 401 오류이고, 한 번만 리프레시 시도
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        try {
          console.log('Refreshing token due to 401 error...');
          await store.dispatch(refreshToken()).unwrap();

          // 새로운 토큰으로 요청 다시 시도
          const newAccessToken = await AsyncStorage.getItem('FranchiseAccessToken');
          originalRequest.headers['Authorization'] = `Bearer ${newAccessToken}`;
          return axios(originalRequest);
        } catch (refreshError) {
          console.log('Token refresh failed, logging out...');
          store.dispatch(logoutUser());
          return Promise.reject(refreshError);
        }
      }

      return Promise.reject(error);
    }
  );
};

export default setupInterceptors;
