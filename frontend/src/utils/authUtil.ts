import {jwtDecode} from 'jwt-decode';

interface JwtPayload {
    sub: string;
    roles: string[];
}

export function getToken(): string | null {
    return localStorage.getItem('token');
}

export function isAuthenticated(): boolean {
    return getToken() !== null;
}

export function getUserRole(): string | null {
    const token = getToken();

    if (!token) return null;

    try {
        return jwtDecode<JwtPayload>(token).roles[0] ?? null;
    } catch {
        return null;
    }
}