export class SlotNotFoundError extends Error {
    readonly slotId: string;

    constructor(slotId: string) {
        super(`Slot not found: #slot:${slotId}`);
        this.name = 'SlotNotFoundError';
        this.slotId = slotId;
    }
}
