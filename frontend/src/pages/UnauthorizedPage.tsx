import {useNavigate} from 'react-router-dom'

export default function UnauthorizedPage() {
    const navigate = useNavigate();

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">
            <div className="bg-white shadow rounded p-8 w-full max-w-md text-center">
                <h2 className="text-2xl font-bold text-red-600 mb-4">Accès non autorisé</h2>
                <p>Vous n'avez pas les droits nécessaires pour accéder à cette page.</p>
                <button onClick={() => navigate('/login')}> se connecter</button>
            </div>
        </div>
    );

}