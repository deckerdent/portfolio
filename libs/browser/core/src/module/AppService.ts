import { App } from './model/App';
import { AppFactory } from './factory/AppFactory';
import { SlotNotFoundError } from '../errors/SlotNotFoundError';
import { MountFunctionNotFoundError } from '../errors/MountFunctionNotFoundError';

export class AppService {

    private static isSlot(slotId: string): boolean {
        return document.getElementById(`slot:${slotId}`) !== null;
    }

    private static handleMountError(error: unknown, appId: string): void {
        const message = error instanceof Error ? error.message : String(error);
        switch (true) {
            case error instanceof SlotNotFoundError:
                console.error(`[SlotNotFoundError] ${message}`);
                break;
            case error instanceof MountFunctionNotFoundError:
                console.error(`[MountFunctionNotFoundError] ${message}`);
                break;
            default:
                console.error(`[RemoteLoadError] Failed to load remote ${appId}:`, error);
        }
    }

    static async load(url?: string): Promise<App[]> {
        return AppFactory.load(url);
    }

    /**
     * Mount a remote app into its designated slot.
     * Delegates lifecycle management to the App instance.
     */
    static async mount(app: App): Promise<void> {
        const appId = `${app.scope}/${app.module}`;

        try {
            if (!AppService.isSlot(app.slot)) {
                throw new SlotNotFoundError(app.slot);
            }

            const slot = document.getElementById(`slot:${app.slot}`) as HTMLElement;
            await app.mount(slot, app.activationUrl);
        } catch (error) {
            AppService.handleMountError(error, appId);
        }
    }
}
