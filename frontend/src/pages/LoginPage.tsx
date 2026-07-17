import {useState} from 'react';
import {login} from '../services/authService';
import {jwtDecode} from 'jwt-decode';

interface JwtPayload {
    sub: string;
    roles: string[];
}

export default function LoginPage() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const response = await login({username, password});
            localStorage.setItem('token', response.token);
            const decodedToken = jwtDecode<JwtPayload>(response.token);
            const userRole = decodedToken.roles[0];

            if (userRole === 'ORGANIZER') {
                window.location.href = '/patients';
            } else if (userRole === 'DOCTOR') {
                window.location.href = '/my-patients';
            }
        } catch (err) {
            setError('Invalid username or password');
        }
    };
    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">

            <div className="bg-white shadow rounded p-8 w-full max-w-md">

                <h2 className="text-2xl font-bold text-blue-700 mb-6 text-center">
                    Medical Assessment
                </h2>

                <form onSubmit={handleSubmit}>
                    <h2>Connexion</h2>

                    <input
                        type="text"
                        placeholder="Nom d'utilisateur"
                        value={username}
                        onChange={e => setUsername(e.target.value)}
                    />

                    <input
                        type="password"
                        placeholder="Mot de passe"
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                    />

                    {error && <p>{error}</p>}

                    <button type="submit">Se connecter</button>
                </form>
            </div>
        </div>
    );
}
