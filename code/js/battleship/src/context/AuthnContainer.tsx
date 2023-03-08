import * as React from 'react'
import {
    useState,
    createContext,
    useContext,
} from 'react'

type ContextType = {
    try_logged_in: boolean,
    setTry_logged_in: (v: boolean) => void
}
const AuthContext = createContext<ContextType>({
    try_logged_in: false,
    setTry_logged_in: () => { }
})


export function AuthnContainer({ children }: { children: React.ReactNode }) {
    const [try_logged_in, setTry_logged_in] = useState(false)
    return (
        <AuthContext.Provider value={{try_logged_in: try_logged_in, setTry_logged_in: setTry_logged_in }}>
            {children}
        </AuthContext.Provider>
    )
}

export function useTry_logged_in() {
    return useContext(AuthContext).try_logged_in
}

export function useSetTry_logged_in() {
    return useContext(AuthContext).setTry_logged_in
}