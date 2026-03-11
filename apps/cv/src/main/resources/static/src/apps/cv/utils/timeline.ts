import type { CertificateResponse, EducationResponse, ExperienceResponse } from '../types';

const OPEN_ENDED_END_INDEX = Number.MAX_SAFE_INTEGER;

export type TimelineEntryKind = 'experience' | 'education' | 'certificate';

export interface NormalizedTimelineEntry {
    id: string;
    kind: TimelineEntryKind;
    startDate: string;
    endDate: string | null;
    startMonthIndex: number;
    endMonthIndex: number;
    sortMonthIndex: number;
    payload: ExperienceResponse | EducationResponse | CertificateResponse;
}

export interface TimelineRow {
    id: string;
    left: NormalizedTimelineEntry[];
    right: NormalizedTimelineEntry[];
    overlapStartMonthIndex: number;
    overlapEndMonthIndex: number;
    sortMonthIndex: number;
}

const parseDateToMonthIndex = (isoDate: string | null | undefined, fallback: 'start' | 'end') => {
    if (!isoDate) {
        return fallback === 'start' ? 0 : OPEN_ENDED_END_INDEX;
    }

    const [yearString = '', monthString = '', dayString = '01'] = isoDate.split('-');
    const parsedYear = Number.parseInt(yearString, 10);
    const parsedMonth = Number.parseInt(monthString || '1', 10);
    const parsedDay = Number.parseInt(dayString || '1', 10);

    const year = Number.isFinite(parsedYear) ? parsedYear : 1970;
    const month = Number.isFinite(parsedMonth) ? Math.min(12, Math.max(1, parsedMonth)) : 1;
    const day = Number.isFinite(parsedDay) ? Math.min(31, Math.max(1, parsedDay)) : 1;

    const date = new Date(year, month - 1, day);
    return date.getFullYear() * 12 + date.getMonth();
};

const normalizeEntry = (
    kind: TimelineEntryKind,
    payload: ExperienceResponse | EducationResponse | CertificateResponse,
): NormalizedTimelineEntry => {
    const startMonthIndex = parseDateToMonthIndex(payload.startDate, 'start');
    const normalizedEndMonthIndex = parseDateToMonthIndex(payload.endDate, 'end');
    const endMonthIndex = Math.max(startMonthIndex, normalizedEndMonthIndex);

    return {
        id: `${kind}-${payload.id}`,
        kind,
        startDate: payload.startDate,
        endDate: payload.endDate,
        startMonthIndex,
        endMonthIndex,
        sortMonthIndex: endMonthIndex,
        payload,
    };
};

const compareEntriesNewestFirst = (a: NormalizedTimelineEntry, b: NormalizedTimelineEntry) => {
    if (b.sortMonthIndex !== a.sortMonthIndex) {
        return b.sortMonthIndex - a.sortMonthIndex;
    }

    if (b.startMonthIndex !== a.startMonthIndex) {
        return b.startMonthIndex - a.startMonthIndex;
    }

    return a.id.localeCompare(b.id);
};

const rangesOverlap = (
    startA: number,
    endA: number,
    startB: number,
    endB: number,
) => startA <= endB && startB <= endA;

const sortRowsNewestFirst = (a: TimelineRow, b: TimelineRow) => {
    if (b.sortMonthIndex !== a.sortMonthIndex) {
        return b.sortMonthIndex - a.sortMonthIndex;
    }

    if (b.overlapStartMonthIndex !== a.overlapStartMonthIndex) {
        return b.overlapStartMonthIndex - a.overlapStartMonthIndex;
    }

    return a.id.localeCompare(b.id);
};

const isRightColumnKind = (kind: TimelineEntryKind) => kind === 'education' || kind === 'certificate';

export const buildTimelineRows = (
    experiences: ExperienceResponse[],
    education: EducationResponse[],
    certificates: CertificateResponse[],
) => {
    const mergedEntries: NormalizedTimelineEntry[] = [
        ...experiences.map((item) => normalizeEntry('experience', item)),
        ...education.map((item) => normalizeEntry('education', item)),
        ...certificates.map((item) => normalizeEntry('certificate', item)),
    ].sort(compareEntriesNewestFirst);

    const rows: TimelineRow[] = [];

    for (const entry of mergedEntries) {
        const existingRow = rows.find((row) =>
            rangesOverlap(
                entry.startMonthIndex,
                entry.endMonthIndex,
                row.overlapStartMonthIndex,
                row.overlapEndMonthIndex,
            ),
        );

        if (!existingRow) {
            rows.push({
                id: `row-${entry.id}`,
                left: isRightColumnKind(entry.kind) ? [] : [entry],
                right: isRightColumnKind(entry.kind) ? [entry] : [],
                overlapStartMonthIndex: entry.startMonthIndex,
                overlapEndMonthIndex: entry.endMonthIndex,
                sortMonthIndex: entry.sortMonthIndex,
            });
            continue;
        }

        if (isRightColumnKind(entry.kind)) {
            existingRow.right.push(entry);
        } else {
            existingRow.left.push(entry);
        }

        existingRow.overlapStartMonthIndex = Math.max(existingRow.overlapStartMonthIndex, entry.startMonthIndex);
        existingRow.overlapEndMonthIndex = Math.min(existingRow.overlapEndMonthIndex, entry.endMonthIndex);
        existingRow.sortMonthIndex = Math.max(existingRow.sortMonthIndex, entry.sortMonthIndex);
    }

    for (const row of rows) {
        row.left.sort(compareEntriesNewestFirst);
        row.right.sort(compareEntriesNewestFirst);
    }

    return rows.sort(sortRowsNewestFirst);
};