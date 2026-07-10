import {useState} from "react";
import type {PatientCreateDto} from "../types/Patient.ts";
import {createPatient} from "../services/patientService.ts";

interface Props {
    onClose: () => void;
    onCreated:() => void;
}

export default function CreatePatientModal({onClose, onCreated}: Props) {
    const [form, setForm] = useState<PatientCreateDto>({
        firstName: '',
        lastName: '',
        birthDate: '',
        gender: 'M',
        address: '',
        phoneNumber: ''
    });
    const [errors, setErrors] = useState<String[]>([]);
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        console.log('change', e.target.name, e.target.value);
        const {name, value} = e.target;
        setForm(prev => ({...prev,[name]: value}));
    };
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setErrors([]);
        try{
            await createPatient(form);
            onCreated();
            onClose();
        }catch (err: any) {
            if (err.response?.data){
                const data = err.response.data;
                if(Array.isArray(data)){
                    setErrors(data)
                } else if (typeof data === 'object'){
                    setErrors(Object.values(data));
                }else {
                    setErrors([data]);
                }
            }else {
                setErrors(['Error during patient creation']);
            }
        }
    };
    return (

        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">

            <div className="bg-white rounded shadow-lg p-8 w-full max-w-lg">

                <h2 className="text-xl font-bold text-blue-700 mb-6">Nouveau patient</h2>

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">

                    <input

                        name="firstName"

                        placeholder="Prénom *"

                        value={form.firstName}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    />

                    <input

                        name="lastName"

                        placeholder="Nom *"

                        value={form.lastName}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    />

                    <input

                        name="birthDate"

                        type="date"

                        value={form.birthDate}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    />

                    <select

                        name="gender"

                        value={form.gender}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    >

                        <option value="M">Masculin</option>

                        <option value="F">Féminin</option>

                    </select>

                    <input

                        name="address"

                        placeholder="Adresse"

                        value={form.address}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    />

                    <input

                        name="phoneNumber"

                        placeholder="Téléphone"

                        value={form.phoneNumber}

                        onChange={handleChange}

                        className="border rounded px-4 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"

                    />

                    {errors.length > 0 && (

                        <ul className="text-red-500 text-sm">

                            {errors.map((err, i) => <li key={i}>{err}</li>)}

                        </ul>

                    )}

                    <div className="flex justify-end gap-3 mt-2">

                        <button

                            type="button"

                            onClick={onClose}

                            className="px-4 py-2 rounded border hover:bg-gray-100"

                        >

                            Annuler

                        </button>

                        <button

                            type="submit"

                            className="px-4 py-2 rounded bg-blue-700 text-white hover:bg-blue-800"

                        >

                            Créer

                        </button>

                    </div>

                </form>

            </div>

        </div>

    );
}