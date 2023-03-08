import * as React from 'react'
import { useState, useEffect } from 'react';
import { cookie_logged_in } from '../../fetch/useFetch';
import {
    Link, Navigate,
} from 'react-router-dom'
import { useCookies } from 'react-cookie';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import FormControlLabel from '@mui/material/FormControlLabel';
import Checkbox from '@mui/material/Checkbox';
import Grid from '@mui/material/Grid';
import Box from '@mui/material/Box';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import { url_register } from '../../utils/UserConfigs';
const USER_REGEX = /^[A-z][A-z0-9-_]{0,23}$/;
const PWD_REGEX = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%]).{8,24}$/;
/*
chuck
Chuck123!
*/

function Copyright(props: any) {
    return (
        <Typography variant="body2" color="text.secondary" align="center" {...props}>
            {'Copyright © '}
            <Link to="/">BattleShip</Link>
            {' '}
            {new Date().getFullYear()}
            {'.'}
        </Typography>
    );
}


export function SignUp() {
    const [signUpIn, setSignUp] = useState(false);

    const [user, setUser] = useState('');
    const [validName, setValidName] = useState(false);

    const [pwd, setPwd] = useState('');
    const [validPwd, setValidPwd] = useState(false);

    const [email, setEmail] = useState('');

    const [cookies, setCookie, removeCookie] = useCookies([cookie_logged_in]);

    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            // POST request using fetch with error handling
            const requestOptions = {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    "username": user,
                    "email": email,
                    "password": pwd
                })
            };
            await fetch(url_register, requestOptions)
                .then(async response => {
                    const isJson = response.headers.get('content-type')?.includes('application/json');
                    const body = isJson && await response.json();
                    // check for error response
                    if (!response.ok) {
                        // get error message from body or default to response status
                        const error = (body && body.message) || response.status;
                        setError(error)
                        setLoading(false)
                        return Promise.reject(error);
                    }
                    if (!cancelled) {
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (signUpIn) {
            setLoading(true)
            doFetch()
        }
        return () => {
            cancelled = true
        }
    }, [setContent, signUpIn])

    const [errMsg, setErrMsg] = useState('');

    useEffect(() => {
        setValidName(USER_REGEX.test(user));
    }, [user]);

    useEffect(() => {
        setValidPwd(PWD_REGEX.test(pwd));
    }, [pwd]);

    useEffect(() => {
        setErrMsg('');
    }, [user, pwd, email]);

    const handleSubmit = async (e) => {
        e.preventDefault();
        const v1 = USER_REGEX.test(user);
        const v2 = PWD_REGEX.test(pwd);
        if (!v1 || !v2) {
            setErrMsg('Invalid Entry');
            return;
        }
        try {
            setSignUp(true)
            //clear state and controlled inputs
        } catch (err) {
            if (!err?.response) {
                setErrMsg('No Server Response');
            } else if (err.response?.status === 409) {
                setErrMsg('Username Taken');
            } else {
                setErrMsg('Registration Failed');
            }
        }
    };
    return (
        <>
            {signUpIn || cookies.logged_in == 'true' ? (
                cookies.logged_in == 'true' ? (<Navigate replace to='/'></Navigate>) : (<Navigate replace to='/login'></Navigate>)
            ) : (
                <Container component="main" maxWidth="xs">
                    <CssBaseline />
                    <Box
                        sx={{
                            marginTop: 8,
                            display: 'flex',
                            flexDirection: 'column',
                            alignItems: 'center',
                        }}
                    >
                        <Avatar sx={{ m: 1, bgcolor: 'primary.main' }}>
                            <LockOutlinedIcon />
                        </Avatar>
                        <Typography component="h1" variant="h5">
                            Sign up
                        </Typography>
                        <Box component="form" noValidate onSubmit={handleSubmit} sx={{ mt: 3 }}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        required
                                        fullWidth
                                        id="username"
                                        onChange={(e) => setUser(e.target.value)}
                                        value={user}
                                        label="Username"
                                        name="username"
                                        autoComplete="username"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        required
                                        fullWidth
                                        id="email"
                                        onChange={(e) => setEmail(e.target.value)}
                                        value={email}
                                        label="Email Address"
                                        name="email"
                                        autoComplete="email"
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <TextField
                                        required
                                        fullWidth
                                        name="password"
                                        label="Password"
                                        type="password"
                                        id="password"
                                        onChange={(e) => setPwd(e.target.value)}
                                        value={pwd}
                                        autoComplete="new-password"
                                    />
                                </Grid>
                            </Grid>
                            <Button
                                disabled={!validName || !validPwd ? true : false}
                                type="submit"
                                fullWidth
                                variant="contained"
                                sx={{ mt: 3, mb: 2 }}
                            >
                                Sign Up
                            </Button>
                            <Grid container justifyContent="flex-end">
                                <Grid item>
                                    <Link to="/login">{"Already have an account? Sign In"}</Link>
                                </Grid>
                            </Grid>
                        </Box>
                    </Box>
                    <Copyright sx={{ mt: 8, mb: 4 }} />
                </Container>
            )}
        </>
    );
}