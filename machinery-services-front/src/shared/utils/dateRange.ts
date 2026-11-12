import {
    startOfMonth,
    subDays,
    subMonths,
    subYears,
} from "date-fns";

import type { DateRangePreset } from "../types/dateRange.types";

export function getSinceDateFromPreset(preset: DateRangePreset): Date {
    const today = new Date();

    switch (preset) {
        case "thisMonth":
            return startOfMonth(today);

        case "last3Months":
            return subMonths(today, 3);

        case "lastYear":
            return subYears(today, 1);

        case "last30Days":
        case "custom":
            return subDays(today, 30);
    }
}