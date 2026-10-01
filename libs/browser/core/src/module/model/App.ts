import { loadRemote } from '@module-federation/enhanced/runtime';
import type { ModuleLifecycle, AppData } from '../../types';
import { MountFunctionNotFoundError } from '../../errors/MountFunctionNotFoundError';

export class App {
    private _displayName: string;
    private _scope: string;
    private _module: string;
    private _entry: string;
    private _description: string;
    private _slot: string;
    private _activationUrl: string;
    private _lifecycle: ModuleLifecycle | null = null;

    constructor(data: AppData) {
        this._displayName = data.displayName;
        this._scope = data.scope;
        this._module = data.module;
        this._entry = data.entry;
        this._description = data.description;
        this._slot = data.slot;
        this._activationUrl = data.activationUrl;
    }

    /** Human-readable name shown in the UI */
    get displayName(): string { return this._displayName; }
    set displayName(value: string) { this._displayName = value; }

    /** Module Federation scope (the remote's `name`) */
    get scope(): string { return this._scope; }
    set scope(value: string) { this._scope = value; }

    /** Exposed module name within the remote */
    get module(): string { return this._module; }
    set module(value: string) { this._module = value; }

    /** Module Federation manifest entry URL */
    get entry(): string { return this._entry; }
    set entry(value: string) { this._entry = value; }

    /** Short description of what this app does */
    get description(): string { return this._description; }
    set description(value: string) { this._description = value; }

    /** Layout slot this app should be mounted into */
    get slot(): string { return this._slot; }
    set slot(value: string) { this._slot = value; }

    /** Client-side route that activates this app */
    get activationUrl(): string { return this._activationUrl; }
    set activationUrl(value: string) { this._activationUrl = value; }

    /**
     * Resolves the remote via Module Federation and stores the lifecycle.
     * Idempotent — safe to call multiple times; subsequent calls are no-ops.
     * Calls lifecycle.load() once after the remote resolves.
     */
    async init(): Promise<void> {
        if (this._lifecycle !== null) return;

        const remoteId = `${this._scope}/${this._module}`;
        const remote = await loadRemote<unknown>(remoteId);

        if (!remote || typeof (remote as ModuleLifecycle).mount !== 'function') {
            throw new MountFunctionNotFoundError(remoteId);
        }

        this._lifecycle = remote as ModuleLifecycle;
        await this._lifecycle.load?.();
    }

    /**
     * Lazily initialises if needed, then mounts the app.
     * Calls lifecycle.bootstrap() before each mount.
     */
    async mount(container: HTMLElement, basename: string): Promise<void> {
        if (this._lifecycle === null) {
            await this.init();
        }
        await this._lifecycle!.bootstrap?.();
        await this._lifecycle!.mount(container, basename);
    }

    /**
     * Tears down the app.
     * Calls lifecycle.unload() then lifecycle.unmount().
     */
    async unmount(): Promise<void> {
        if (this._lifecycle === null) return;
        await this._lifecycle.unload?.();
        await this._lifecycle.unmount?.();
    }
}
