/**
 * Contract that a remote module must satisfy so the host can manage its full lifecycle.
 *
 * Call order:
 *   init  → loadRemote → load?()
 *   mount → bootstrap?() → mount(container, basename)
 *   unmount → unload?() → unmount?()
 */
export interface ModuleLifecycle {
    /** Called once after loadRemote resolves — use for one-time setup (i18n, cache warm-up, etc.) */
    load?: () => Promise<void> | void;
    /** Called before every mount — use for per-activation preparation (restore state, reset stores, etc.) */
    bootstrap?: () => Promise<void> | void;
    /** Mount the app into the given container at the given basename */
    mount: (container: HTMLElement, basename: string) => Promise<void> | void;
    /** Called before unmount — use for teardown (persist state, cancel subscriptions, etc.) */
    unload?: () => Promise<void> | void;
    /** Unmount the app from the DOM */
    unmount?: () => Promise<void> | void;
}

/** Shape of a single entry in apps.json */
export interface AppData {
    displayName: string;
    scope: string;
    module: string;
    entry: string;
    description: string;
    slot: string;
    activationUrl: string;
}

/** Shape of host.json — the raw configuration payload fetched at bootstrap */
export interface HostData {
    title: string;
    basePath?: string;
    sourceUrls?: string[];
    appsUrl?: string;
}

/** Shape of a single entry in a sources.json file */
export interface SourceEntry {
    name: string;
    entry: string;
}
