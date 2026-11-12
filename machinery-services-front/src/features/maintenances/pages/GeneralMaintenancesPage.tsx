import { keepPreviousData, useQuery } from '@tanstack/react-query';
import MaintenancesHeader from '../components/MaintenancesHeader.tsx';
import { useState } from 'react';
import { toStringLocalDate } from '../utils/date.ts';
import { getMaintenanceByDates } from '../api.ts';
import MaintenanceFormModal from '../components/MaintenanceFormModal.tsx';
import type { Maintenance } from '../types.ts';
import MaintenancesTable from '../components/MaintenancesTable.tsx';
import { usePagination } from '@/shared/hooks/usePagination.ts';
import Pagination from '@/shared/components/Pagination.tsx';
import { useDateRangeFilters } from '@/shared/hooks/useDateRangeFilters.ts';
import type { DateRangeFilterValue } from '@/shared/types/dateRange.types.ts';
import DateRangeFilters from '@/shared/components/DateRangeFilters.tsx';

export default function GeneralMaintenancesPage() {
    const [showModal, setShowModal] = useState(false);
    const [editingMaintenance, setEditingMaintenance] = useState<Maintenance | null>(null);

    // ─── Filters
    const { filters, setFilters } = useDateRangeFilters({
        storagePrefix: "Maintenances",
        allowLastYear: false,
    });

    // ─── Paginación
    const { page, pageSize, setPage, changePageSize } = usePagination({
        storageKey: 'MaintenancesPageSize'
    });

    const sinceStr = toStringLocalDate(filters.since);
    const untilStr = toStringLocalDate(filters.until);
    // ─── Query Data
    const { data, isLoading, error: maintenancesError } = useQuery({
        queryKey: ['maintenances', sinceStr, untilStr, page, pageSize],
        queryFn: () => getMaintenanceByDates(sinceStr, untilStr, page, pageSize),
        placeholderData: keepPreviousData
    });

    const handleFiltersChange = (next: DateRangeFilterValue) => {
        setFilters(next);
        setPage(0);
    };

    return (
        <div className="container mt-3">
            <MaintenancesHeader
                totalElements={data?.totalElements}
                onNewMaintenance={() => { setEditingMaintenance(null); setShowModal(true); }}
            />
            <DateRangeFilters
                value={filters}
                onChange={handleFiltersChange}
                allowLastYear={false}
            />
            <MaintenancesTable
                maintenances={data?.content ?? []}
                isLoading={isLoading}
                error={maintenancesError}
                onEdit={(m: Maintenance) => { setEditingMaintenance(m); setShowModal(true) }}
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
                        mode='edit'
                        maintenance={editingMaintenance}
                        onClose={() => setShowModal(false)}
                    />
                ) : (
                    <MaintenanceFormModal mode='create' onClose={() => setShowModal(false)} />
                )
            )}
        </div>
    );
}
