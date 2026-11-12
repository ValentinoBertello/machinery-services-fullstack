import type {
    AuthenticationResponse,
    LoginRequest,
} from '@/shared/types/auth.types';
import { ApiError } from '@/shared/types/error.types';

export const demoUser = {
    id: 1,
    email: 'demo@maquinaria.com',
    password: 'demo123',
    role: 'ADMIN',
};

export async function loginDemo(
    credentials: LoginRequest,
): Promise<AuthenticationResponse> {
    const isValidEmail =
        credentials.email.trim().toLowerCase() === demoUser.email.toLowerCase();
    const isValidPassword = credentials.password === demoUser.password;

    if (!isValidEmail || !isValidPassword) {
        throw new ApiError(401, 'Email o contraseña incorrectos');
    }

    return {
        id: demoUser.id,
        email: demoUser.email,
        role: demoUser.role,
        token: 'demo-session-token',
    };
}
