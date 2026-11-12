import { useEffect, useState } from "react";

export type CustomerSortValue = "fullName,asc" | "fullName,desc" | "maintenanceCount,desc";
const SS_SEARCH = "customers.search";
const SS_SORT = "customers.sort";

const DEFAULT_SORT: CustomerSortValue = "fullName,asc";

const VALID_SORTS: CustomerSortValue[] = [
    "fullName,asc",
    "fullName,desc",
    "maintenanceCount,desc",
];

function isCustomerSortValue(value: string | null): value is CustomerSortValue {
    return value !== null &&
        VALID_SORTS.includes(value as CustomerSortValue);
}

export function useCustomerFilters() {
    const [search, setSearch] = useState(() =>
        sessionStorage.getItem(SS_SEARCH) ?? ""
    );

    const [sort, setSort] = useState<CustomerSortValue>(() => {
        const storedSort = sessionStorage.getItem(SS_SORT);

        return isCustomerSortValue(storedSort)
            ? storedSort
            : DEFAULT_SORT;
    });

    useEffect(() => {
        sessionStorage.setItem(SS_SEARCH, search);
    }, [search]);

    useEffect(() => {
        sessionStorage.setItem(SS_SORT, sort);
    }, [sort]);

    return {
        search,
        setSearch,
        sort,
        setSort,
    };
}