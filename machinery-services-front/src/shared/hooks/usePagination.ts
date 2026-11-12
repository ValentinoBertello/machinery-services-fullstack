import { useEffect, useState } from "react";

const PAGE_SIZE_OPTIONS = [8, 15, 20, 30];

type Options = {
    storageKey: string;
    defaultPageSize?: number;
}

type UsePaginationResult = {
    page: number;
    pageSize: number;
    setPage: (page: number) => void;
    changePageSize: (size: number) => void;
};

export function usePagination({ storageKey, defaultPageSize = 8 }: Options): UsePaginationResult {
    const [page, setPage] = useState(0);

    const [pageSize, setPageSize] = useState<number>(() => {
        const storedValue = Number(localStorage.getItem(storageKey));
        return PAGE_SIZE_OPTIONS.includes(storedValue)
            ? storedValue : defaultPageSize;
    });

    useEffect(() => {
        localStorage.setItem(storageKey, String(pageSize));
    }, [pageSize, storageKey]);

    const changePageSize = (size: number) => {
        setPageSize(size);
        setPage(0);
    };

    return { page, pageSize, setPage, changePageSize };
}