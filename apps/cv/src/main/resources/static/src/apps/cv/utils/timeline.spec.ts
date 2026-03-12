import { describe, it, expect } from 'vitest';
import { buildTimelineRows } from './timeline';

describe('buildTimelineRows', () => {
    it('groups overlapping experience and education into one row', () => {
        const rows = buildTimelineRows(
            [
                {
                    id: 'exp-1',
                    title: 'Engineer',
                    companyName: 'Acme',
                    startDate: '2024-01-01',
                    endDate: '2024-06-01',
                    location: null,
                    description: null,
                    skills: ['Vue'],
                    highlights: 'Implemented a design system',
                },
            ],
            [
                {
                    id: 'edu-1',
                    title: 'MSc',
                    schoolName: 'TU',
                    startDate: '2024-02-01',
                    endDate: '2024-05-01',
                    location: null,
                    description: null,
                },
            ],
            [],
        );

        expect(rows).toHaveLength(1);
        expect(rows[0].left).toHaveLength(1);
        expect(rows[0].right).toHaveLength(1);
    });

    it('orders rows newest first by end date', () => {
        const rows = buildTimelineRows(
            [
                {
                    id: 'exp-old',
                    title: 'Old Role',
                    companyName: 'OldCo',
                    startDate: '2022-01-01',
                    endDate: '2022-12-01',
                    location: null,
                    description: null,
                    skills: ['Java'],
                    highlights: 'Stabilized backend services',
                },
                {
                    id: 'exp-new',
                    title: 'New Role',
                    companyName: 'NewCo',
                    startDate: '2024-01-01',
                    endDate: '2025-01-01',
                    location: null,
                    description: null,
                    skills: ['TypeScript'],
                    highlights: 'Introduced frontend federation',
                },
            ],
            [],
            [],
        );

        expect(rows).toHaveLength(2);
        expect(rows[0].left[0].payload.title).toBe('New Role');
        expect(rows[1].left[0].payload.title).toBe('Old Role');
    });
});
