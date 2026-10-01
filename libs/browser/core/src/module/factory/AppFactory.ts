import { axiosInstance } from '../../api/axiosInstance';
import { App } from '../model/App';
import type { AppData } from '../../types';

export class AppFactory {
    /**
     * Fetch and parse apps.json into App instances.
     * @param url URL of the apps config — defaults to /apps.json
     */
    static async load(url: string = '/apps.json'): Promise<App[]> {
        const response = await axiosInstance.get<AppData[]>(url);

        if (!Array.isArray(response.data)) {
            throw new TypeError(`apps.json must be an array, got: ${typeof response.data}`);
        }

        return response.data.map((item: AppData, index: number) => {
            try {
                return new App(item);
            } catch (error) {
                throw new Error(
                    `Invalid app at index ${index}: ${error instanceof Error ? error.message : String(error)}`
                );
            }
        });
    }
}
