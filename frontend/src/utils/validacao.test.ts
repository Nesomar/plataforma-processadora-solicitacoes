import { describe, expect, it } from "vitest";
import {
  cepValido,
  cpfValido,
  emailValido,
  mascaraCep,
  mascaraCpf,
  mascaraTelefone,
  telefoneValido,
} from "./validacao";

describe("cpfValido", () => {
  it("aceita CPF com dígito verificador correto", () => {
    expect(cpfValido("529.982.247-25")).toBe(true);
  });

  it("rejeita dígito verificador incorreto", () => {
    expect(cpfValido("529.982.247-26")).toBe(false);
  });

  it("rejeita sequência repetida", () => {
    expect(cpfValido("111.111.111-11")).toBe(false);
  });
});

describe("cepValido", () => {
  it("aceita 8 dígitos com ou sem hífen", () => {
    expect(cepValido("01000-000")).toBe(true);
    expect(cepValido("01000000")).toBe(true);
  });

  it("rejeita quantidade errada de dígitos", () => {
    expect(cepValido("0100000")).toBe(false);
  });
});

describe("telefoneValido", () => {
  it("aceita fixo (10) e celular (11) com ddd", () => {
    expect(telefoneValido("(11) 3333-4444")).toBe(true);
    expect(telefoneValido("11999999999")).toBe(true);
  });

  it("rejeita sem ddd", () => {
    expect(telefoneValido("999999999")).toBe(false);
  });
});

describe("emailValido", () => {
  it("aceita email em formato válido", () => {
    expect(emailValido("cliente@example.com")).toBe(true);
  });

  it("rejeita sem @ ou domínio", () => {
    expect(emailValido("cliente")).toBe(false);
    expect(emailValido("cliente@")).toBe(false);
  });
});

describe("máscaras", () => {
  it("mascaraCpf formata progressivamente", () => {
    expect(mascaraCpf("52998224725")).toBe("529.982.247-25");
  });

  it("mascaraCep formata progressivamente", () => {
    expect(mascaraCep("01000000")).toBe("01000-000");
  });

  it("mascaraTelefone formata celular (11 dígitos)", () => {
    expect(mascaraTelefone("11999999999")).toBe("(11) 99999-9999");
  });

  it("mascaraTelefone formata fixo (10 dígitos)", () => {
    expect(mascaraTelefone("1133334444")).toBe("(11) 3333-4444");
  });
});
