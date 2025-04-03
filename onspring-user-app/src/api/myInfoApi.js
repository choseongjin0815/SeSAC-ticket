import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080';

const prefix = `${API_SERVER_HOST}/api/user`;

export const getMyInfo = async () => {
  try {
    const res = await axios.get(`${prefix}/info`);
    console.log(res.data);  // 서버 응답 데이터
    return res.data;
  } catch (error) {
    console.error("API 호출 실패:", error);  // 에러 처리
  }
};

export const getMyPoints = async () => {
    try {
      const res = await axios.get(`${prefix}/points`);
      console.log(res.data);
      return res.data;
    } catch (error) {
      console.error("포인트 조회 실패:", error);
      return [];
    }
  };

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