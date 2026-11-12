import type { ChangeEvent } from "react";
import '../customer.css';
import type { CustomerSortValue } from "../hooks/useCustomerFilters";

type CustomerFiltersProps = {
    search: string;
    sortOrder: CustomerSortValue;
    onSearchChange: (value: string) => void;
    onSortOrderChange: (value: CustomerSortValue) => void;
};

export default function CustomerFilters({
    search,
    sortOrder,
    onSearchChange,
    onSortOrderChange
}: CustomerFiltersProps) {
    return (
        <div className="row g-2 mb-3 mt-1">
            <div className="col-md-6">
                <input
                    type="text"
                    className="form-control"
                    placeholder="Buscar por nombre..."
                    value={search}
                    onChange={(e: ChangeEvent<HTMLInputElement>) =>
                        onSearchChange(e.target.value)
                    }
                />
            </div>
            <div className="col-md-3">
                <select
                    className="form-select"
                    value={sortOrder}
                    onChange={(e: ChangeEvent<HTMLSelectElement>) =>
                        onSortOrderChange(e.target.value as CustomerSortValue)
                    }
                >
                    <option value="fullName,asc">Nombre (A-Z)</option>
                    <option value="fullName,desc">Nombre (Z-A)</option>
                    <option value="maintenanceCount,desc">Más activos</option>

                </select>
            </div>
        </div>
    );
}
