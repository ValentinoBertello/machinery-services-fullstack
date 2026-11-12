import { Link } from "react-router";
import type { Customer } from "../types.ts";
import '../customer.css';
import { ApiError } from "@/shared/types/error.types.ts";

type CustomersTableProps = {
    customers: Customer[];
    isLoading: boolean;
    error: Error | null;
    searchDebounced: string;
}

export default function CustomersTable({
    customers,
    isLoading,
    error,
    searchDebounced
}: CustomersTableProps) {
    return (
        <>
            <table className="customers-table">
                <thead>
                    <tr>
                        <th>Nombre</th>
                        <th>Acción</th>
                    </tr>
                </thead>
                <tbody>
                    {isLoading && (
                        <tr>
                            <td colSpan={2}>Cargando...</td>
                        </tr>
                    )}

                    {error && (
                        <tr>
                            <td colSpan={2}>
                                {error instanceof ApiError
                                    ? error.message
                                    : 'Ocurrió un error inesperado'}
                            </td>
                        </tr>
                    )}

                    {!error && customers.map((customer) => (
                        <tr key={customer.id}>
                            <td>{customer.fullName}</td>
                            <td>
                                <Link
                                    to={`/customers/${customer.id}/maintenances`}
                                    className="btn btn-sm btn-primary"
                                >
                                    Ver servicios
                                </Link>
                            </td>
                        </tr>
                    ))}

                    {!isLoading && !error && customers.length === 0 && (
                        <tr>
                            <td colSpan={2} className="text-center text-muted py-4">
                                {searchDebounced
                                    ? `No se encontraron clientes con ese nombre`
                                    : "No hay clientes cargados"}
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </>
    );
}
