import type { AuthenticationResponse, LoginRequest } from '@/shared/types/auth.types';
import { loginDemo } from '@/demo/demoAuth';
import { request } from './client';

const isDemo = import.meta.env.VITE_DEMO_MODE === 'true';

export function loginRequest( body: LoginRequest): Promise<AuthenticationResponse> {
  if (isDemo) {
    return loginDemo(body);
  }

  return request<AuthenticationResponse>("/auth", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });
}
