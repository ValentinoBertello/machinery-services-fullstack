import { useParams, Navigate } from "react-router";
import { useState } from "react";
import MaintenancesHeader from '../components/MaintenancesHeader.tsx';
import '../maintenance.css';
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { toStringLocalDate } from "../utils/date.ts";
import { getMaintenancesByCustomerAndDates } from "../api.ts";
import MaintenanceFormModal from "../components/MaintenanceFormModal.tsx";
import type { Maintenance } from "../types.ts";
import MaintenancesByCustomerTable from "../components/MaintenancesByCustomerTable.tsx";
import { usePagination } from "@/shared/hooks/usePagination.ts";
import Pagination from "@/shared/components/Pagination.tsx";
import { getCustomerById } from "@/features/customers/api.ts";
import { useDateRangeFilters } from "@/shared/hooks/useDateRangeFilters.ts";
import type { DateRangeFilterValue } from "@/shared/types/dateRange.types.ts";
import DateRangeFilters from "@/shared/components/DateRangeFilters.tsx";

export default function MaintenancesByCustomerPage() {
    const { id } = useParams();
    const customerId = Number(id);
    const isValidCustomerId =
        Number.isInteger(customerId) &&
        customerId > 0;

    // ─── busca cliente a partir de su id    
    const { data: customer, error: customerError } = useQuery({
        queryKey: ["customers", "detail", customerId],
        queryFn: () => getCustomerById(customerId),
        enabled: isValidCustomerId,
        retry: false
    });

    const [showModal, setShowModal] = useState(false);
    const [editingMaintenance, setEditingMaintenance] = useState<Maintenance | null>(null);

    // ─── Filters
    const { filters, setFilters } = useDateRangeFilters({
        storagePrefix: "MaintenancesByCustomer",
        allowLastYear: true,
    });

    // ─── Pagination
    const { page, pageSize, setPage, changePageSize } = usePagination({
        storageKey: 'MaintenancesPageSize'
    });

    const sinceStr = toStringLocalDate(filters.since);
    const untilStr = toStringLocalDate(filters.until);
    // ─── Query Data
    const { data, isLoading, error: maintenancesError } = useQuery({
        queryKey: ['maintenances', customerId, sinceStr, untilStr, page, pageSize],
        queryFn: () => getMaintenancesByCustomerAndDates(customerId, sinceStr, untilStr, page, pageSize),
        enabled: !!customerId,
        placeholderData: keepPreviousData
    });

    const handleFiltersChange = (next: DateRangeFilterValue) => {
        setFilters(next);
        setPage(0);
    };

    if (!isValidCustomerId) return <Navigate to="/" />;

    return (
        <div className="container mt-3">

            <MaintenancesHeader
                fullName={customer?.fullName}
                totalElements={data?.totalElements}
                onNewMaintenance={() => { setEditingMaintenance(null); setShowModal(true); }}
            />

            <DateRangeFilters
                value={filters}
                onChange={handleFiltersChange}
                allowLastYear
            />

            <MaintenancesByCustomerTable
                maintenances={data?.content ?? []}
                isLoading={isLoading}
                error={customerError ?? maintenancesError}
                onEdit={(m) => { setEditingMaintenance(m); setShowModal(true) }}
            />

            {data && (
                <Pagination
                    page={data.number}
                    totalPages={data.totalPages}
                    totalElements={data.totalElements}
                    first={data.first}
                    last={data.last}
                    onPageChange={setPage}
                    pageSize={pageSize}
                    onPageSizeChange={changePageSize}
                />
            )}

            {showModal && (
                editingMaintenance ? (
                    <MaintenanceFormModal
                        mode="edit"
                        maintenance={editingMaintenance}
                        onClose={() => setShowModal(false)}
                    />
                ) : customer ? (
                    <MaintenanceFormModal
                        mode="create-with-customer"
                        customerId={customerId}
                        fullName={customer.fullName}
                        onClose={() => setShowModal(false)}
                    />
                ) : null
            )}
        </div>
    );
}
