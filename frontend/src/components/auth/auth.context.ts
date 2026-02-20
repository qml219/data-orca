import { createContext, useContext } from "react";
import type { AuthContextValue } from "./auth.types";

// undefined implies this object wasn't set up correctly / unintended to work then.
export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// customHook to make sure useContext can't be called with an undefined returned and fail unchecked 
export function useAuth(): AuthContextValue {
    const ctx = useContext(AuthContext);

    if (ctx === undefined) {
        throw new Error("useAuth must be used within AuthProvider");
    }
    
    return ctx;
}