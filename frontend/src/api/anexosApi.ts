import { httpClient } from "./httpClient";

export interface Anexo {
  id: string;
  nomeArquivo: string;
}

export const anexosApi = {
  enviar: (arquivo: File) => {
    const formData = new FormData();
    formData.append("arquivo", arquivo);
    return httpClient.post("/api/perfil/anexos", formData);
  },
  listar: () => httpClient.get<Anexo[]>("/api/perfil/anexos").then((r) => r.data),
  // Vai como blob (não URL direta) porque o endpoint exige o Bearer token — <embed src>
  // não manda header custom, então buscamos os bytes autenticados e criamos um object URL.
  visualizar: (id: string) =>
    httpClient.get(`/api/perfil/anexos/${id}`, { responseType: "blob" }).then((r) => r.data as Blob),
};
