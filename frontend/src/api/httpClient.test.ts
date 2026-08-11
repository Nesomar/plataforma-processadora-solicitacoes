import { beforeEach, describe, expect, it } from "vitest";
import type { InternalAxiosRequestConfig } from "axios";
import { AxiosHeaders } from "axios";
import { attachAuthHeader } from "./httpClient";
import { tokenStore } from "../auth/tokenStore";

function baseConfig(): InternalAxiosRequestConfig {
  return { headers: new AxiosHeaders() } as InternalAxiosRequestConfig;
}

describe("attachAuthHeader", () => {
  beforeEach(() => {
    tokenStore.clear();
  });

  it("anexa Bearer token quando presente", () => {
    tokenStore.set("token-abc");
    const config = attachAuthHeader(baseConfig());
    expect(config.headers.Authorization).toBe("Bearer token-abc");
  });

  it("não anexa header quando não há token", () => {
    const config = attachAuthHeader(baseConfig());
    expect(config.headers.Authorization).toBeUndefined();
  });
});
