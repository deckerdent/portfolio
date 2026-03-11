import '@awesome.me/webawesome/dist/styles/webawesome.css';
import '../../public/portal.css';
import { axiosInstance } from '@portfolio/core';
import { HostService } from '@portfolio/host';

const { data } = await axiosInstance.get<{ initialized: boolean }>('/api/host/initialized');
await HostService.bootstrap(data.initialized ? '/api/host' : '/host.json');
