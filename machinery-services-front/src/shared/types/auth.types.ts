export type AuthenticationResponse = {
    id: number;
    email: string;
    role: string;
    token: string;
}

export type LoginRequest = {
    email: string;
    password: string;
}