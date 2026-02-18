import { createApp } from 'vue';
import { createRouter, createWebHistory } from 'vue-router';
import App from './app/App.vue';
import HomeView from './views/HomeView.vue';
import AboutView from './views/AboutView.vue';

// Create router factory function
function createRouterInstance(basename: string) {
    return createRouter({
        history: createWebHistory(basename),
        routes: [
            {
                path: '/',
                name: 'home',
                component: HomeView,
            },
            {
                path: '/about',
                name: 'about',
                component: AboutView,
            },
        ],
    });
}

// Export mount function for host to call
export function mount(container: HTMLElement, basename: string = '/') {
    const router = createRouterInstance(basename);
    const app = createApp(App);
    app.use(router);
    app.mount(container);

    return {
        unmount: () => app.unmount(),
    };
}

