import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080';

const prefix = `${API_SERVER_HOST}/api/franchise`;

export const getSettlements = async () => {
    try {
      const res = await axios.get(`${prefix}/settlements`);
      console.log(res.data);  // 서버 응답 데이터
      return res.data;
    } catch (error) {
      console.error("API 호출 실패:", error);  // 에러 처리
    }
}

export const getSettlementList = async (filterParam) => {
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