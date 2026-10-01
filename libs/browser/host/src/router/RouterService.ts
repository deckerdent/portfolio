import Navigo from 'navigo';
import { App, AppService } from '@portfolio/core';

export class RouterService {
    private readonly activeApps = new Map<string, App>();
    private readonly slots: string[] = [];
    private readonly router: Navigo;

    private static normalizePath(path: string): string {
        if (!path) return '/';

        const withoutQueryOrHash = path.split(/[?#]/, 1)[0] ?? '/';
        const trimmed = withoutQueryOrHash.replace(/\/+$/, '');

        return trimmed === '' ? '/' : trimmed;
    }

    private static isPrefixMatch(pathname: string, activationUrl: string): boolean {
        const path = RouterService.normalizePath(pathname);
        const activation = RouterService.normalizePath(activationUrl);

        if (activation === '/') {
            return true;
        }

        return path === activation || path.startsWith(`${activation}/`);
    }

    private getCurrentPathname(): string {
        return RouterService.normalizePath(globalThis.location.pathname);
    }

    private getPathnameFromHref(href: string): string {
        const url = new URL(href, globalThis.location.origin);
        return RouterService.normalizePath(url.pathname);
    }

    private getNavigoPatterns(activationUrl: string): string[] {
        const normalized = RouterService.normalizePath(activationUrl);

        if (normalized === '/') {
            return ['/', '/:path*'];
        }

        return [normalized, `${normalized}/*`];
    }

    constructor(basePath: string, slots: string[]) {
        this.router = new Navigo(basePath);
        this.slots = slots;
    }

    private async activate(pathname: string, apps: App[]): Promise<void> {
        for (const slot of this.slots) {
            const candidates = apps.filter(
                a => a.slot === slot && RouterService.isPrefixMatch(pathname, a.activationUrl)
            );
            if (candidates.length === 0) continue;

            const normalizedPath = RouterService.normalizePath(pathname);
            const exact = candidates.find(a =>
                RouterService.normalizePath(a.activationUrl) === normalizedPath
            );
            const match = exact ?? candidates.reduce(
                (best, a) => (a.activationUrl.length > best.activationUrl.length ? a : best),
                candidates[0]
            );

            if (this.activeApps.get(slot) === match) continue;

            const previousApp = this.activeApps.get(slot);
            if (previousApp) {
                await previousApp.unmount();
            }

            const slotEl = document.getElementById(`slot:${slot}`);
            if (slotEl) {
                slotEl.innerHTML = '';
            }

            if (slot === 'app') {
                const previous = this.activeApps.get('app');
                if (previous) {
                    document.title = document.title.replace(previous.displayName, match.displayName);
                }
            }

            this.activeApps.set(slot, match);
            await AppService.mount(match);
            this.router.updatePageLinks();
        }
    }

    init(apps: App[]): void {
        const routedApps = apps.filter(a => a.slot === 'app');
        const urls = [...new Set(routedApps.map(a => a.activationUrl))];
        const patterns = [...new Set(urls.flatMap(url => this.getNavigoPatterns(url)))];

        for (const pattern of patterns) {
            this.router.on(pattern, () => this.activate(this.getCurrentPathname(), apps));
        }

        this.router.notFound(() => {
            this.activate(this.getCurrentPathname(), apps);
        });

        document.addEventListener('portal-link:connected', () => {
            this.router.updatePageLinks();
        });

        document.addEventListener('portal-navigate', (e: Event) => {
            const { href } = (e as CustomEvent<{ href: string }>).detail;
            this.router.navigate(href);
            this.activate(this.getPathnameFromHref(href), apps);
        });

        this.router.resolve();
    }
}
