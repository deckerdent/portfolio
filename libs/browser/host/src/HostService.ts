import { init, registerRemotes } from '@module-federation/enhanced/runtime';
import { axiosInstance, AppService } from '@portfolio/core';
import { PortalStoreWriter } from '@portfolio/core/writer';
import type { HostData } from '@portfolio/core';
import { Host } from './model/Host';
import { HostConfigError } from './errors/HostConfigError';
import { RouterService } from './router/RouterService';
import { SourcesService } from './source/SourcesService';

export class HostService {

    /**
     * Load host config from the given URL, initialise Module Federation and start routing.
     * @param url URL of the host config — defaults to /host.json
     */
    static async bootstrap(url: string = '/host.json'): Promise<void> {
        let hostData: HostData;

        try {
            const response = await axiosInstance.get<HostData>(url);
            hostData = response.data;
        } catch (error) {
            throw new HostConfigError(url, error);
        }

        PortalStoreWriter.setHost(hostData);

        const slots = [...document.querySelectorAll('[id^="slot:"]')]
            .map(el => el.id.slice('slot:'.length));
        const host = new Host({ ...hostData, slots });

        document.title = host.title;

        // need to use init as createInstance is buggy
        init({
            name: host.title,
            remotes: [],
            shared: {},
        });

        const apps = await AppService.load(host.appsUrl);
        PortalStoreWriter.setApps(apps);

        const sources = await SourcesService.load(host.sourceUrls);
        PortalStoreWriter.setSources(sources);

        registerRemotes(sources);
        new RouterService(host.basePath, host.slots).init(apps);
    }
}
