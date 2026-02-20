/// <reference types='vitest' />
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { federation } from '@module-federation/vite';
import { ViteEjsPlugin } from 'vite-plugin-ejs';

export default defineConfig(({ mode, command }) => ({
  root: import.meta.dirname,
  cacheDir: '../../../../../../node_modules/.vite/apps/portal/src/main/resources/static',
  //base: './',
  server: {
    port: 4202,
    host: 'localhost',
    cors: true,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    origin: 'http://localhost:4202',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  preview: {
    port: 4300,
    host: 'localhost',
  },
  plugins: [
    ViteEjsPlugin({
      env: { mode, command }
    }),
    vue(),
    federation({
      name: 'host',
      filename: 'remoteEntry.js',
      exposes: {
        './DefaultApp': './src/apps/default/Module.ts',
        './TestApp': './src/apps/test/Module.ts',
        './HelloComponent': './src/components/HelloComponent.ts',
      },
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
  // Uncomment this if you are using workers.
  // worker: {
  //  plugins: [],
  // },
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
    name: 'portal',
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
