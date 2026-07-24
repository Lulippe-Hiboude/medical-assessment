import axios from 'axios';
import type {Note, NoteCreateDto} from '../types/Note';

const API_URL = '/api/notes';

const getAuthHeader = () => ({
    headers: {Authorization: `Bearer ${localStorage.getItem('token')}`}
});

export const getNotesByPatientId = async (patientId: number): Promise<Note[]> => {
    const response = await axios.get<Note[]>(`${API_URL}/patient/${patientId}`, getAuthHeader());
    return response.data;
};

export const createNote = async(noteCreateDto: NoteCreateDto): Promise<Note> => {
    const response = await axios.post<Note>(API_URL, noteCreateDto, getAuthHeader());
    return response.data;
}