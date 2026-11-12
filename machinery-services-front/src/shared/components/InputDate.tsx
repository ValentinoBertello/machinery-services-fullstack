import { Controller, type Control, type FieldValues, type FieldPath } from 'react-hook-form';
import DatePicker from 'react-datepicker';
import { es } from "date-fns/locale";
import "react-datepicker/dist/react-datepicker.css";

type InputDateProps<T extends FieldValues> = {
    control: Control<T>;
    name: FieldPath<T>;
};

// Componente reutilizable para DatePicker con RHF
export default function InputDate<T extends FieldValues>({ control, name }: InputDateProps<T>) {
    return (
        <Controller
            control={control}
            name={name}
            render={({ field }) => (
                <DatePicker
                    selected={field.value}
                    onChange={field.onChange}
                    onBlur={field.onBlur}
                    dateFormat="dd/MM/yyyy"
                    className="form-control"
                    wrapperClassName="flex-grow-1 input-group"
                    placeholderText="dd/mm/yyyy"
                    locale={es}

                    // No se puede escribir sobre el input
                    onKeyDown={(e) => {
                        e.preventDefault();
                    }}

                />
            )}
        />
    );
}