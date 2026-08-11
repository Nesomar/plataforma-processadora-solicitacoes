import axios from "axios";
import type { InternalAxiosRequestConfig } from "axios";
import { tokenStore } from "../auth/tokenStore";

export function attachAuthHeader(
  config: InternalAxiosRequestConfig,
): InternalAxiosRequestConfig {
  const token = tokenStore.get();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}

export const httpClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
});

httpClient.interceptors.request.use(attachAuthHeader);

// Token expirado/inválido: gateway ou backend respondem 401. Sem isso o usuário
// fica preso numa página que só falha silenciosamente (ver client-auth spec).
httpClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      tokenStore.clear();
      window.location.assign("/login");
    }
    return Promise.reject(error);
  },
);
