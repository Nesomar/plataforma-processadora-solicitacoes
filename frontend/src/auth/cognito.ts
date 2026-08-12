import {
  AuthenticationDetails,
  CognitoUser,
  CognitoUserPool,
} from "amazon-cognito-identity-js";
import type { ISignUpResult } from "amazon-cognito-identity-js";

const userPool = new CognitoUserPool({
  UserPoolId: import.meta.env.VITE_COGNITO_USER_POOL_ID,
  ClientId: import.meta.env.VITE_COGNITO_CLIENT_ID,
  ...(import.meta.env.VITE_COGNITO_ENDPOINT
    ? { endpoint: import.meta.env.VITE_COGNITO_ENDPOINT }
    : {}),
});

export interface LoginResult {
  idToken: string;
  refreshToken: string;
}

/**
 * Envia o ID token (não o access token) como Bearer: o Cognito Authorizer do API
 * Gateway valida a claim `aud`, que só existe no ID token.
 */
export function login(email: string, password: string): Promise<LoginResult> {
  return new Promise((resolve, reject) => {
    const user = new CognitoUser({ Username: email, Pool: userPool });
    const authDetails = new AuthenticationDetails({
      Username: email,
      Password: password,
    });

    user.authenticateUser(authDetails, {
      onSuccess: (session) => {
        resolve({
          idToken: session.getIdToken().getJwtToken(),
          refreshToken: session.getRefreshToken().getToken(),
        });
      },
      onFailure: (err) => reject(err),
    });
  });
}

export function signUp(email: string, password: string): Promise<ISignUpResult> {
  return new Promise((resolve, reject) => {
    userPool.signUp(email, password, [], [], (err, result) => {
      if (err || !result) {
        reject(err);
        return;
      }
      resolve(result);
    });
  });
}

export function confirmSignUp(email: string, code: string): Promise<void> {
  return new Promise((resolve, reject) => {
    const user = new CognitoUser({ Username: email, Pool: userPool });
    user.confirmRegistration(code, true, (err) => {
      if (err) {
        reject(err);
        return;
      }
      resolve();
    });
  });
}
