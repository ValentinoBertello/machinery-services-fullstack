import type { AuthenticationResponse, LoginRequest } from '@/shared/types/auth.types';
import { request } from './client';

export function loginRequest( body: LoginRequest): Promise<AuthenticationResponse> {
  return request<AuthenticationResponse>("/auth", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
}