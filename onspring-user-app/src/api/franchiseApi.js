import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080';

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