import { BrowserRouter, Routes, Route, Navigate} from 'react-router-dom';
import LoginPage from './pages/LoginPage';
import PatientList   from './pages/PatientList';
import MyPatientList   from './pages/MyPatientList';

function App() {
  return (
      <BrowserRouter>
        <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/patients" element={<PatientList />} />
            <Route path="/my-patients" element={<MyPatientList />} />
            <Route path="*" element={<Navigate to="/login" />} />
        </Routes>
      </BrowserRouter>
  )
}
export default App
