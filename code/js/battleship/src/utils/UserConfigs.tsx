import {
    useState,
    useEffect,
} from 'react'

export const url_register = '/api/users/register'
//POST
//No Authorization
/*
{
    "username" : "td3",
    "email" : "td3@hotmail.com",
    "password" : "Chuck!123"
}
*/
export const url_auth = '/api/users/auth'
//POST
//No Authorization
/*
{
    "username" : "td3",
    "password" : "Chuck!123"
}
*/
export const url_me = '/api/me'
//GET
//Authorization
export const url_logout = '/api/users/logout'
//POST
//Authorization
const url_recovery = 'http://localhost:8080/users/recovery'
//POST
//No Authorization
/*
{
    "email" : "td1@hotamail.com"
}
*/
export function fetches() {
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    //'Authorization': bearer+'FakeAccessToken',
                },
                /*body: JSON.stringify({
                    //Insert body
                })*/
            };
            await fetch(url_me, requestOptions)
                .then(async response => {
                    const isJson = response.headers.get('content-type')?.includes('application/json');
                    const body = isJson && await response.json();
                    // check for error response
                    if (!response.ok) {
                        const error = (body && body.message) || response.status;
                        return Promise.reject(error);
                    }
                    if (!cancelled) {
                        //get content
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        doFetch()
        return () => {
            cancelled = true
        }
    }, [])
}