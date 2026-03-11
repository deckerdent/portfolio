import type { HostConfig } from '../types';

export class Host {
    private _title: string;
    private _basePath: string;
    private _slots: string[];
    private _sourceUrls: string[];
    private _appsUrl: string;

    constructor(data: HostConfig) {
        this._title = data.title;
        this._basePath = data.basePath ?? '/';
        this._slots = data.slots;
        this._sourceUrls = data.sourceUrls ?? [];
        this._appsUrl = data.appsUrl ?? '/apps.json';
    }

    /** Human-readable title of the host application */
    get title(): string { return this._title; }
    set title(value: string) { this._title = value; }

    /** Base path for the router — defaults to "/" */
    get basePath(): string { return this._basePath; }
    set basePath(value: string) { this._basePath = value; }

    /** Slot IDs discovered in the document at bootstrap time */
    get slots(): string[] { return this._slots; }
    set slots(value: string[]) { this._slots = value; }

    /** URLs of sources configs to load remote entries from */
    get sourceUrls(): string[] { return this._sourceUrls; }
    set sourceUrls(value: string[]) { this._sourceUrls = value; }

    /** URL of the apps config — defaults to "/apps.json" */
    get appsUrl(): string { return this._appsUrl; }
    set appsUrl(value: string) { this._appsUrl = value; }
}
