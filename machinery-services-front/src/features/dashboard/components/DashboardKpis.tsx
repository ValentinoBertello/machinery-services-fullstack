type DashboardSummary = {
    totalMaintenances?: number;
    avgMonthlyMaintenances?: number;
    distinctCustomers?: number;
    notInvoicedCount?: number;
};

type DashboardKpisProps = {
    summary?: DashboardSummary;
};

export default function DashboardKpis({ summary }: DashboardKpisProps) {
    const items = [
        { label: "Servicios", value: summary?.totalMaintenances },
        { label: "Promedio mensual", value: summary?.avgMonthlyMaintenances },
        { label: "Clientes distintos", value: summary?.distinctCustomers },
        { label: "Servicios sin facturar", value: summary?.notInvoicedCount },
    ];

    return (
        <div className="row g-2 g-sm-3">
            {items.map((kpi, i) => (
                <div className="col-6 col-lg-3" key={i}>
                    <div className="card h-100 rounded-3 border bg-body-tertiary shadow-sm">
                        <div className="card-body d-flex flex-column justify-content-center align-items-center py-2 py-sm-3">
                            <div
                                className="small mb-1 text-uppercase fw-semibold text-center"
                                style={{
                                    color: "#495057",
                                    letterSpacing: ".5px",
                                }}
                            >
                                {kpi.label}
                            </div>

                            <div className="fs-4 fw-bold text-center lh-1">
                                {kpi.value && Math.round(kpi.value)}
                            </div>
                        </div>
                    </div>
                </div>
            ))}
        </div>
    );
}