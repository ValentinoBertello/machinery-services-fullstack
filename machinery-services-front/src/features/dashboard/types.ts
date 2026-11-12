export type DashboardReportResponse = {
    summary: MaintenanceSummary;
    monthlySeries: MonthlyMaintenance[];
    topCustomers: TopCustomer[];
};

export type MaintenanceSummary = {
    totalMaintenances: number;
    distinctCustomers: number;
    notInvoicedCount: number;
    inFieldCount: number;
    inWorkshopCount: number;
    periodDays: number;
    avgMonthlyMaintenances: number;
};

export type MonthlyMaintenance = {
    year: number;
    month: number;
    total: number;
};

export type TopCustomer = {
    customerId: number;
    fullName: string;
    totalMaintenances: number;
    lastMaintenanceDate: string;
};