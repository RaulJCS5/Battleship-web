import * as React from 'react'
import {
    Link,
} from 'react-router-dom'
import {
    useState,
    useEffect
} from 'react'
import { useCookies } from 'react-cookie'
import { cookie_dotcom_user, cookie_logged_in, cookie_user_session } from '../../fetch/useFetch'
import Typography from '@mui/material/Typography'
import { Box } from '@mui/material'
import Button from '@mui/material/Button';
import LinearIndeterminate from '../Loading/LinearIndeterminated'
import { url_me } from '../../utils/UserConfigs'
import { ProblemOutputModel, SirenHome } from '../../utils/types'

const styleLink = { textDecoration: 'none', color: 'inherit' }
export const styleFlexCenter = { display: 'flex', justifyContent: 'center' }
export function Home() {
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session, cookie_dotcom_user, cookie_logged_in]);
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
            };
            await fetch(url_me, requestOptions)
                .then(async response => {
                    const isJson = response.headers.get('content-type')?.includes('application/problem+json');
                    // check for error response
                    if (!response.ok) {
                        var bodyError = await response.json()
                        if (isJson) {
                            bodyError = bodyError as ProblemOutputModel;
                            setError(bodyError)
                        }
                        setLoading(false)
                        return Promise.reject(bodyError);
                    }
                    if (!cancelled) {
                        const body = await response.json() as SirenHome
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined) {
            setLoading(true)
            doFetch()
        }
        return () => {
            cancelled = true
        }
    }, [cookies, setContent])
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
        if (cookies.logged_in && error != undefined) {
            removeCookie(cookie_user_session)
            removeCookie(cookie_dotcom_user)
            const minutes = 60 * 4 //4 hours
            var date = new Date()
            date.setTime(date.getTime() + (minutes * 60 * 1000))
            setCookie(cookie_logged_in, false, { expires: date, secure: true, sameSite: 'strict', path: '/' })
        }
    }, [error])
    return (
        <Box>
            {
                loading ? (<LinearIndeterminate></LinearIndeterminate>) : (
                    <Box sx={styleFlexCenter}>
                        <Box>
                            <Typography variant='h4'>Home</Typography>
                            {cookies.logged_in == 'true' ? (
                                <Box>
                                    <Button sx={styleFlexCenter} variant="text" color={'success'}>
                                        <Link style={styleLink} to="/createlobby" >Create lobby</Link>
                                    </Button>
                                </Box>
                            ) : (
                                <Box>
                                </Box>
                            )
                            }
                        </Box>
                    </Box>
                )
            }
        </Box>
    )
}