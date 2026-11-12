import { useQueryClient } from "@tanstack/react-query";
import { createContext, useContext, useState, type ReactNode } from "react";
import { useNavigate } from "react-router";

    type AuthContextType = {
        token: string | null;
        isAuth: boolean;
        login: (newToken: string) => void;
        logout: () => void;
    }

    // Canal global para compartir datos entre componentes sin usar props
    const AuthContext = createContext<AuthContextType | null>(null);

    // AuthProvider sera un wrapper para que todo lo que esté adentro acceda al context
    export function AuthProvider({ children }: { children: ReactNode }) {
        const navigate = useNavigate();
        const queryClient = useQueryClient();
        const [token, setToken] = useState<string | null>(() => localStorage.getItem('token'));

        const login = (newToken: string) => {
            localStorage.setItem('token', newToken);
            setToken(newToken);
        }

        const logout = () => {
            localStorage.removeItem('token');
            setToken(null);
            queryClient.clear();
            navigate('/login', { replace: true });
        };

        // <AuthContext.Provider> publica el contexto para sus hijos
        return (
            <AuthContext.Provider value={{ token, isAuth: !!token, login, logout }}>
                {children}
            </AuthContext.Provider>
        );
    }

    export const useAuth = () => {
        const ctx = useContext(AuthContext);
        if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider');
        return ctx;
    }