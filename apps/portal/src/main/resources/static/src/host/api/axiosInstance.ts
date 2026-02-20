import axios from 'axios';
import axiosRetry from 'axios-retry';

const axiosInstance = axios.create();

axiosRetry(axiosInstance, {
    retries: 3,
    retryDelay: axiosRetry.exponentialDelay,
});

export { axiosInstance };
