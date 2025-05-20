import axios from 'axios';
import { Platform } from 'react-native';

export const API_SERVER_HOST =
  Platform.select({
    ios: 'http://52.79.216.140', // iOS 시뮬레이터
    android: 'http://52.79.216.140', // 안드로이드 에뮬레이터
    web: 'http://52.79.216.140', // 웹 브라우저
  }); 


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

export const getSettlementList = async (filterParam) => {
  const {month, period, startDate, endDate} = filterParam;

  try {
    const res = await axios.get(
        `${prefix}/settlements/${month}`, 
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