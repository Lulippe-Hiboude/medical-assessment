import {useEffect, useState} from 'react';
import type {Patient} from '../types/Patient';
import {getAllPatients} from '../services/patientService';
import Layout from '../components/Layout';
import CreatePatientModal from '../components/CreatePatientModal.tsx';
import UpdatePatientModal from "../components/UpdatePatientModal.tsx";

export default function PatientList() {
    const [patients, setPatients] = useState<Patient[]>([]);
    const [error, setError] = useState('');
    const [showModal, setShowModal] = useState(false);
    const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);

    const loadPatients = () => {
        getAllPatients()
            .then(setPatients)
            .catch(() => setError('Failed to fetch patients'));
    };

    useEffect(() => {
        loadPatients();
    }, []);

    if (error) return <p>{error}</p>;

    return (
        <Layout>
            <div>
                <h1>Liste des patients</h1>

                <button
                    onClick={() => setShowModal(true)}
                    className="bg-blue-700 text-white px-4 py-2 rounded hover:bg-blue-800"
                >
                    Ajouter un patient
                </button>

                <button onClick={() => {
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                }}>
                    Déconnexion
                </button>
            </div>
            <table className="w-full bg-white shadow-md rounded text-sm">
                <thead className="bg-blue-700 text-white">
                <tr>
                    <th className="p-4 text-left whitespace-nowrap">Nom</th>
                    <th className="p-4 text-left whitespace-nowrap">Prénom</th>
                    <th className="p-4 text-left whitespace-nowrap">Date de naissance</th>
                    <th className="p-4 text-left whitespace-nowrap">Genre</th>
                    <th className="p-4 text-left whitespace-nowrap">Adresse</th>
                    <th className="p-4 text-left whitespace-nowrap">Téléphone</th>
                    <th className="p-4 text-left whitespace-nowrap"> Actions</th>
                </tr>
                </thead>
                <tbody>
                {patients.map(p => (
                    <tr key={p.id} className="border-b hover:bg-gray-50">
                        <td className="p-4">{p.lastName}</td>
                        <td className="p-4">{p.firstName}</td>
                        <td className="p-4">{p.birthDate}</td>
                        <td className="p-4">{p.gender}</td>
                        <td className="p-4">{p.address}</td>
                        <td className="p-4">{p.phoneNumber}</td>
                        <td className="p-4">
                            <button
                                onClick={() => setSelectedPatient(p)}
                                className="bg-yellow-500 text-white px-3 py-1 rounded hover:bg-yellow-600 text-xs"
                            > Modifier
                            </button>
                        </td>
                    </tr>
                    ))}
                </tbody>
            </table>
            {showModal && (
            <CreatePatientModal
                onClose={() => setShowModal(false)}
                onCreated={loadPatients}
            />
            )}
            {selectedPatient && (
            <UpdatePatientModal
                patient={selectedPatient}
                onClose={() => setSelectedPatient(null)}
                onUpdated={loadPatients}
            />
            )}
        </Layout>
);
}