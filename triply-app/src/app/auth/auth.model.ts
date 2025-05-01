export interface User {
  id: number;
  username: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface LoginResponse {
    username: string;
    password: string;
    role: string;
}