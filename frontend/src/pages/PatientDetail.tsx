import {useState} from 'react';
import type {Patient} from '../types/Patient';
import type {Note, NoteCreateDto} from "../types/Note.ts";
import type {RiskLevel} from "../types/Risk.ts";
import {RISK_LABELS} from "../types/Risk.ts";
import {getPatientById} from "../services/patientService.ts";
import {createNote, getNotesByPatientId} from "../services/NoteService.ts";
import {getPatientRisk} from "../services/riskService.ts";
import Layout from '../components/Layout';

export default function PatientDetail() {
    const [patientId, setPatientId] = useState('');
    const [patient, setPatient] = useState<Patient | null>(null);
    const [notes, setNotes] = useState<Note[]>([])
    const [newNoteContent, setNewNoteContent] = useState('');
    const [risk, setRisk] = useState<RiskLevel | null>(null)
    const [error, setError] = useState('');
    const [noteError, setNoteError] = useState('');
    const [riskError, setRiskError] = useState('');

    const handleSearch = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setRisk(null);
        setRiskError('');
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

    const handleAddNote = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!patient) return;
        setNoteError('');

        try {
            const noteCreateDto: NoteCreateDto = {patientId: patient.id, content: newNoteContent};
            const createdNote = await createNote(noteCreateDto);
            setNotes([createdNote, ...notes]);
            setNewNoteContent('');
        } catch {
            setNoteError('Failed to create note');
        }
    }

    const handleGetRisk = async () => {
        if (!patient) return;
        setRiskError('');
        try {
            const riskData = await getPatientRisk(patient.id);
            setRisk(riskData);
        } catch {
            setRisk(null);
            setRiskError('Failed to fetch risk profile');
        }
    }

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
                       onChange={e => setPatientId(e.target.value)}
                />
                <button type="submit" className="bg-blue-700 text-white px-4 py-2 rounded">
                    Rechercher
                </button>
            </form>

            {error && <p className="text-red-600">{error}</p>}

            {patient && (
                <div className="mt-6">
                    <h2 className="text-x1 font-bold">{patient.firstName} {patient.lastName}</h2>
                    <p>Date de naissance: {patient.birthDate}</p>
                    <p>Genre : {patient.gender}</p>
                    <p>Adresse: {patient.address}</p>
                    <p>Téléphone: {patient.phoneNumber}</p>

                    <button onClick={handleGetRisk} className="bg-blue-700 text-white px-4 py-2 rounded mt-2">
                        Voir le profil de risque
                    </button>
                    {riskError && <p className="text-red-600">{riskError}</p>}
                    {risk && <p> Risque : {RISK_LABELS[risk]} </p>}

                    <h3 className="text-lg font-semibold mt-4">Notes</h3>
                    <ul>
                        {notes.map(note => (
                            <li key={note.id} className="border-b py-2">
                                <p>{note.content}</p>
                                <small className="text-gray-500">{note.createdAt}</small>
                            </li>
                        ))}
                    </ul>
                    <form onSubmit={handleAddNote} className="mt-4">
                        <textarea
                            placeholder="nouvelle note"
                            value={newNoteContent}
                            onChange={e => setNewNoteContent(e.target.value)}
                            className="w-full border p-2"
                        />
                        {noteError && <p className="text-red-600">{noteError}</p>}
                        <button type="submit" className="bg-blue-700 text-white px-4 py-2 rounded">
                            Ajouter la note
                        </button>
                    </form>
                </div>
            )}
        </Layout>
    );
}