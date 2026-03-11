export interface GeneralInfoResponse {
    id: string;
    firstName: string;
    lastName: string;
    maritalStatus: 'SINGLE' | 'MARRIED' | 'DIVORCED' | 'WIDOWED' | 'SEPARATED' | null;
    numberOfChildren: number | null;
    dateOfBirth: string | null;   // ISO date: "yyyy-MM-dd"
    placeOfBirth: string | null;
    nationality: string | null;
    imageUrl: string | null;
    summary: string | null;
}

export interface ExperienceResponse {
    id: string;
    title: string;
    companyName: string;
    startDate: string;     // "yyyy-MM-dd"
    endDate: string | null;
    location: string | null;
    description: string | null;
}

export interface EducationResponse {
    id: string;
    title: string;
    schoolName: string;
    startDate: string;
    endDate: string | null;
    location: string | null;
    description: string | null;
}

export interface SkillResponse {
    id: string;
    title: string;
    level: number;
}

export interface CompetenceResponse {
    id: string;
    description: string;
}

export interface LanguageResponse {
    id: string;
    name: string;
    level: number;
}

export interface HobbyResponse {
    id: string;
    name: string;
}

export interface ReferenceResponse {
    id: string;
    firstName: string;
    lastName: string;
    description: string;
    relation: 'COWORKER' | 'MANAGER' | null;
}

export interface CertificateResponse {
    id: string;
    title: string;
    issuingOrganization: string;
    startDate: string;       // "yyyy-MM-dd"
    endDate: string | null;
    location: string | null;
    description: string | null;
}
