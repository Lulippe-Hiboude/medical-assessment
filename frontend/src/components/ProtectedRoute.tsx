import { Navigate } from 'react-router-dom';

import { isAuthenticated, getUserRole } from '../utils/authUtil';

interface ProtectedRouteProps {
    role: string;
    children: React.ReactElement;
}

export default function ProtectedRoute({ role, children }: ProtectedRouteProps) {
    if (!isAuthenticated()) {
        return <Navigate to="/login" replace />;
    }

    if (getUserRole() !== role) {
        return <Navigate to="/unauthorized" replace />;
    }

    return children;
}