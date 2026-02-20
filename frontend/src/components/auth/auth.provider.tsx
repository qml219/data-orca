import {  useEffect, useState, type ReactNode } from "react";
import React from "react";
import { User } from "./user/user.types";
import { jwtDecode } from 'jwt-decode';
import type { Role, UserDTO } from "./user/user.dto";
import { useNavigate } from "react-router-dom";
import { AuthContext } from "./auth.context";

interface JwtPayloadProps {
                sub: string;
                username: string;
                role: Role;
            }

export function AuthProvider({ children } : { children: React.ReactNode }) {

    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(() => {
        return localStorage.getItem("access_token")
    });
    const [loading, setLoading] = useState<boolean>(false);

    const isAuthenticated = !!token;

    const navigate = useNavigate();

    // Rehydrate user on amount - redecode token to get the user
    useEffect(() => {
        if (!token) return;

        try {
            const payload = jwtDecode<JwtPayloadProps>(token);

            const newUser = new User({
                id: payload.sub,
                username: payload.username,
                role: payload.role,
            });

            setUser(newUser);
        } catch (e) {
            console.error("Invalid token", e);
            setToken(null);
            localStorage.removeItem("access_token");
        }
    }, [token]);


    const login = async (identifier: string, password: string) => {

        setLoading(true);
        try {

            const response = await fetch('http://172.30.107.109:8080/api/auth/login',
                {
                    method: 'POST', 
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(
                        {
                            'identifier' : identifier,
                            'password' : password
                        }
                    )
                }
            ) 

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || `Login Failed ${response.status}`);
            }

            const { accessToken } = await response.json();

            const payload = jwtDecode<JwtPayloadProps>(accessToken);

            const newUser = new User({
                id: payload.sub, 
                username: payload.username,
                role: payload.role as Role
            })

            setUser(newUser);
            setToken(accessToken);
            localStorage.setItem('access_token', accessToken);
            console.log(`Auth Token: ${accessToken}`);
            console.log(`User ${newUser.displayName()} successfully signed in`);
        } catch (error) {
            console.error('Failed with: ', error);
            throw error;
        } finally {
            setLoading(false);
        }
    }

    const logout = () => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("access_token");
        navigate('/');
    }

    const contextValue = {
        user,
        token,
        isAuthenticated,
        login,
        logout,
        loading
    }

    return (
        <AuthContext.Provider value={contextValue}>
            {children}
        </AuthContext.Provider>
    )
}