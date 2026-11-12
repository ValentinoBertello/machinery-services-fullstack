import { formatStringDateToArgentinaZone } from "@/features/maintenances/utils/date";
import "../dashboard.css";
import type { TopCustomer } from "../types";

type TopCustomersTableProps = {
    customers?: TopCustomer[];
};

const initials = (name: string) =>
    name.trim().split(/\s+/).slice(0, 2).map((w) => w[0]).join("").toUpperCase() || "?";

export default function TopCustomersTable({ customers = [] }: TopCustomersTableProps) {
    return (
        <div>
            <div className="dashboard-card-title">Top Clientes</div>
            <div className="table-customers">
                <div className="table-responsive">
                    <table className="table table-hover align-middle mb-0">
                        <thead>
                            <tr>
                                <th>Cliente</th>
                                <th className="text-end">Servicios</th>
                                <th className="text-end">Último servicio</th>
                            </tr>
                        </thead>
                        <tbody>
                            {customers.map((c) => (
                                <tr key={c.customerId}>
                                    <td>
                                        <span className="table-customers-avatar">{initials(c.fullName)}</span>
                                        {c.fullName}
                                    </td>
                                    <td className="text-end fw-semibold">{c.totalMaintenances}</td>
                                    <td className="text-end text-muted">
                                        {formatStringDateToArgentinaZone(c.lastMaintenanceDate)}
                                    </td>
                                </tr>
                            ))}
                            {customers.length === 0 && (
                                <tr>
                                    <td colSpan={3} className="text-center text-muted py-4">
                                        Todavía no hay clientes con servicios registrados.
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}