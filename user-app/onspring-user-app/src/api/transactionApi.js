import axios from 'axios';
import { Platform } from 'react-native';

// export const API_SERVER_HOST =
//   Platform.select({
//     ios: 'http://52.79.216.140', // iOS 시뮬레이터
//     android: 'http://52.79.216.140:8080', // 안드로이드 에뮬레이터
//     web: 'http://52.79.216.140', // 웹 브라우저
//   }) 
export const API_SERVER_HOST = 'http://52.79.216.140:8080'; // EC2 공인 IP (혹은 도메인)

const prefix = `${API_SERVER_HOST}/api/user`;

export const getTransactionList = async (page = 0, size = 10) => {
  try {
    const res = await axios.get(`${prefix}/transactions`, {
      params: { page, size },
    });
    console.log(res.data);
    return res.data; 
  } catch (error) {
    console.log('API 호출 실패', error);
    return null;
  }
};

export const postTransaction = async (franchiseId, pointId, transactionDto, partyId) => {
  try {
    const res = await axios.post(
      `${prefix}/transactions/${franchiseId}/point/${pointId}`,
      transactionDto,
      { params: { partyId } }
    );
    return res.data;
  } catch (error) {
    console.error('결제 요청 실패:', error);
    throw error.response?.data || '결제 요청 중 오류 발생';
  }
};
