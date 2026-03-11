import { BehaviorSubject } from 'rxjs';
import type { Observable } from 'rxjs';
import type { HostData, SourceEntry } from '../types';
import type { App } from '../module/model/App';

const _host$ = new BehaviorSubject<HostData | null>(null);
const _apps$ = new BehaviorSubject<App[]>([]);
const _sources$ = new BehaviorSubject<SourceEntry[]>([]);

export class PortalState {
    get host(): HostData | null { return _host$.getValue(); }
    set host(data: HostData) { _host$.next(data); }

    get apps(): App[] { return _apps$.getValue(); }
    set apps(apps: App[]) { _apps$.next(apps); }

    get sources(): SourceEntry[] { return _sources$.getValue(); }
    set sources(sources: SourceEntry[]) { _sources$.next(sources); }
}

const _state = new PortalState();

/**
 * Read-only reactive store shared across all micro-frontends via Module Federation singleton.
 *
 * Subscribe to the observables for reactive updates, or use `getInstance()` for a synchronous
 * point-in-time read.
 *
 * Populated by `@portfolio/host` during bootstrap — values are `null` / `[]` until then.
 */
export class PortalStore {
    private constructor() { }

    /** Emits the host configuration after bootstrap, then on each update. */
    static get host$(): Observable<HostData | null> { return _host$.asObservable(); }

    /** Emits the full list of registered apps after bootstrap, then on each update. */
    static get apps$(): Observable<App[]> { return _apps$.asObservable(); }

    /** Emits the resolved source entries after bootstrap, then on each update. */
    static get sources$(): Observable<SourceEntry[]> { return _sources$.asObservable(); }

    /** Synchronous point-in-time read of the current store values. */
    static getInstance(): PortalState { return _state; }
}

/**
 * Write API for `PortalStore`.
 *
 * Intentionally **not** exported from `@portfolio/core`'s public `index.ts`.
 * Import exclusively from `@portfolio/host` via `'@portfolio/core/writer'`.
 */
export class PortalStoreWriter {
    private constructor() { }

    static setHost(data: HostData): void { _state.host = data; }
    static setApps(apps: App[]): void { _state.apps = apps; }
    static setSources(sources: SourceEntry[]): void { _state.sources = sources; }
}
