export class HostConfigError extends Error {
    constructor(url: string, cause: unknown) {
        super(`Failed to load host config from "${url}": ${cause instanceof Error ? cause.message : String(cause)}`);
        this.name = 'HostConfigError';
    }
}
