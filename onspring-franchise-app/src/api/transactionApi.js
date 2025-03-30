import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080';

const prefix = `${API_SERVER_HOST}/api/franchise`;


export const getTransactionList = async (filterParam) => {
  const {period, startDate, endDate} = filterParam;
    try {
        const res = await axios.get(
            `${prefix}/transactions`, 
            {params : {
                period:period,
                startDate:startDate,
                endDate:endDate
            }});
        console.log(res.data);  // 서버 응답 데이터
        return res.data;
      } catch (error) {
        console.error("API 호출 실패:", error);  // 에러 처리
      }
}

export const cancelTransaction= async(transactionId) => {
  try {
      const res = await axios.put(`${prefix}/transactions/${transactionId}/cancel`);
      console.log(res);
  }catch(error){
    console.error("API 호출 실패", error);
  }
}