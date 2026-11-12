import { Navigate, Outlet, useLocation } from "react-router";
import { useAuth } from "./AuthContext";

export default function RequireAuth() {
    const { isAuth } = useAuth();
    const location = useLocation();

    if (!isAuth) {
        return (
            <Navigate to="/login" replace state={{ from: location }} />
        );
    }

    return <Outlet />
}