import axios from 'axios';
import { Platform } from 'react-native';

export const API_SERVER_HOST =
  Platform.select({
    ios: 'https://sesacticket.site', // iOS 시뮬레이터
    android: 'https://sesacticket.site', // 안드로이드 에뮬레이터
    web: 'https://sesacticket.site', // 웹 브라우저
  }); 


const prefix = `${API_SERVER_HOST}/api/franchise`;

export const getMyInfo = async () => {
  try {
    const res = await axios.get(`${prefix}/info`);
    console.log(res.data);  // 서버 응답 데이터
    return res.data;
  } catch (error) {
    console.error("API 호출 실패:", error);  // 에러 처리
  }
};

export const updateInfo = async (franchiseObj) => {
  const res = await axios.put(`${prefix}/info`, franchiseObj);
}

export const updatePassword = async ({ oldPassword, newPassword }) => {
  try {
    const res = await axios.put(`${prefix}/password`, {
      oldPassword,
      newPassword,
    });
    return res.data; 
  } catch (error) {
    throw error.response?.data || '비밀번호 변경 중 오류 발생';
  }
};