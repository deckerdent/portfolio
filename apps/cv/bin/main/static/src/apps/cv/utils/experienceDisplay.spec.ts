import { describe, expect, it } from 'vitest';
import { getExperienceHighlight, getExperienceSkills } from './experienceDisplay';

describe('experienceDisplay helpers', () => {
    it('normalizes skills for badge rendering', () => {
        const skills = getExperienceSkills([' Java ', ' ', 'Spring Boot', '', 'PostgreSQL']);
        expect(skills).toEqual(['Java', 'Spring Boot', 'PostgreSQL']);
    });

    it('returns null highlight for blank values and text for valid values', () => {
        expect(getExperienceHighlight('   ')).toBeNull();
        expect(getExperienceHighlight(' Delivered key platform modules ')).toBe('Delivered key platform modules');
    });
});
