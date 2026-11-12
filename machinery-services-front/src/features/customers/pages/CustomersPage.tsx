import { useState } from "react";
import NewCustomerModal from "../components/NewCustomerModal.tsx";
import CustomerFilters from '../components/CustomerFilters.tsx';
import CustomersTable from "../components/CustomersTable.tsx";
import '../customer.css';
import { useDebouncedValue } from "@/shared/hooks/useDebouncedValue.ts";
import { useCustomers } from "../hooks/useCustomers.ts";
import { useCustomerFilters } from "../hooks/useCustomerFilters.ts";

export default function CustomersTablePage() {
    // ─── Estado
    const [showModal, setShowModal] = useState(false);

    const {
        search,
        setSearch,
        sort,
        setSort,
    } = useCustomerFilters();

    const searchDebounced = useDebouncedValue(search, 400);

    // ─── Data
    const { data: customers = [], isLoading, error } = useCustomers({
        search: searchDebounced,
        sort,
    });

    return (
        <div className="container mt-3">

            <div className="d-flex align-items-center gap-3">
                <h2>Clientes</h2>

                <span className="badge rounded-pill customers-count">
                    {customers.length}
                </span>

                <button className="btn btn-success btn-sm" onClick={() => setShowModal(true)}>
                    + Nuevo cliente
                </button>

            </div>

            <CustomerFilters
                search={search}
                sortOrder={sort}
                onSearchChange={setSearch}
                onSortOrderChange={setSort}
            />

            <CustomersTable
                customers={customers}
                isLoading={isLoading}
                error={error}
                searchDebounced={searchDebounced}
            />

            {showModal && (
                <NewCustomerModal onClose={() => setShowModal(false)} />
            )}

        </div>
    );
}
