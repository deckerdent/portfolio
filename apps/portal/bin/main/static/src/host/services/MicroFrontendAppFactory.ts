import { MicroFrontendApp } from '../models/MicroFrontendApp';

/**
 * MicroFrontendAppFactory
 * Loads and parses microfrontend app configurations from JSON
 */
export class MicroFrontendAppFactory {
    /**
     * Load microfrontend app configurations from JSON file
     * @param jsonPath Path to the JSON configuration file
     * @returns Promise resolving to array of MicroFrontendApp instances
     */
    static async loadFromJson(jsonPath: string): Promise<MicroFrontendApp[]> {
        try {
            const response = await fetch(jsonPath);

            if (!response.ok) {
                throw new Error(`Failed to load apps configuration: ${response.status} ${response.statusText}`);
            }

            const data = await response.json();

            if (!Array.isArray(data)) {
                throw new Error('Apps configuration must be an array');
            }

            const apps: MicroFrontendApp[] = [];

            for (let i = 0; i < data.length; i++) {
                try {
                    const app = new MicroFrontendApp(data[i]);
                    apps.push(app);
                } catch (error) {
                    console.error(`Failed to create app at index ${i}:`, error);
                    throw new Error(`Invalid app configuration at index ${i}: ${error instanceof Error ? error.message : 'Unknown error'}`);
                }
            }

            return apps;
        } catch (error) {
            console.error('Failed to load microfrontend apps:', error);
            throw error;
        }
    }

    /**
     * Load microfrontend app configurations from inline JSON object
     * @param config Array of app configuration objects
     * @returns Array of MicroFrontendApp instances
     */
    static loadFromObject(config: any[]): MicroFrontendApp[] {
        if (!Array.isArray(config)) {
            throw new Error('Configuration must be an array');
        }

        const apps: MicroFrontendApp[] = [];

        for (let i = 0; i < config.length; i++) {
            try {
                const app = new MicroFrontendApp(config[i]);
                apps.push(app);
            } catch (error) {
                console.error(`Failed to create app at index ${i}:`, error);
                throw new Error(`Invalid app configuration at index ${i}: ${error instanceof Error ? error.message : 'Unknown error'}`);
            }
        }

        return apps;
    }
}
