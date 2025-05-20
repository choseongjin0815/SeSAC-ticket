import axios from 'axios';
import { Platform } from 'react-native';

// export const API_SERVER_HOST =
//   // Platform.select({
//   //   ios: 'http://52.79.216.140', // iOS 시뮬레이터
//   //   android: 'http://52.79.216.140', // 안드로이드 에뮬레이터
//   //   web: 'http://52.79.216.140', // 웹 브라우저
//   // }); 
  export const API_SERVER_HOST = 'http://52.79.216.140:8080'; // EC2 공인 IP (혹은 도메인)

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