import { ApiError } from "@/shared/types/error.types";

const BASE_URL = 'http://localhost:8080';

// Método helper para crear peticiones pasando token
export async function request<T>(path: string, options?: RequestInit): Promise<T> {
    const token = localStorage.getItem('token');
    const headers = new Headers(options?.headers);

    // Si es login no pasamos token
    if (token && path !== "/auth") {
        headers.set("Authorization", `Bearer ${token}`);
    }

    const response = await fetch(BASE_URL + path, {
        ...options,
        headers,
    });

    if (!response.ok) {
        // Si es error 401 directo al login
        if (response.status === 401 && path !== '/auth') {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        throw await buildApiError(response);
    }

    return (await response.json()) as T;
}

// Construye un ApiError a partir del mensaje de error que viene del backend
async function buildApiError(response: Response): Promise<ApiError> {
    const body = await response.json().catch(() => null);
    const message = body?.message ?? `Error ${response.status}`;
    return new ApiError(response.status, String(message));
}