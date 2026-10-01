/// <reference types='vitest' />
import path from 'path';
import { randomUUID } from 'node:crypto';
import net from 'node:net';
import { defineConfig, type ViteDevServer } from 'vite';
import vue from '@vitejs/plugin-vue';
import { federation } from '@module-federation/vite';
import { ViteEjsPlugin } from 'vite-plugin-ejs';

/** Returns true if a TCP connection to host:port succeeds within the timeout. */
function isReachable(host: string, port: number, timeoutMs = 1000): Promise<boolean> {
  return new Promise(resolve => {
    const socket = new net.Socket();
    const done = (result: boolean) => { socket.destroy(); resolve(result); };
    socket.setTimeout(timeoutMs);
    socket.once('connect', () => done(true));
    socket.once('error', () => done(false));
    socket.once('timeout', () => done(false));
    socket.connect(port, host);
  });
}

/**
 * Intercepts selected /api routes in dev so the frontend works without
 * the Spring backend running. Runs before the proxy middleware.
 * Auto-disables itself when localhost:8080 is reachable.
 */
function devMockApiPlugin() {
  return {
    name: 'dev-mock-api',
    async configureServer(server: ViteDevServer) {
      const backendUp = await isReachable('localhost', 8080);
      if (backendUp) {
        console.info('[dev-mock-api] localhost:8080 is reachable — mock disabled, using proxy.');
        return;
      }
      console.info('[dev-mock-api] localhost:8080 unreachable — mock API active.');
      server.middlewares.use((req: any, res: any, next: () => void) => {
        const pathname = ((req.url as string) ?? '').split('?')[0];

        if (req.method === 'GET' && pathname === '/api/host/initialized') {
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({ initialized: false }));
          return;
        }

        if (req.method === 'GET' && pathname === '/api/host') {
          res.setHeader('Content-Type', 'application/json');
          res.end(JSON.stringify({
            title: 'portfolio',
            basePath: '/',
            appsUrl: '/apps_setup.json',
            sourceUrls: ['/sources.json'],
            configProps: {},
          }));
          return;
        }

        if (req.method === 'POST' && pathname === '/api/host/config') {
          let body = '';
          req.on('data', (chunk: Buffer) => { body += chunk.toString(); });
          req.on('end', () => {
            try {
              const { key, value } = JSON.parse(body);
              res.statusCode = 201;
              res.setHeader('Content-Type', 'application/json');
              res.end(JSON.stringify({ id: randomUUID(), key, value }));
            } catch {
              res.statusCode = 400;
              res.end('Bad Request');
            }
          });
          return;
        }

        next();
      });
    },
  };
}

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
    ...(command === 'serve' ? [devMockApiPlugin()] : []),
    ViteEjsPlugin({
      env: { mode, command }
    }),
    vue({
      template: {
        compilerOptions: {
          isCustomElement: (tag) => tag.startsWith('-'),
        },
      },
    }),
    federation({
      name: 'host',
      filename: 'remoteEntry.js',
      exposes: {
        './DefaultApp': './src/apps/default/Module.ts',
        './TestApp': './src/apps/test/Module.ts',
        './SetupApp': './src/apps/setup/Module.ts',
        './HelloComponent': './src/components/HelloComponent.ts',
        './PortalLink': './src/components/PortalLink.ts',
        './PortalHeader': './src/components/PortalHeader.ts',
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
    rollupOptions: {
      treeshake: {
        moduleSideEffects: (id) => id.includes('@awesome.me/webawesome'),
      },
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
