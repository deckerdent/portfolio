import { createApp, type App } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import type { ModuleLifecycle } from '@portfolio/core';
import AppComponent from './app/App.vue';
import StaticLandingView from './views/StaticLandingView.vue';
import './styles.css';

let _app: App | null = null;

function createRouterInstance(basename: string) {
    return createRouter({
        history: createWebHistory(basename),
        routes: [
            { path: '/', name: 'cv-static', component: StaticLandingView },
        ],
    });
}

export const mount: ModuleLifecycle['mount'] = (container, basename) => {
    _app = createApp(AppComponent);
    _app.use(createRouterInstance(basename));
    _app.mount(container);
};

export const unmount: NonNullable<ModuleLifecycle['unmount']> = () => {
    _app?.unmount();
    _app = null;
};
