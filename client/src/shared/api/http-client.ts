import axios from 'axios'

const baseURL = process.env.EXPO_PUBLIC_API_BASE_URL;

if (!baseURL) {
  throw new Error('EXPO_PUBLIC_API_BASE_URL is not defined');
}

export const httpClient = axios.create({
    baseURL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});

httpClient.interceptors.request.use((config) => {
    // TODO: Token injection will be add here
    return config;
})