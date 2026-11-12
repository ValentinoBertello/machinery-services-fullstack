import Modal from 'react-bootstrap/Modal'
import { useMutation, useQueryClient } from "@tanstack/react-query";
import z from "zod";
import { useForm, type DefaultValues } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import '../maintenance.css';
import { toDate, toStringOffsetDate } from '../utils/date';
import SelectSearchForm from './SelectSearchForm';
import { createMaintenance, updateMaintenance } from '../api';
import type { Maintenance, NewMaintenance } from '../types';
import InputDate from '@/shared/components/InputDate';
import type { ApiError } from '@/shared/types/error.types';
import { useCustomers } from '@/features/customers/hooks/useCustomers';

type MaintenanceFormModalProps = { onClose: () => void; } & (
    | { mode: 'edit'; maintenance: Maintenance }
    | { mode: 'create-with-customer'; customerId: number; fullName: string }
    | { mode: 'create' }
)

// Schema para validar el formulario
const schema = z.object({
    maintenanceDate: z.date({ error: "Debe seleccionar una fecha" }),
    invoiceDate: z.date().nullish(),
    customerId: z.number({ message: 'Debe seleccionar un cliente' }).int().positive(),
    place: z.string().min(1, 'Lugar es obligatorio'),
    model: z.string().min(1, 'El modelo es obligatorio'),
    description: z.string().min(1, 'Observaciones es obligatorio')
});
type SchemaType = z.infer<typeof schema>;

function buildDefaults(maintenance?: Maintenance, customerId?: number): DefaultValues<SchemaType> {
    // Si está el mantenimiento presente (es decir si es para editar)...
    if (maintenance) {
        return {
            maintenanceDate: toDate(maintenance.maintenanceDate),
            invoiceDate: maintenance.invoiceDate ? toDate(maintenance.invoiceDate) : null,
            customerId: maintenance.customerId,
            place: maintenance.place,
            model: maintenance.model,
            description: maintenance.description,
        };
    }
    // Si no es edicion solo se pone default la fecha y el customerId (si está presente)
    return { maintenanceDate: new Date(), customerId: customerId }
}

// Modal de alta/edición. 3 modos: 'create' (libre), 'create-with-customer'
// (cliente pre-cargado) y 'edit' (servicio existente).
export default function MaintenanceFormModal(props: MaintenanceFormModalProps) {
    const { onClose, mode } = props;

    const maintenance = mode === 'edit' ? props.maintenance : undefined;
    const customerId = mode === 'create-with-customer' ? props.customerId : undefined;

    const queryClient = useQueryClient();

    // React Query
    const mutation = useMutation({
        mutationFn: (payload: NewMaintenance) => {
            if (mode === 'edit') {
                const { customerId: _ignored, ...rest } = payload;
                return updateMaintenance(maintenance!.id, rest);
            } else {
                return createMaintenance(payload);
            }
        },
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['maintenances'] });
            onClose();
        }
    });

    // React Hook Form
    const { control, register, handleSubmit, formState: { errors, isDirty } } = useForm<SchemaType>({
        resolver: zodResolver(schema),
        defaultValues: buildDefaults(maintenance, customerId),
    });

    // On Submit
    const onSubmit = (data: z.infer<typeof schema>) => {
        mutation.mutate({
            maintenanceDate: toStringOffsetDate(data.maintenanceDate),
            invoiceDate: data.invoiceDate ? toStringOffsetDate(data.invoiceDate) : null,
            place: data.place,
            model: data.model,
            description: data.description,
            customerId: data.customerId
        });
    };

    // Cargamos clientes (si aplica)
    const { data: customers = [], isLoading, error: customerError } = useCustomers({
        enabled: mode === 'create',
        staleTime: 5 * 60_000,
    });

    // Tipo de dato que "Select React" nos pide con value y label
    type Option = { value: number; label: string };
    const customerOptions: Option[] = customers.map((c) => ({
        value: c.id,
        label: c.fullName,
    }));

    const title = (() => {
        switch (props.mode) {
            case 'edit':
                return `Editar servicio de ${props.maintenance.customerName}`;
            case 'create-with-customer':
                return `Nuevo servicio para ${props.fullName}`;
            case 'create':
                return 'Nuevo Servicio';
        }
    })();

    return (
        <Modal
            show
            onHide={onClose}
            animation={false}
            backdrop="static"
        >
            <Modal.Header closeButton>
                <Modal.Title>{title}</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <form onSubmit={handleSubmit(onSubmit)}>
                    {/* Select de cliente solo si el modeo es 'create' */}
                    {mode === 'create' && (
                        <div className="mb-3">
                            <label>Cliente</label>
                            {/* Helper que crea el Select React con el Controller */}
                            <SelectSearchForm
                                control={control}
                                name="customerId"
                                options={customerOptions}
                                isLoading={isLoading}
                                placeholder="Escribe para buscar tu cliente..."
                                isClearable
                            />
                            {errors.customerId && (
                                <div className="text-danger">{errors.customerId.message}</div>
                            )}
                            {customerError && (
                                <div className="text-danger">No se pudieron cargar los clientes</div>
                            )}
                        </div>
                    )}

                    <div className="mb-3">
                        <label>Fecha</label>
                        <InputDate control={control} name="maintenanceDate" />
                        {errors.maintenanceDate && (<div className="text-danger"> {errors.maintenanceDate.message} </div>)}
                    </div>

                    <div className="mb-3">
                        <label>Fecha Facturación</label>
                        <InputDate control={control} name="invoiceDate" />
                    </div>

                    <div className="mb-3">
                        <label>Lugar</label>
                        <select className="form-select" {...register("place")}>
                            <option value="">...</option>
                            <option value="CAMPO">Campo</option>
                            <option value="TALLER">Taller</option>
                        </select>
                        {errors.place && <div className="text-danger">{errors.place.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label>Modelo</label>
                        <input className="form-control" type="text" {...register("model")} />
                        {errors.model && <div className="text-danger">{errors.model.message}</div>}
                    </div>

                    <div className="mb-3">
                        <label>Observaciones</label>
                        <textarea
                            className="form-control"
                            rows={4}
                            style={{ resize: 'none' }}
                            {...register("description")} />
                        {errors.description && <div className="text-danger">{errors.description.message}</div>}
                    </div>

                    {mutation.isError && (
                        <p style={{ color: 'red' }}>{(mutation.error as ApiError).message}</p>
                    )}

                    <button type='submit' className='btn btn-success' disabled={mutation.isPending || (mode === 'edit' && !isDirty)}>
                        {mutation.isPending
                            ? 'Guardando...'
                            : mode === 'edit'
                                ? 'Guardar cambios'
                                : 'Guardar'}
                    </button>
                </form>
            </Modal.Body>
        </Modal>
    );
}
