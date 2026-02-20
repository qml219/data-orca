import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import "./Login.css";
import HomePage from "@/components/HomePage/HomePage";
// import { useAuth } from '@/contexts/AuthContext';
import Logo from "@/assets/orca.svg";
import { useAuth } from '../auth/auth.context';

function Login() {

    const [userIdentifier, setUserIdentifier] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    // const [loading, setLoading] = useState(false);
    // const { setToken } = useAuth();

    const { user, isAuthenticated, login, loading } = useAuth() 

    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        setError('');

        try {
            await login(userIdentifier, password);
            navigate('/home');
        } catch (err) {
            const message = err instanceof Error ? err.message : 'Login failed';
            setError(message);
            console.error(err);
        }
    }

    return (
        <>
            <form onSubmit={handleSubmit} className="login-form">
                <h1>Login</h1>

                <div className="form-group">
                    <label htmlFor="identifier">Username or Email:</label>
                    <input
                        id="identifier"
                        type="text"
                        value={userIdentifier}
                        onChange={(e) => setUserIdentifier(e.target.value)}
                        className="input-field"
                        placeholder="Enter your email or username"
                        required
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="password">Password:</label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="input-field"
                        placeholder="Enter your password"
                        required
                    />
                </div>

                {error && <p className="error-message">{error}</p>}

                <button type="submit" disabled={loading} className="submit-btn">
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>
            <div className="login-brand">
                <h1>
                    DataOrca &mdash; your personal data oracle! 
                    <img src={Logo} className="logo" alt="OrcaLogo"></img>
                </h1>
            </div>
        </>
    )
}

export default Login;
