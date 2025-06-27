import axios from 'axios';
import { Platform } from 'react-native';

export const API_SERVER_HOST =
  Platform.select({
    ios: 'https://sesacticket.site', // iOS 시뮬레이터
    android: 'https://sesacticket.site', // 안드로이드 에뮬레이터
    web: 'https://sesacticket.site', // 웹 브라우저
  }); 


const prefix = `${API_SERVER_HOST}/api/franchise`;

export const getMenuImage = async(fileName) => {
    const res = await axios.get(`${prefix}/menu/${fileName}`);

    console.log(res);

    res.status;
}
export const postMenuImage = async (formData) => {
  try {
    const response = await axios.put(`${prefix}/menu`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
     
    });
    return response.data;
  } catch (error) {
    console.error('Error uploading menu image:', error);
    throw error;
  }
};
