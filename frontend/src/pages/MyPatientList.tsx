import Layout from '../components/Layout';

export default function MyPatientList() {
    return (
        <Layout>
            <div>
                <div>
                    <h1>Mes Patients</h1>
                    <button onClick={() => {
                        localStorage.removeItem('token');
                        window.location.href = '/login';
                    }}>
                        Déconnexion
                    </button>
                </div>
            </div>
        </Layout>
    );
}