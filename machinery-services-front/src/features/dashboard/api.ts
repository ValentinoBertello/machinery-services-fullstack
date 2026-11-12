import type { DashboardReportResponse } from './types';
import { request } from '@/shared/api/client';

export function getDashboardReport(
    since: string,
    until: string,
    limit = 5
): Promise<DashboardReportResponse> {
    const params = new URLSearchParams({
        since,
        until,
        limit: String(limit),
    });

    return request<DashboardReportResponse>(
        "/dashboard?" + params.toString()
    );
}