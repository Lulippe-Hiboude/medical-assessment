import axios from 'axios';
import type {Patient, PatientCreateDto, PatientUpdateDto} from '../types/Patient';

const API_URL = '/api/patient';

//TODO Need to user a generic class to avoid duplication?
const getAuthHeader = () => ({
    headers: {Authorization: `Bearer ${localStorage.getItem('token')}`}
});

export const getAllPatients = async (): Promise<Patient[]> => {
    const response = await axios.get<Patient[]>(API_URL, getAuthHeader());
    return response.data;
};

export const getPatientById = async (id: number): Promise<Patient> => {
    const response = await axios.get<Patient>(`${API_URL}/${id}`, getAuthHeader());
    return response.data;
}

export const createPatient = async (patient: PatientCreateDto): Promise<Patient> => {
    const response = await axios.post<Patient>(API_URL, patient, getAuthHeader());
    return response.data;
}

export const updatePatient = async (id: number, data: PatientUpdateDto): Promise<Patient> => {
    const response = await axios.patch<Patient>(`${API_URL}/${id}`, data, getAuthHeader());
    return response.data;
}