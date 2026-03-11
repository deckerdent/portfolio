import { createApp, type App } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import type { ModuleLifecycle } from '@portfolio/core';
import { createAxiosInstance } from '@portfolio/core';
import AppComponent from './app/App.vue';
import TimelineView from './views/TimelineView.vue';
import ExperienceView from './views/ExperienceView.vue';
import EducationView from './views/EducationView.vue';
import SkillsView from './views/SkillsView.vue';
import CertificatesAndReferencesView from './views/CertificatesAndReferencesView.vue';
import './styles.css';

// Create CV-specific axios instance with origin from the CV module itself
const cvApi = createAxiosInstance({ baseURL: new URL(import.meta.url).origin });

let _app: App | null = null;

function createRouterInstance(basename: string) {
    return createRouter({
        history: createWebHistory(basename),
        routes: [
            { path: '/', redirect: '/timeline' },
            { path: '/timeline', name: 'timeline', component: TimelineView },
            { path: '/experience', name: 'experience', component: ExperienceView },
            { path: '/education', name: 'education', component: EducationView },
            { path: '/skills', name: 'skills', component: SkillsView },
            { path: '/certificates-references', name: 'certificates-references', component: CertificatesAndReferencesView },
        ],
    });
}

export const mount: ModuleLifecycle['mount'] = (container, basename) => {
    _app = createApp(AppComponent);
    _app.provide('cvApi', cvApi);
    _app.use(createRouterInstance(basename));
    _app.mount(container);
};

export const unmount: NonNullable<ModuleLifecycle['unmount']> = () => {
    _app?.unmount();
    _app = null;
};
