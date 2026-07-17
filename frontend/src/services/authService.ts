import axios from 'axios';
import type {LoginRequest, LoginResponse} from "../types/Auth";

const API_URL = '/api/auth';
export const login = async(credantials: LoginRequest): Promise<LoginResponse> => {
    const response = await axios.post<LoginResponse>(`${API_URL}/login`, credantials);
    return response.data;
}