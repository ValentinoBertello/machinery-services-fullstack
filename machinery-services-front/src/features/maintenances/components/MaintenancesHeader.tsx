import { useNavigate } from "react-router"
import '../maintenance.css';

type MaintenancesHeaderProps = {
    fullName?: string;
    totalElements?: number;
    onNewMaintenance: () => void;
}

export default function MaintenancesHeader({
    fullName,
    totalElements,
    onNewMaintenance
}: MaintenancesHeaderProps) {
    const navigate = useNavigate();
    return (
        <div className="page-header d-flex align-items-center gap-3 mb-3">
            {fullName && (
                <button
                    onClick={() => navigate(-1)}
                    className="btn btn-secondary btn-sm">
                    ← Volver
                </button>
            )}

            <h3 className="page-title mb-0">
                Servicios{fullName && <> de <span className="client-name">{fullName}</span></>}
            </h3>

            <span className="badge rounded-pill maintenances-count">
                {totalElements}
            </span>
            <button className="btn btn-success btn-sm ms-auto" onClick={onNewMaintenance}>
                + Nuevo Servicio
            </button>
        </div>
    );
}
