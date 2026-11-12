import DatePicker from "react-datepicker";
import type { DateRangeFilterValue, DateRangePreset } from "../types/dateRange.types";
import { getSinceDateFromPreset } from "../utils/dateRange";
import { es } from "date-fns/locale";
import { format } from "date-fns";
import "react-datepicker/dist/react-datepicker.css";
import "./DateRangeFilters.css";

const PRESETS: {
    id: DateRangePreset;
    label: string;
}[] = [
        { id: "thisMonth", label: "Este mes" },
        { id: "last30Days", label: "30 días" },
        { id: "last3Months", label: "3 meses" },
        { id: "lastYear", label: "Último año" },
        { id: "custom", label: "Personalizado" },
    ];

type DateRangeFiltersProps = {
    value: DateRangeFilterValue;
    onChange: (value: DateRangeFilterValue) => void;
    allowLastYear?: boolean;
};

export default function DateRangeFilters({
    value,
    onChange,
    allowLastYear = true,
}: DateRangeFiltersProps) {
    const { preset, since, until } = value;

    const visiblePresets = allowLastYear
        ? PRESETS
        : PRESETS.filter(({ id }) => id !== "lastYear");

    const handlePresetClick = (nextPreset: DateRangePreset) => {
        if (nextPreset === "custom") {
            onChange({
                ...value,
                preset: nextPreset,
            });

            return;
        }

        onChange({
            preset: nextPreset,
            since: getSinceDateFromPreset(nextPreset),
            until: new Date(),
        });
    };

    return (
        <div className="date-range-filters d-flex flex-wrap align-items-center justify-content-between gap-3 mb-3">
            <div className="date-range-filters__presets" role="group" aria-label="Rango de fechas">
                {visiblePresets.map(({ id, label }) => {
                    const active = preset === id;
                    return (
                        <button
                            key={id}
                            type="button"
                            className={`date-range-filters__preset ${active ? "is-active" : ""
                                }`}
                            aria-pressed={active}
                            onClick={() => handlePresetClick(id)}
                        >
                            {label}
                        </button>
                    );
                })}
            </div>

            {preset !== "custom" && since && until && (
                <span className="date-range-filters__label">
                    {format(since, "dd/MM/yyyy")} a {format(until, "dd/MM/yyyy")}
                </span>
            )}

            {/* Inputs de fecha: solo se muestran cuando el preset es "Personalizado" */}
            {preset === "custom" && (
                <div className="date-range-filters__inputs">
                    <label className="date-range-filters__field">
                        <span>Desde</span>
                        <DatePicker
                            selected={since}
                            // Actualiza fecha "desde" manteniendo el resto de los filtros
                            onChange={(d: Date | null) => onChange({ ...value, since: d })}
                            dateFormat="dd/MM/yyyy"
                            className="form-control form-control-sm"
                            placeholderText="dd/mm/aaaa"
                            locale={es}

                            // No se puede escribir sobre el input
                            onKeyDown={(e) => {
                                e.preventDefault();
                            }}
                        />
                    </label>
                    <label className="date-range-filters__field">
                        <span>Hasta</span>
                        <DatePicker
                            selected={until}
                            onChange={(d: Date | null) => onChange({ ...value, until: d })}
                            dateFormat="dd/MM/yyyy"
                            className="form-control form-control-sm"
                            placeholderText="dd/mm/aaaa"
                            locale={es}

                            // Actualiza fecha "hasta" manteniendo el resto de los filtros
                            onKeyDown={(e) => {
                                e.preventDefault();
                            }}
                        />
                    </label>
                </div>
            )}
        </div>
    );
}