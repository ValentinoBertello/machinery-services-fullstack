import type { DashboardReportResponse } from './types';
import { request } from '@/shared/api/client';
import * as demoApi from '@/demo/demoApi';

const isDemo = import.meta.env.VITE_DEMO_MODE === 'true';

export function getDashboardReport(
    since: string,
    until: string,
    limit = 5
): Promise<DashboardReportResponse> {
    if (isDemo) {
        return demoApi.getDashboardReport(since, until, limit);
    }

    const params = new URLSearchParams({
        since,
        until,
        limit: String(limit),
    });

    return request<DashboardReportResponse>(
        "/dashboard?" + params.toString()
    );
}
