import { describe, it, expect } from 'vitest';
import { buildTimelineRows } from '../utils/timeline';

describe('CV frontend basic behavior', () => {
    it('treats open-ended entries as newest', () => {
        const rows = buildTimelineRows(
            [
                {
                    id: 'exp-current',
                    title: 'Current Role',
                    companyName: 'Acme',
                    startDate: '2024-01-01',
                    endDate: null,
                    location: null,
                    description: null,
                    skills: ['TypeScript'],
                    highlights: 'Currently leading delivery',
                },
                {
                    id: 'exp-finished',
                    title: 'Past Role',
                    companyName: 'Acme',
                    startDate: '2021-01-01',
                    endDate: '2022-01-01',
                    location: null,
                    description: null,
                    skills: ['Java'],
                    highlights: 'Delivered stable services',
                },
            ],
            [],
            [],
        );

        expect(rows[0].left[0].payload.title).toBe('Current Role');
    });

    it('separates experience to left and education/certificate to right', () => {
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
                    highlights: 'Implemented core UI modules',
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
            [
                {
                    id: 'cert-1',
                    title: 'Cloud Cert',
                    issuingOrganization: 'Cloud Org',
                    startDate: '2024-03-01',
                    endDate: '2024-03-15',
                    location: null,
                    description: null,
                },
            ],
        );

        expect(rows[0].left).toHaveLength(1);
        expect(rows[0].right.length).toBeGreaterThanOrEqual(1);
    });
});
