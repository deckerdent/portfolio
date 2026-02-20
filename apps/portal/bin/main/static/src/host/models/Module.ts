export type Slot = 'header' | 'footer' | 'sidebar-left' | 'sidebar-right' | 'app' | 'floating-button';

export class Module {
    private _displayName: string;
    private _sourceUrl: string;
    private _scope: string;
    private _module: string;
    private _description: string;
    private _slot: Slot;
    private _activationUrl: string;

    constructor(data: {
        displayName: string;
        sourceUrl: string;
        scope: string;
        module: string;
        description: string;
        slot: Slot;
        activationUrl: string;
    }) {
        this._displayName = data.displayName;
        this._sourceUrl = data.sourceUrl;
        this._scope = data.scope;
        this._module = data.module;
        this._description = data.description;
        this._slot = data.slot;
        this._activationUrl = data.activationUrl;
    }

    /** Human-readable name shown in the UI */
    get displayName(): string { return this._displayName; }
    set displayName(value: string) { this._displayName = value; }

    /** Base URL where the remote is hosted. Empty string means same origin. */
    get sourceUrl(): string { return this._sourceUrl; }
    set sourceUrl(value: string) { this._sourceUrl = value; }

    /** Module Federation scope (the remote's `name`) */
    get scope(): string { return this._scope; }
    set scope(value: string) { this._scope = value; }

    /** Exposed module name within the remote */
    get module(): string { return this._module; }
    set module(value: string) { this._module = value; }

    /** Short description of what this module does */
    get description(): string { return this._description; }
    set description(value: string) { this._description = value; }

    /** Layout slot this module should be mounted into */
    get slot(): Slot { return this._slot; }
    set slot(value: Slot) { this._slot = value; }

    /** Client-side route that activates this module */
    get activationUrl(): string { return this._activationUrl; }
    set activationUrl(value: string) { this._activationUrl = value; }

    /** Resolved entry URL for Module Federation — sourceUrl with trailing slash stripped */
    get entry(): string {
        return this._sourceUrl ? `${this._sourceUrl.replace(/\/$/, '')}/mf-manifest.json` : '/mf-manifest.json';
    }
}
