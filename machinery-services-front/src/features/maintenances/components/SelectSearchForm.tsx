import { Controller, type Control, type FieldPath, type FieldValues } from 'react-hook-form';
import Select from 'react-select';

export type Option<V> = { value: V; label: string };

type SelectFormProps<T extends FieldValues, V> = {
    control: Control<T>;
    name: FieldPath<T>;
    options: Option<V>[];
    isLoading?: boolean;
    placeholder?: string;
    isClearable?: boolean;
};

const selectStyles = {
    control: (provided: any) => ({
        ...provided,
        cursor: 'text',
    }),
};
// Componente reutilizable para select con react-select y RHF
export default function SelectForm<T extends FieldValues, V>({
    control,
    name,
    options,
    isLoading,
    placeholder,
    isClearable = false,
}: SelectFormProps<T, V>) {
    return (
        <Controller
            control={control}
            name={name}
            render={({ field }) => (
                <Select<Option<V>>
                    options={options}
                    isLoading={isLoading}
                    placeholder={placeholder}
                    isClearable={isClearable}
                    styles={selectStyles}
                    // Puente entre react-select y RHF: react-select entrega el objeto
                    // Option completo, pero el form solo guarda el `value` (ej. customerId).
                    onChange={(opt) => field.onChange(opt?.value ?? null)}
                    // Y al revés: reconstruimos la Option a partir del value del form
                    // para que react-select muestre la opción seleccionada.
                    value={options.find(o => o.value === field.value) ?? null}
                />
            )}
        />
    );
}