import { describe, expect, it, vi } from "vitest";

const authenticateUser = vi.fn();

vi.mock("amazon-cognito-identity-js", () => {
  class CognitoUserPool {
    constructor() {}
  }
  class AuthenticationDetails {
    constructor() {}
  }
  class CognitoUser {
    authenticateUser = authenticateUser;
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
