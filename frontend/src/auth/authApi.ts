import { httpClient } from "../api/httpClient";

export interface SignupResult {
  clienteId: string;
}

export interface LoginResult {
  token: string;
}

export function signUp(email: string, password: string): Promise<SignupResult> {
  return httpClient.post<SignupResult>("/api/auth/signup", { email, password }).then((r) => r.data);
}

export function login(email: string, password: string): Promise<LoginResult> {
  return httpClient.post<LoginResult>("/api/auth/login", { email, password }).then((r) => r.data);
}
