import axios from 'axios';

export const API_SERVER_HOST = 'http://localhost:8080';

const prefix = `${API_SERVER_HOST}/api/franchise`;

export const getMenuImage = async(fileName) => {
    const res = await axios.get(`${prefix}/menu/${fileName}`);

    console.log(res);

    res.status;

}