import axios from 'axios';
import type {RiskLevel} from "../types/Risk.ts";

const API_URL = '/api/risk';

const getAuthHeader = () => ({
    headers: {Authorization: `Bearer ${localStorage.getItem('token')}`}
});

export const getPatientRisk = async (id: number): Promise<RiskLevel> => {
    const response = await axios.get<RiskLevel>(`${API_URL}/${id}`, getAuthHeader());
    return response.data;
}