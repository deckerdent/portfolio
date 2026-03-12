-- V3: Add skills and highlights to professional experience

ALTER TABLE professional_experience
    ADD COLUMN IF NOT EXISTS skills TEXT;

ALTER TABLE professional_experience
    ADD COLUMN IF NOT EXISTS highlights TEXT;
