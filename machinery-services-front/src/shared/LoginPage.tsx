import { loginRequest } from "@/shared/api/auth";
import { useAuth } from "@/shared/auth/AuthContext";
import { ApiError } from "@/shared/types/error.types";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { useForm } from "react-hook-form";
import { FaEye, FaEyeSlash } from "react-icons/fa";
import { useNavigate } from "react-router";
import z from "zod";

const schema = z.object({
    email: z.string().min(1, 'Email obligatorio'),
    password: z.string().min(1, 'Constraseña obligatoria'),
})
export default function LoginPage() {
    const { login } = useAuth();
    const navigate = useNavigate();
    const [showPassword, setShowPassword] = useState(false);

    // React Query
    const mutation = useMutation({
        mutationFn: loginRequest,
        onSuccess: (data) => {
            login(data.token);
            navigate("/customers", { replace: true });
        },
    });

    // React Hook Form
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(schema)
    });

    // On Submit
    const onSubmit = (data: z.infer<typeof schema>) => {
        mutation.mutate({
            email: data.email,
            password: data.password
        });
    };

    return (
        <div className="container d-flex justify-content-center align-items-start min-vh-100 pt-5">
            <div className="card shadow-sm" style={{ width: "100%", maxWidth: 380, border: "2px solid #9b9c9c", borderRadius: 10 }}>
                <div className="card-body p-3">
                    <h2 className="mb-4 text-center">Iniciar sesión</h2>

                    <form onSubmit={handleSubmit(onSubmit)} >
                        <div className="mb-3">
                            <label>Email</label>
                            <input className="form-control" type="text" {...register("email")} />
                            {errors.email && <div className="text-danger">{errors.email.message}</div>}
                        </div>
                        <div className="mb-3">
                            <label>Contraseña</label>
                            <div className="input-group">
                                <input className="form-control" type={showPassword ? 'text' : 'password'} {...register("password")} />
                                <button type="button"
                                    className="btn btn-outline-secondary"
                                    tabIndex={-1}
                                    onClick={() => setShowPassword(v => !v)}>
                                    {showPassword ? <FaEyeSlash /> : <FaEye />}
                                </button>
                            </div>
                            {errors.password && <div className="text-danger">{errors.password.message}</div>}
                        </div>

                        {mutation.isError && (
                            <div className="text-danger mb-3" role="alert">
                                {mutation.error instanceof ApiError
                                    ? mutation.error.message
                                    : 'Ocurrió un error inesperado'}
                            </div>
                        )}

                        <button type="submit" className='btn btn-success' disabled={mutation.isPending}>
                            {mutation.isPending
                                ? 'Logueando...'
                                : 'Enviar'}
                        </button>

                    </form>
                </div>
            </div>
        </div>
    );
}