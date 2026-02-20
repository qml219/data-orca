import { useAuth } from "../auth/auth.context";
import { Navigate, Outlet } from "react-router-dom";

export default function ProtectedRoute() {
    const { token } = useAuth();

    if (!token) {
        // do not use navigate within render logic - only inside handler functions
        // navigate("/");
        return <Navigate to="/" replace></Navigate> 
    }

    return <Outlet />
}