import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080'; 
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
