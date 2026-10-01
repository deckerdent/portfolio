/** Constructor argument for the Host domain model — extends HostData with runtime-discovered slots */
export interface HostConfig {
    title: string;
    basePath?: string;
    slots: string[];
    sourceUrls?: string[];
    appsUrl?: string;
}
