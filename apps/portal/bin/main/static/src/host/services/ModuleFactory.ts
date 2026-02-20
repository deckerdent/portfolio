import { axiosInstance } from '../api/axiosInstance';
import { Module, Slot } from '../models/Module';

interface ModuleData {
    displayName: string;
    sourceUrl: string;
    scope: string;
    module: string;
    description: string;
    slot: Slot;
    activationUrl: string;
}

interface RemoteEntry {
    name: string;
    entry: string;
}

export class ModuleFactory {
    /**
     * Fetch and parse modules.json into Module instances.
     * @param url URL of the modules config — defaults to /modules.json
     */
    static async load(url: string = '/modules.json'): Promise<Module[]> {
        const response = await axiosInstance.get<ModuleData[]>(url);

        if (!Array.isArray(response.data)) {
            throw new TypeError(`modules.json must be an array, got: ${typeof response.data}`);
        }

        return response.data.map((item: ModuleData, index: number) => {
            try {
                return new Module(item);
            } catch (error) {
                throw new Error(
                    `Invalid module at index ${index}: ${error instanceof Error ? error.message : String(error)}`
                );
            }
        });
    }

    /**
     * Deduplicate modules into one Remote Federation entry per unique scope+sourceUrl pair.
     * Multiple modules exposed by the same remote (same scope + sourceUrl) collapse to one entry.
     */
    static getUniqueEntries(modules: Module[]): RemoteEntry[] {
        const seen = new Map<string, RemoteEntry>();

        for (const mod of modules) {
            if (!seen.has(mod.entry)) {
                seen.set(mod.entry, { name: mod.scope, entry: mod.entry });
            }
        }

        return Array.from(seen.values());
    }
}
