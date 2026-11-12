import { subDays } from "date-fns";
import { useEffect, useState } from "react";
import type {
    DateRangeFilterValue,
    DateRangePreset
} from "../types/dateRange.types";
import { getSinceDateFromPreset } from "../utils/dateRange";

type UseDateRangeFiltersOptions = {
    storagePrefix: string;
    allowLastYear: boolean;
}

const VALID_PRESETS: DateRangePreset[] = [
    "thisMonth",
    "last30Days",
    "last3Months",
    "lastYear",
    "custom",
];

function getDateFromLocalStorage(key: string): Date | null {
    const stored = localStorage.getItem(key);
    return stored ? new Date(stored) : null;
}

// Obtiene preset del localStorage y fechas "since" y "until" (si es que el preset es "custom")
function getInitialFilters({
    storagePrefix,
    allowLastYear,
}: UseDateRangeFiltersOptions): DateRangeFilterValue {
    const storedPreset = localStorage.getItem(
        `${storagePrefix}Preset`
    );

    let preset: DateRangePreset =
        storedPreset &&
            VALID_PRESETS.includes(storedPreset as DateRangePreset)
            ? (storedPreset as DateRangePreset)
            : "last30Days";

    if (!allowLastYear && preset === "lastYear") {
        preset = "last30Days";
    }

    const today = new Date();

    if (preset === "custom") {
        return {
            preset,
            since:
                getDateFromLocalStorage(`${storagePrefix}Since`) ??
                subDays(today, 30),
            until:
                getDateFromLocalStorage(`${storagePrefix}Until`) ??
                today,
        };
    }
    // Si no es custom since y until se calculan a partir del preset
    return {
        preset,
        since: getSinceDateFromPreset(preset),
        until: today,
    };
}

/**
 * Maneja el filtro de rango de fechas de una pantalla y lo persiste en
 * localStorage. Guarda siempre el preset; las fechas solo se guardan cuando
 * el preset es "custom" (los presets relativos se recalculan al iniciar).
 */
export function useDateRangeFilters(options: UseDateRangeFiltersOptions) {
    const { storagePrefix } = options;

    const [filters, setFilters] = useState<DateRangeFilterValue>(
        () => getInitialFilters(options)
    );

    useEffect(() => {
        localStorage.setItem(
            `${storagePrefix}Preset`,
            filters.preset
        );

        if (filters.preset !== "custom") return;

        if (filters.since) {
            localStorage.setItem(
                `${storagePrefix}Since`,
                filters.since.toISOString()
            );
        }

        if (filters.until) {
            localStorage.setItem(
                `${storagePrefix}Until`,
                filters.until.toISOString()
            );
        }
    }, [
        storagePrefix,
        filters.preset,
        filters.since,
        filters.until,
    ]);

    return {
        filters,
        setFilters,
    };
}