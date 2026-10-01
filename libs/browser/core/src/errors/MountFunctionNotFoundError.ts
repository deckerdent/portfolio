export class MountFunctionNotFoundError extends Error {
    readonly remoteId: string;

    constructor(remoteId: string) {
        super(`Module is not mountable: ${remoteId} does not expose a mount function`);
        this.name = 'MountFunctionNotFoundError';
        this.remoteId = remoteId;
    }
}
