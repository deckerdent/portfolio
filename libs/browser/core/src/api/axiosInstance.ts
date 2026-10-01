import axios, { type AxiosInstance } from 'axios';
import axiosRetry from 'axios-retry';

function createAxiosInstance(config: object = {}, retries: number = 3): AxiosInstance {
    const instance = axios.create(config);

    axiosRetry(instance, {
        retries: retries,
        retryDelay: axiosRetry.exponentialDelay,
        retryCondition: (error) =>
            axiosRetry.isNetworkError(error) || axiosRetry.isRetryableError(error),
    });

    return instance;
}

/** Default instance — relative to the current origin (no baseURL). */
const axiosInstance = createAxiosInstance();

export { axiosInstance, createAxiosInstance };
