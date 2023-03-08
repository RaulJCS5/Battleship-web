import * as React from 'react'
import {
    Route,
    Routes,
} from 'react-router-dom'
import { CreateLobby } from './screens/Lobby/CreateLobby'
import DefineFleet from './screens/Lobby/DefineFleet'
import { Home } from './screens/AppBar/Home'
import CssBaseline from '@mui/material/CssBaseline';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import { PaletteMode } from '@mui/material';
import { } from '@mui/material/colors';
import { SignIn } from './screens/AppBar/SignIn'
import { SignUp } from './screens/AppBar/SignUp'
import { ColorModeContext } from './screens/AppBar/DarkMode'
import ResponsiveAppBar from './screens/AppBar/ResponsiveAppBar'
import { Me } from './screens/AppBar/Me'
import { StickyHeadTableLeaderboard } from './screens/AppBar/StickyHeadTableLeaderboard'
import { StickyHeadTableUsersGameHistory } from './screens/AppBar/StickyHeadTableUsersGameHistory'
import {
    useState,
    useMemo,
    useEffect,
} from 'react'
import { Play } from './screens/Lobby/Play'

const getDesignTokens = (mode: PaletteMode) => ({
    palette: {
      mode,
      ...(mode === 'light'
        ? {
            // palette values for light mode
          }
        : {
            // palette values for dark mode
          }),
    },
  });

export function App() {
    const [mode, setMode] = useState<'light' | 'dark'>('light');
    const colorMode = useMemo(
        () => ({
            toggleColorMode: () => {
                setMode((prevMode) => prevMode === 'light' ? 'dark' : 'light')
                localStorage.setItem('data-theme', mode === 'light' ? 'dark' : 'light');
            },
        }),
        [mode],
    );
    useEffect(() => {
        let theme = window.localStorage.getItem('data-theme');
        if (theme == null) {
            window.localStorage.setItem('data-theme', 'light');
            theme = window.localStorage.getItem('data-theme');
        }
        setMode(theme == 'light' ? 'light' : 'dark');
    }, [])
    const theme = useMemo(
        () => createTheme(getDesignTokens(mode)), [mode]);
    return (
        <ColorModeContext.Provider value={colorMode}>
            <ThemeProvider theme={theme}>
                <ResponsiveAppBar />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route
                        path="/register"
                        element={<SignUp />}
                    />
                    <Route
                        path="/login"
                        element={<SignIn />}
                    />
                    <Route
                        path="/createlobby"
                        element={<CreateLobby />}
                    />
                    <Route
                        path="/leaderboard"
                        element={<StickyHeadTableLeaderboard />}
                    />
                    <Route
                        path="/usersgamehistory"
                        element={<StickyHeadTableUsersGameHistory />}
                    />
                    <Route
                        path="/me"
                        element={<Me />}
                    />
                </Routes>
                <CssBaseline />
            </ThemeProvider>
        </ColorModeContext.Provider>
    );
}