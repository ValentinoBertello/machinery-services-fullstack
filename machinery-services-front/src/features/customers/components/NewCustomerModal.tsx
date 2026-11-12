import Modal from 'react-bootstrap/Modal'
import Form from 'react-bootstrap/Form';
import { createCustomer } from '../api.ts'
import { useState, type ChangeEvent, type SubmitEvent} from "react";
import { useMutation, useQueryClient } from '@tanstack/react-query';
import '../customer.css';
import { ApiError } from '@/shared/types/error.types.ts';

type NewCustomerModalProps = {
    onClose: () => void;
};
export default function NewCustomerModal({ onClose }: NewCustomerModalProps) {
    const [fullName, setFullName] = useState('');
    const [error, setError] = useState('');
    const queryClient = useQueryClient();

    const mutation = useMutation({
        mutationFn: createCustomer,
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ['customers'] });
            onClose();
        },
        onError: (error) => {
            setError(error instanceof ApiError ? error.message : 'Error al guardar cliente');
        }
    });

    function handleSubmit(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();
        if (!fullName.trim()) {
            setError('Campo obligatorio');
            return;
        }
        mutation.mutate({ fullName });
    }

    function handleChange(e: ChangeEvent<HTMLInputElement>) {
        setFullName(e.target.value);
        if (Boolean(error)) setError('');
    }

    return (
        <Modal
            show
            onHide={onClose}
            backdrop="static"
            animation={false}>
            <Modal.Header closeButton>
                <Modal.Title className='fw-bold'>Crear Cliente</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <form onSubmit={handleSubmit}>
                    <Form.Group className='mb-3'>
                        <Form.Control
                            type='text'
                            value={fullName}
                            onChange={handleChange}
                            placeholder='Nombre completo'
                            isInvalid={Boolean(error)}
                        />
                        <Form.Control.Feedback type='invalid'>
                            {error}
                        </Form.Control.Feedback>
                    </Form.Group>
                    <button type='submit' className='btn btn-success' disabled={mutation.isPending}>
                        {mutation.isPending ? 'Guardando...' : 'Guardar'}
                    </button>
                </form>
            </Modal.Body>
        </Modal>
    );
}
