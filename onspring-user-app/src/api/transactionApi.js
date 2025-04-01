import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080'; 
const prefix = `${API_SERVER_HOST}/api/user`;

export const getTransactionList = async (page = 0, size = 15) => {
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