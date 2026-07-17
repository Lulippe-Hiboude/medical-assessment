import {useState} from 'react';
import type {Patient} from '../types/Patient';
import type {Note} from "../types/Note.ts";
import {getPatientById} from "../services/patientService.ts";
import {getNotesByPatientId} from "../services/NoteService.ts";
import Layout from '../components/Layout';

export default function PatientDetail() {
    const [patientId, setPatientid] = useState('');
    const [patient, setPatient] = useState<Patient | null>(null);
    const [notes, setNotes] = useState<Note[]>([])
    const [error, setError] = useState('');

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        try {
            const id = Number(patientId);
            const [patientData, notesData] = await Promise.all([
                getPatientById(id),
                getNotesByPatientId(id),
            ]);
            setPatient(patientData);
            setNotes(notesData);
        } catch {
            setPatient(null);
            setNotes([]);
            setError('Patient not found');
        }
    };

    return (
        <Layout>
            <div>
                <h1>Mon patient</h1>
                <button onClick={() => {
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                }}>
                    Déconnexion
                </button>
            </div>

            <form onSubmit={handleSearch} className={"mt-4 flex gap-2"}>
                <input type="number"
                       placeholder="ID du patient"
                       value={patientId}
                       onChange={e => setPatientid(e.target.value)}
                />
                <button type="submit" className="bg-blue-700 text-white px-4 py-2 rounded">
                    Rechercher
                </button>
            </form>

            {error && <p className="text-red-600">{error}</p> }

            {patient && (
                <div className="mt-6">
                    <h2 className="text-x1 font-bold">{patient.firstName}  {patient.lastName}</h2>
                    <p>Date de naissance: {patient.birthDate}</p>
                    <p>Genre : {patient.gender}</p>
                    <p>Adresse: {patient.address}</p>
                    <p>Téléphone: {patient.phoneNumber}</p>

                    <h3 className="text-lg font-semibold mt-4">Notes</h3>
                    <ul>
                        {notes.map(note => (
                            <li key={note.id} className="border-b py-2">
                                <p>{note.content}</p>
                                <small className="text-gray-500">{note.createdAt}</small>
                            </li>
                        ))}
                    </ul>
                </div>
            )}
        </Layout>
    );
}