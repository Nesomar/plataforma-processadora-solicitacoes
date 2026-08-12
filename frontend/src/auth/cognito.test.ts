import { describe, expect, it, vi } from "vitest";

const authenticateUser = vi.fn();
const confirmRegistration = vi.fn();
const signUpMock = vi.fn();

vi.mock("amazon-cognito-identity-js", () => {
  class CognitoUserPool {
    signUp = signUpMock;
    constructor() {}
  }
  class AuthenticationDetails {
    constructor() {}
  }
  class CognitoUser {
    authenticateUser = authenticateUser;
    confirmRegistration = confirmRegistration;
    constructor() {}
  }
  return { CognitoUserPool, AuthenticationDetails, CognitoUser };
});

describe("login", () => {
  it("resolve com idToken e refreshToken em caso de sucesso", async () => {
    const { login } = await import("./cognito");
    authenticateUser.mockImplementation((_details, callbacks) => {
      callbacks.onSuccess({
        getIdToken: () => ({ getJwtToken: () => "id-token" }),
        getRefreshToken: () => ({ getToken: () => "refresh-token" }),
      });
    });

    await expect(login("a@b.com", "senha")).resolves.toEqual({
      idToken: "id-token",
      refreshToken: "refresh-token",
    });
  });

  it("rejeita quando as credenciais são inválidas", async () => {
    const { login } = await import("./cognito");
    authenticateUser.mockImplementation((_details, callbacks) => {
      callbacks.onFailure(new Error("NotAuthorizedException"));
    });

    await expect(login("a@b.com", "errada")).rejects.toThrow(
      "NotAuthorizedException",
    );
  });
});

describe("signUp", () => {
  it("resolve com o resultado do cadastro em caso de sucesso", async () => {
    const { signUp } = await import("./cognito");
    signUpMock.mockImplementation((_email, _senha, _attrs, _validation, callback) => {
      callback(undefined, { userConfirmed: false });
    });

    await expect(signUp("a@b.com", "SenhaForte123")).resolves.toEqual({
      userConfirmed: false,
    });
  });

  it("rejeita quando o Cognito recusa o cadastro", async () => {
    const { signUp } = await import("./cognito");
    signUpMock.mockImplementation((_email, _senha, _attrs, _validation, callback) => {
      callback(new Error("UsernameExistsException"), undefined);
    });

    await expect(signUp("a@b.com", "SenhaForte123")).rejects.toThrow(
      "UsernameExistsException",
    );
  });
});

describe("confirmSignUp", () => {
  it("resolve quando o código está correto", async () => {
    const { confirmSignUp } = await import("./cognito");
    confirmRegistration.mockImplementation((_code, _forceAlias, callback) => {
      callback(undefined);
    });

    await expect(confirmSignUp("a@b.com", "123456")).resolves.toBeUndefined();
  });

  it("rejeita quando o código está incorreto", async () => {
    const { confirmSignUp } = await import("./cognito");
    confirmRegistration.mockImplementation((_code, _forceAlias, callback) => {
      callback(new Error("CodeMismatchException"));
    });

    await expect(confirmSignUp("a@b.com", "000000")).rejects.toThrow(
      "CodeMismatchException",
    );
  });
});
