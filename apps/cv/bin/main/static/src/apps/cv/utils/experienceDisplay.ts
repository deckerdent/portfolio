export const getExperienceSkills = (skills: string[] | null | undefined) => {
    if (!skills || skills.length === 0) {
        return [];
    }

    return skills
        .map((skill) => skill?.trim())
        .filter((skill): skill is string => Boolean(skill));
};

export const getExperienceHighlight = (highlights: string | null | undefined) => {
    const normalized = highlights?.trim();
    return normalized || null;
};
