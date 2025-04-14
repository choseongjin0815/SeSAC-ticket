import axios from 'axios';
import { Platform } from 'react-native';

export const API_SERVER_HOST =
  Platform.select({
    ios: 'http://43.200.223.231:8080', // iOS 시뮬레이터
    android: 'http://43.200.223.231:8080', // 안드로이드 에뮬레이터
    web: 'http://43.200.223.231:8080', // 웹 브라우저
  }); 

const prefix = `${API_SERVER_HOST}/api/user`;

export const getFranchiseList = async(page = 0, size = 10) => {
    try {
        const res = await axios.get(`${prefix}/franchises`, {
          params: { page, size },
        });
        console.log(res.data);
        return res.data; 
      } catch (error) {
        console.log('API 호출 실패', error);
        return null;
      }
}

export const getFranchiseInfo = async(franchiseId) => {
    try {
        const res = await axios.get(`${prefix}/franchises/${franchiseId}`);
        console.log(res.data);
        return res.data;
    } catch (error) {
        console.log('API 호출 실패', error);
        return null;
    }
}