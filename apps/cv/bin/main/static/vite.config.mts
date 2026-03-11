/// <reference types='vitest' />
import path from 'node:path';
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { federation } from '@module-federation/vite';
import { ViteEjsPlugin } from 'vite-plugin-ejs';

export default defineConfig(({ mode, command }) => ({
    root: import.meta.dirname,
    cacheDir: '../../../../../../node_modules/.vite/apps/cv/src/main/resources/static',
    // Empty base makes Vite generate relative asset URLs (new URL(dep, import.meta.url))
    // instead of absolute "/assets/..." paths, so preloads resolve to the correct origin
    // (localhost:8081) rather than the portal's origin (localhost:8080).
    base: '',
    server: {
        port: 4203,
        host: 'localhost',
        cors: true,
        headers: {
            'Access-Control-Allow-Origin': '*',
        },
        origin: 'http://localhost:4203',
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true,
            },
        },
    },
    preview: {
        port: 4301,
        host: 'localhost',
    },
    plugins: [
        ViteEjsPlugin({
            env: { mode, command }
        }),
        vue({
            // isProduction: true tells the Vue SFC compiler not to emit
            // __VUE_HMR_RUNTIME__ registration code. This is required when the
            // CV dev server is consumed by the Spring Boot portal (production Vue
            // runtime), which never defines that global. Vite HMR itself stays on
            // so @module-federation/vite can still serve remotes correctly.
            isProduction: true,
            template: {
                compilerOptions: {
                    isCustomElement: (tag) => tag.startsWith('portal-') || tag.startsWith('wa-'),
                },
            },
        }),
        federation({
            name: 'cv',
            filename: 'remoteEntry.js',
            exposes: {
                './CvApp': './src/apps/cv/Module.ts',
                './CvStaticApp': './src/apps/cv-static/Module.ts',
            },
            // 'auto' derives the public path from remoteEntry.js's own script src at
            // runtime, so cross-origin chunk URLs resolve to localhost:8081 not localhost:8080.
            publicPath: 'auto',
            shared: {
                vue: {
                    singleton: true,
                    requiredVersion: '^3.5.0',
                },
                'vue-router': {
                    singleton: true,
                    requiredVersion: '^4.5.0',
                },
            },
            dts: false,
            manifest: true,
        }),
    ],
    resolve: {
        alias: {
            '@portfolio/core/writer': path.resolve(import.meta.dirname, '../../../../../../libs/browser/core/src/store/PortalStore.ts'),
            '@portfolio/core': path.resolve(import.meta.dirname, '../../../../../../libs/browser/core/src/index.ts'),
            '@portfolio/host': path.resolve(import.meta.dirname, '../../../../../../libs/browser/host/src/index.ts'),
            '@portfolio/app': path.resolve(import.meta.dirname, '../../../../../../libs/browser/app/src/index.ts'),
        },
    },
    optimizeDeps: {
        include: ['rxjs'],
    },
    // In dev mode the CV app is served to a Spring Boot portal that uses Vue's
    // *production* runtime, which never registers __VUE_HMR_RUNTIME__ globally.
    // Stubbing it here prevents ReferenceErrors from any dev-compiled SFC code
    // that still references it, while leaving Vite HMR intact for federation.
    define: command === 'serve' ? {
        __VUE_HMR_RUNTIME__: '({ createRecord() {}, rerender() {}, reload() {} })',
    } : {},
    build: {
        outDir: './dist',
        emptyOutDir: true,
        reportCompressedSize: true,
        target: 'esnext',
        minify: false,
        cssCodeSplit: false,
        commonjsOptions: {
            transformMixedEsModules: true,
        },
    },
    test: {
        name: 'cv',
        watch: false,
        globals: true,
        environment: 'jsdom',
        include: ['{src,tests}/**/*.{test,spec}.{js,mjs,cjs,ts,mts,cts,jsx,tsx}'],
        reporters: ['default'],
        coverage: {
            reportsDirectory: './test-output/vitest/coverage',
            provider: 'v8' as const,
        }
    },
}));
