/**
 * MicroFrontendApp Model
 * Represents the configuration for a microfrontend application
 */
export class MicroFrontendApp {
    displayName: string;
    sourceUrl: string;
    scope: string;
    module: string;
    description: string;
    activationUrl: string;
    slot: 'header' | 'footer' | 'sidebar-left' | 'sidebar-right' | 'app' | 'floating-button';

    constructor(config: {
        displayName: string;
        sourceUrl: string;
        scope: string;
        module: string;
        description: string;
        activationUrl?: string;
        slot: string;
    }) {
        // Validate displayName
        if (!config.displayName || config.displayName.trim() === '') {
            throw new Error('displayName is required');
        }
        if (!/^[a-zA-Z0-9\s_-]+$/.test(config.displayName)) {
            throw new Error('displayName must contain only alphanumeric characters, spaces, underscores, and hyphens');
        }
        this.displayName = config.displayName;

        // Validate sourceUrl
        if (!config.sourceUrl || config.sourceUrl.trim() === '') {
            throw new Error('sourceUrl is required');
        }
        if (!/^https?:\/\/.+/.test(config.sourceUrl)) {
            throw new Error('sourceUrl must be a valid HTTP or HTTPS URL');
        }
        this.sourceUrl = config.sourceUrl;

        // Validate scope
        if (!config.scope || config.scope.trim() === '') {
            throw new Error('scope is required');
        }
        if (!/^[a-zA-Z0-9_-]+$/.test(config.scope)) {
            throw new Error('scope must contain only alphanumeric characters, underscores, and hyphens');
        }
        this.scope = config.scope;

        // Validate module
        if (!config.module || config.module.trim() === '') {
            throw new Error('module is required');
        }
        /*if (!/^\.?\/[a-zA-Z0-9_/-]+$/.test(config.module)) {
            throw new Error('module must start with "./" or "/" and contain only valid path characters');
        }*/
        this.module = config.module;

        // Validate description
        if (!config.description || config.description.trim() === '') {
            throw new Error('description is required');
        }
        this.description = config.description;

        // Validate slot
        const validSlots = ['header', 'footer', 'sidebar-left', 'sidebar-right', 'app', 'floating-button'];
        if (!validSlots.includes(config.slot)) {
            throw new Error(`slot must be one of: ${validSlots.join(', ')}`);
        }
        this.slot = config.slot as typeof this.slot;

        // Generate or validate activationUrl
        if (config.activationUrl) {
            if (!/^\/[a-zA-Z0-9_/-]*$/.test(config.activationUrl)) {
                throw new Error('activationUrl must start with "/" and contain only valid path characters');
            }
            this.activationUrl = config.activationUrl;
        } else {
            // Auto-generate from displayName: replace spaces and underscores with hyphens, lowercase
            this.activationUrl = '/' + config.displayName
                .toLowerCase()
                .replace(/[\s_]+/g, '-')
                .replace(/[^a-z0-9-]/g, '');
        }
    }

    /**
     * Get the slot element ID for this app
     */
    getSlotId(): string {
        return `slot:${this.slot}`;
    }

    /**
     * Get the remote entry key for Module Federation
     */
    getRemoteKey(): string {
        return this.scope;
    }

    /**
     * Get the full module path for Module Federation
     */
    getModulePath(): string {
        return `${this.scope}/${this.module}`;
    }
}
