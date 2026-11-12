export type DateRangePreset =
    | "thisMonth"
    | "last30Days"
    | "last3Months"
    | "lastYear"
    | "custom";

export type DateRangeFilterValue = {
    preset: DateRangePreset;
    since: Date | null;
    until: Date | null;
}
