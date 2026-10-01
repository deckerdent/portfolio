import { axiosInstance } from '@portfolio/core';
import type { SourceEntry } from '@portfolio/core';

export class SourcesService {

    private static normalizeEntry(entry: string): string {
        const trimmed = entry.replace(/\/$/, '');
        return trimmed.endsWith('/mf-manifest.json')
            ? trimmed
            : `${trimmed}/mf-manifest.json`;
    }

    /**
     * Load source entries from one or more config URLs and combine the results.
     * Accepts both relative (e.g. "/portal") and absolute (e.g. "http://localhost:4202") entry URLs.
     * Ensures each entry URL ends with /mf-manifest.json.
     * @param urls Array of URLs to fetch source configs from
     */
    static async load(urls: string[]): Promise<SourceEntry[]> {
        const results = await Promise.all(
            urls.map(async (url) => {
                const response = await axiosInstance.get<SourceEntry[]>(url);

                if (!Array.isArray(response.data)) {
                    throw new TypeError(`${url} must return an array, got: ${typeof response.data}`);
                }

                return response.data.map(({ name, entry }) => ({
                    name,
                    entry: SourcesService.normalizeEntry(entry),
                }));
            })
        );

        return results.flat();
    }
}
