import DateRangeFilters from "@/shared/components/DateRangeFilters";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { getDashboardReport } from "../api";
import { useDateRangeFilters } from "@/shared/hooks/useDateRangeFilters";
import { toStringLocalDate } from "@/features/maintenances/utils/date";
import DashboardKpis from "../components/DashboardKpis";
import PlacesPieChart from "../components/PlacesPieChart";
import TopCustomersTable from "../components/TopCustomersTable";
import DashboardCard from "../components/DashboardCard";
import MaintenancesBarChart from "../components/MaintenancesBarChart";

export default function DashboardPage() {
    // ─── Filters
    const { filters, setFilters } = useDateRangeFilters({
        storagePrefix: "Dashboard",
        allowLastYear: true
    });
    const sinceStr = toStringLocalDate(filters.since);
    const untilStr = toStringLocalDate(filters.until);

    // ─── Query Data
    const { data, isLoading, error: error } = useQuery({
        queryKey: ['dashboard', sinceStr, untilStr],
        queryFn: () => getDashboardReport(sinceStr, untilStr),
        placeholderData: keepPreviousData
    });

    if (isLoading) return <div>Cargando...</div>;
    return (
        <div className="container mt-3">
            <DateRangeFilters
                value={filters}
                onChange={setFilters}
                allowLastYear={true}
            />

            {error && <p className="text-center text-danger">{String((error as Error).message ?? error)}</p>}

            <DashboardKpis
                summary={data?.summary}
            />

            <div className="row g-3 mt-1">
                <div className="col-12">
                    <DashboardCard>
                        <MaintenancesBarChart series={data?.monthlySeries ?? []} />
                    </DashboardCard>
                </div>
            </div>

            <div className="row g-3 mt-1 mb-5">
                <div className="col-12 col-lg-6">
                    <DashboardCard>
                        <PlacesPieChart
                            inFieldCount={data?.summary.inFieldCount}
                            inWorkshopCount={data?.summary.inWorkshopCount}
                        />
                    </DashboardCard>
                </div>
                <div className="col-12 col-lg-6">
                    <DashboardCard>
                        <TopCustomersTable customers={data?.topCustomers} />
                    </DashboardCard>
                </div>
            </div>
        </div>
    );
}