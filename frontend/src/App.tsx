import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import PatientList from './pages/PatientList';
import PatientDetail from './pages/PatientDetail.tsx';
import UnauthorizedPage from "./pages/UnauthorizedPage.tsx";
import ProtectedRoute from "./components/ProtectedRoute.tsx";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/login" element={<LoginPage/>}/>
                <Route path="/patients" element={
                    <ProtectedRoute role="ORGANIZER"><PatientList/></ProtectedRoute>
                }/>
                <Route path="/my-patients" element={
                    <ProtectedRoute role="DOCTOR"><PatientDetail/></ProtectedRoute>
                }/>
                <Route path="/unauthorized" element={<UnauthorizedPage/>}/>
                <Route path="*" element={<Navigate to="/login"/>}/>
            </Routes>
        </BrowserRouter>
    )
}

export default App
