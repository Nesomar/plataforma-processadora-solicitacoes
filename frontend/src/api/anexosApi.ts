import { httpClient } from "./httpClient";

export const anexosApi = {
  enviar: (arquivo: File) => {
    const formData = new FormData();
    formData.append("arquivo", arquivo);
    return httpClient.post("/api/perfil/anexos", formData);
  },
};
