import { init, loadRemote, registerRemotes } from '@module-federation/enhanced/runtime';
import { ModuleFactory } from './services/ModuleFactory';
import { Module } from './models/Module';
import { SlotNotFoundError } from './errors/SlotNotFoundError';
import { MountFunctionNotFoundError } from './errors/MountFunctionNotFoundError';

interface Mountable {
    mount: (container: HTMLElement) => void;
}

async function bootstrap() {
    init({
        name: 'host',
        remotes: [],
        shared: {},
    });

    const modules = await ModuleFactory.load();
    const manifests = ModuleFactory.getUniqueEntries(modules);

    registerRemotes(manifests);
    //TODO: Dangerous! mountModule is async .forEach doesn't wait for promises, so errors might be swallowed. 
    //Consider using for...of with await or Promise.allSettled.
    //However, will do it for this portfolio repo
    modules.forEach(module => {
        mountModule(module);
    });
}

const mountModule = async (module: Module): Promise<Mountable | null> => {
    const remoteId = `${module.scope}/${module.module}`;

    try {
        const remoteModule = await loadRemote<Mountable>(remoteId);

        const slot = document.getElementById(`slot:${module.slot}`);
        if (!slot) {
            throw new SlotNotFoundError(module.slot);
        }

        if (typeof remoteModule?.mount !== 'function') {
            throw new MountFunctionNotFoundError(remoteId);
        }

        remoteModule.mount(slot);
        return remoteModule;
    } catch (error) {
        if (error instanceof SlotNotFoundError) {
            console.error(`[SlotNotFoundError] ${error.message}`);
        } else if (error instanceof MountFunctionNotFoundError) {
            console.error(`[MountFunctionNotFoundError] ${error.message}`);
        } else {
            console.error(`[RemoteLoadError] Failed to load remote ${remoteId}:`, error);
        }
    }

    return null;
};

await bootstrap();
