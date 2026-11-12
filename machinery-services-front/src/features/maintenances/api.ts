import type { Maintenance, NewMaintenance, UpdateMaintenance } from './types';
import type { PageResponse } from '../../shared/types/pagination.types';
import { request } from '@/shared/api/client';
import * as demoApi from '@/demo/demoApi';

const isDemo = import.meta.env.VITE_DEMO_MODE === 'true';

export function getMaintenancesByCustomerAndDates(
    customerId: number,
    since: string,
    until: string,
    page: number,
    size: number,
    sortOrder = "desc"
): Promise<PageResponse<Maintenance>> {
    if (isDemo) {
        return demoApi.getMaintenancesByCustomerAndDates(
            customerId,
            since,
            until,
            page,
            size,
            sortOrder,
        );
    }

    const params = new URLSearchParams({
        customerId: String(customerId),
        since,
        until,
        page: String(page),
        size: String(size),
    });

    params.append("sort", `maintenanceDate,${sortOrder}`);

    return request<PageResponse<Maintenance>>(
        "/maintenances/by-customer?" + params.toString()
    );
}

export function createMaintenance(newMaintenanceRecord: NewMaintenance): Promise<Maintenance> {
    if (isDemo) {
        return demoApi.createMaintenance(newMaintenanceRecord);
    }

    return request<Maintenance>("/maintenances", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(newMaintenanceRecord),
    });
}

export function updateMaintenance( id: number, updateMaintenance: UpdateMaintenance ): Promise<Maintenance> {
    if (isDemo) {
        return demoApi.updateMaintenance(id, updateMaintenance);
    }

    return request<Maintenance>(`/maintenances/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(updateMaintenance),
    });
}

export function getMaintenanceByDates(
    since: string,
    until: string,
    page: number,
    size: number,
    sortOrder = "desc"
): Promise<PageResponse<Maintenance>> {
    if (isDemo) {
        return demoApi.getMaintenanceByDates(
            since,
            until,
            page,
            size,
            sortOrder,
        );
    }

    const params = new URLSearchParams({
        since,
        until,
        page: String(page),
        size: String(size),
    });

    params.append("sort", `maintenanceDate,${sortOrder}`);

    return request<PageResponse<Maintenance>>(
        "/maintenances?" + params.toString()
    );
}
