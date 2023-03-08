import * as React from 'react'
import { useState, useEffect } from 'react';
import { useSetTry_logged_in, useTry_logged_in } from '../../context/AuthnContainer'
import { cookie_dotcom_user, cookie_logged_in, cookie_user_session } from '../../fetch/useFetch';
import {
  Link, Navigate
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
import { url_auth } from '../../utils/UserConfigs';

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


export function SignIn() {
  const [user, setUser] = useState('');
  const [pwd, setPwd] = useState('');
  const try_logged_in = useTry_logged_in()
  const setTry_logged_in = useSetTry_logged_in()
  const [loading, setLoading] = useState(false)
  const [content, setContent] = useState(undefined)
  const [error, setError] = useState(undefined)
  const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session, cookie_logged_in, cookie_dotcom_user]);
  useEffect(() => {
    let cancelled = false
    async function doFetch() {
      // POST request using fetch with error handling
      const requestOptions = {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(
          {
            "username": user,
            "password": pwd
          }
        )
      };
      await fetch(url_auth, requestOptions)
        .then(async response => {
          const isJson = response.headers.get('content-type')?.includes('application/json');
          const body = isJson && await response.json();
          // check for error response
          if (!response.ok) {
            // get error message from body or default to response status
            const minutes = 60 * 4 //4 hours
            var date = new Date()
            date.setTime(date.getTime() + (minutes * 60 * 1000))
            setCookie(cookie_logged_in, false, { expires: date, secure: true, sameSite: 'strict', path: '/' })
            const error = (body && body.message) || response.status;
            setError(error)
            setLoading(false)
            return Promise.reject(error);
          }
          if (!cancelled) {
            const auth = { username: user, password: pwd, accessToken: body.token }
            removeCookie(cookie_user_session)
            removeCookie(cookie_dotcom_user)
            //Set Cookie
            //Set Cookie expire time
            //Set to 60 minutes -> 1 hour
            //localStorage.setItem('dataKey',JSON.stringify('data value'))
            const minutes = 60
            var date = new Date()
            date.setTime(date.getTime() + (minutes * 60 * 1000))
            setCookie(cookie_user_session, body.token, { expires: date, secure: true, sameSite: 'strict', path: '/' })
            setCookie(cookie_dotcom_user, auth.username, { expires: date, secure: true, sameSite: 'strict', path: '/' })
            setCookie(cookie_logged_in, true, { expires: date, secure: true, sameSite: 'strict', path: '/' })
            setLoading(false)
            setContent(body)
          }
        })
        .catch(error => {
          console.error('There was an error!', error);
        })
        .finally(()=>{
          setTry_logged_in(false)
        });
    }
    if (try_logged_in && cookies.user_session == undefined) {
      setLoading(true)
      doFetch()
    }
    return () => {
      cancelled = true
    }
  }, [user, pwd, setContent, try_logged_in])
  const [errMsg, setErrMsg] = useState('');
  useEffect(() => {
    if (cookies.user_session == undefined) {
      const minutes = 60 * 4 //4 hours
      var date = new Date()
      date.setTime(date.getTime() + (minutes * 60 * 1000))
      setCookie(cookie_logged_in, false, { expires: date, secure: true, sameSite: 'strict', path: '/' })
    } else if (cookies.logged_in == undefined) {
      const minutes = 60
      var date = new Date()
      date.setTime(date.getTime() + (minutes * 60 * 1000))
      setCookie(cookie_logged_in, true, { expires: date, secure: true, sameSite: 'strict', path: '/' })
    }
  }, [cookies])
  useEffect(() => {
    setErrMsg('');
  }, [user, pwd]);
  const handleSubmit = async (e) => {
    e.preventDefault();
    if (cookies.user_session == undefined) {
      try {
        //setLogged_in(true)
        setTry_logged_in(true)
      } catch (err) {
        if (!err?.response) {
          setErrMsg('No Server Response');
        } else if (err.response?.status === 400) {
          setErrMsg('Missing Username or Password');
        } else if (err.response?.status === 401) {
          setErrMsg('Unauthorized');
        } else {
          setErrMsg('Sign In Failed');
        }
      }
    }
  };

  return (
    <>
      {cookies.logged_in == 'true' ? (
        <Navigate replace to='/'></Navigate>
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
              Sign in
            </Typography>
            <Box component="form" onSubmit={handleSubmit} noValidate sx={{ mt: 1 }}>
              <TextField
                margin="normal"
                required
                fullWidth
                id="username"
                onChange={(e) => setUser(e.target.value)}
                value={user}
                label="Username"
                name="username"
                autoComplete="username"
                autoFocus
              />
              <TextField
                margin="normal"
                required
                fullWidth
                name="password"
                label="Password"
                type="password"
                id="password"
                onChange={(e) => setPwd(e.target.value)}
                value={pwd}
                autoComplete="current-password"
              />
              <FormControlLabel
                control={<Checkbox value="remember" color="primary" />}
                label="Remember me"
              />
              <Button
                type="submit"
                fullWidth
                variant="contained"
                sx={{ mt: 3, mb: 2 }}
              >
                Sign In
              </Button>
              <Grid container>
                <Grid item>
                  <Link to="/register">{"Don't have an account? Sign Up"}</Link>
                </Grid>
              </Grid>
            </Box>
          </Box>
          <Copyright sx={{ mt: 8, mb: 4 }} />
        </Container>
      )
      }
    </>
  );
}