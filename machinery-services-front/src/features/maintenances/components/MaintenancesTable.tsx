import type { Maintenance } from '../types';
import '../maintenance.css';
import { FaPen } from 'react-icons/fa';
import { Link } from 'react-router';
import { formatStringDateToArgentinaZone } from '../utils/date';

type MaintenancesTableProps = {
    maintenances: Maintenance[];
    isLoading: boolean;
    error: Error | null;
    onEdit: (maintenance: Maintenance) => void;
}

export default function MaintenancesTable({ maintenances, isLoading, error, onEdit }: MaintenancesTableProps) {
    if (isLoading) return <div>Cargando...</div>;
    if (error) return <div className='text-center text-danger'>{error.message}</div>;
    return (
        <table className="maintenances-table">
            <thead>
                <tr>
                    <th scope="col" className="col-2">Cliente</th>
                    <th scope="col" className="col-1">Fecha</th>
                    <th scope="col" className="col-1">Facturación</th>
                    <th scope="col" className="col-2">Modelo</th>
                    <th scope="col" className="col-5">Observaciones</th>
                    <th scope="col" className="col-1"></th>
                </tr>
            </thead>
            <tbody>
                {maintenances.map((maintenance) => (
                    <tr key={maintenance.id}>
                        <td>
                            <Link
                                to={`/customers/${maintenance.customerId}/maintenances`}
                                className="customer-accent-link"
                                title={`Ver servicios de ${maintenance.customerName}`}
                            >
                                {maintenance.customerName}
                            </Link>
                        </td>
                        <td>{formatStringDateToArgentinaZone(maintenance.maintenanceDate)}</td>
                        <td>
                            {maintenance.invoiceDate
                                ? formatStringDateToArgentinaZone(maintenance.invoiceDate)
                                : <span className="pending">-</span>}
                        </td>
                        <td className="model">{maintenance.model}</td>
                        <td>
                            {maintenance.description}
                            {maintenance.place && (
                                <span className={`place-text ${maintenance.place.toLowerCase()}`}>
                                    {maintenance.place === 'FIELD' ? ' CAMPO' : ' TALLER'}
                                </span>
                            )}
                        </td>
                        <td>
                            <button
                                type="button"
                                className="btn-edit"
                                aria-label="Editar servicio"
                                title="Editar"
                                onClick={() => onEdit(maintenance)}
                            >
                                <FaPen size={17} />
                            </button>
                        </td>
                    </tr>
                ))}

                {!isLoading && maintenances.length === 0 && (
                    <tr>
                        <td colSpan={6} className="text-center text-muted py-4">
                            No hay servicios en esas fechas...
                        </td>
                    </tr>
                )}
            </tbody>
        </table>
    );
}