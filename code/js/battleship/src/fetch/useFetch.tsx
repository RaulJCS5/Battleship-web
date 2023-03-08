import {
    useState,
    useEffect,
} from 'react'
import { useCookies } from 'react-cookie'
import { url_check_current_game_phase, url_check_fleet, url_get_user_current_game, url_give_up_game, url_give_up_lobby, url_new_game, url_set_fleet, url_set_shoot } from '../utils/GameConfig'
import { DefaultAnswerModel, Position, PositionStateBoard, ProblemOutputModel, ShipAndLayout, SirenFleetSuccess, SirenGetCurrentGamePhase, SirenGetFleet, SirenGetUserCurrentGame, SirenShoot } from '../utils/types'
export const cookie_user_session = 'user_session'
export const cookie_logged_in = 'logged_in'
export const cookie_dotcom_user = 'dotcom_user'

export function useFetchNewGame(maxShots: string): [content: SirenGetUserCurrentGame | undefined, error: any | ProblemOutputModel | undefined, loading: boolean, handleSubmitNewGame: () => void] {
    const [submitNewGame, setSubmitNewGame] = useState(false)
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
                body: JSON.stringify(
                    {
                        "maxShots": Number(maxShots)
                    }
                )
            };
            await fetch(url_new_game, requestOptions)
                .then(async response => {
                    const isJson = response.headers.get('content-type')?.includes('application/problem+json');
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
                        const body = await response.json() as SirenGetUserCurrentGame
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (maxShots != null && cookies.user_session != undefined && submitNewGame) {
            setLoading(true)
            doFetch()
            setSubmitNewGame(!submitNewGame)
        }
        return () => {
            cancelled = true
        }
    }, [maxShots, submitNewGame, setSubmitNewGame])

    const handleSubmitNewGame = () => {
        setSubmitNewGame(!submitNewGame)
    }

    return [content, error, loading, handleSubmitNewGame]
}

export function useFetchSetFleet(confirmFleet: boolean, shipAndLayout: ShipAndLayout[]): [content: any | undefined, error: any | undefined, loading: boolean] {
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    let gameId = window.localStorage.getItem('gameId');
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
                body: JSON.stringify(
                    shipAndLayout
                )
            };
            await fetch(url_set_fleet(gameId), requestOptions)
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
                        const body = await response.json() as SirenFleetSuccess;
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && gameId != null && confirmFleet) {
            setLoading(true)
            doFetch()
        }
        return () => {
            cancelled = true
        }
    }, [confirmFleet])

    return [content, error, loading]
}

export function useFetchCheckFleet(myBoard: boolean, updateBoards: boolean): [content: Array<Array<PositionStateBoard>> | undefined, error: any | undefined, loading: boolean] {
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
                body: JSON.stringify(
                    {
                        "myBoard": myBoard
                    }
                )
            };
            await fetch(url_check_fleet(window.localStorage.getItem('gameId')), requestOptions)
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
                        const body = await response.json() as SirenGetFleet
                        const bodyContent = body.properties
                        setLoading(false)
                        setContent(bodyContent)
                    }
                })
                .catch(error => {
                    setError(error)
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && window.localStorage.getItem('gameId') != null) {
            setLoading(true)
            doFetch()
        }
        return () => {
            cancelled = true
        }
    }, [myBoard, updateBoards])

    return [content, error, loading]
}

export function useFetchSetShoot(updateShoot: Position): [content: SirenShoot | undefined, error: any | ProblemOutputModel | undefined, loading: boolean] {
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    let gameId = window.localStorage.getItem('gameId');
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
                body: JSON.stringify(
                    {
                        "row": updateShoot.row,
                        "col": updateShoot.col
                    }
                )
            };
            await fetch(url_set_shoot(gameId), requestOptions)
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
                        const body = await response.json() as SirenShoot
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && updateShoot != null) {
            setLoading(true)
            doFetch()
        }
        return () => {
            cancelled = true
        }
    }, [updateShoot])

    return [content, error, loading]
}

export function useFetchCheckCurrentGamePhase(): [content: SirenGetCurrentGamePhase | undefined, error: any | ProblemOutputModel | undefined, loading: boolean, handlePhase: () => void] {
    const [submitPhase, setSubmitPhase] = useState(false)
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    let gameId = window.localStorage.getItem('gameId');
    useEffect(() => {
        //let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
            };
            await fetch(url_check_current_game_phase(gameId), requestOptions)
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
                    //if (!cancelled) {
                    const body = await response.json() as SirenGetCurrentGamePhase
                    setLoading(false)
                    setContent(body)
                    //}
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && gameId != null && submitPhase) {
            setLoading(true)
            doFetch()
            setSubmitPhase(!submitPhase)
        }
        return () => {
            //cancelled = true
        }
    }, [submitPhase, setSubmitPhase])

    const handlePhase = () => {
        setSubmitPhase(!submitPhase)
    }

    return [content, error, loading, handlePhase]
}

export function useFetchGiveUpLobby(): [content: DefaultAnswerModel | undefined, error: any | ProblemOutputModel | undefined, loading: boolean, handleSubmitGiveUpLobby: () => void] {
    const [submitGiveUpLobby, setSubmitGiveUpLobby] = useState(false)
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
            };
            await fetch(url_give_up_lobby, requestOptions)
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
                        const body = response.json() as DefaultAnswerModel
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && submitGiveUpLobby) {
            setLoading(true)
            doFetch()
            setSubmitGiveUpLobby(!submitGiveUpLobby)
        }
        return () => {
            cancelled = true
        }
    }, [submitGiveUpLobby, setSubmitGiveUpLobby])

    const handleSubmitGiveUpLobby = () => {
        setSubmitGiveUpLobby(!submitGiveUpLobby)
    }

    return [content, error, loading, handleSubmitGiveUpLobby]
}

export function useFetchGetUserCurrentGame(): [content: SirenGetUserCurrentGame | undefined, error: any | ProblemOutputModel | undefined, loading: boolean, handleGame: () => void] {
    const [submitGame, setSubmitGame] = useState(false)
    const [loading, setLoading] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    useEffect(() => {
        //let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
            };
            await fetch(url_get_user_current_game, requestOptions)
                .then(async response => {
                    const isJson = response.headers.get('content-type')?.includes('application/problem+json');
                    // check for error response
                    if (!response.ok) {
                        var bodyError = await response.json()
                        if (isJson) {
                            bodyError = bodyError as ProblemOutputModel;
                            setError(bodyError)
                        }
                        window.localStorage.removeItem('gameId');
                        setLoading(false)
                        return Promise.reject(bodyError);
                    }
                    //console.log(cancelled)
                    //if (!cancelled) {
                    setError(undefined)
                    const body = await response.json() as SirenGetUserCurrentGame;
                    setLoading(false)
                    setContent(body)
                    let gameId = window.localStorage.getItem('gameId');
                    if (gameId == null) {
                        window.localStorage.setItem('gameId', `${body.properties.gameId}`);
                        gameId = window.localStorage.getItem('gameId');
                    } else {
                        window.localStorage.removeItem('gameId');
                        window.localStorage.setItem('gameId', `${body.properties.gameId}`);
                        gameId = window.localStorage.getItem('gameId');
                    }
                    //}
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && submitGame) {
            setLoading(true)
            doFetch()
            setSubmitGame(!submitGame)
        }
        return () => {
            //cancelled = true
        }
    }, [submitGame, setSubmitGame])

    const handleGame = () => {
        setSubmitGame(!submitGame)
    }

    return [content, error, loading, handleGame]
}

export function useFetchGiveUpGame(): [content: DefaultAnswerModel[] | undefined, error: any | undefined, loading: boolean, handleGiveUpGame: () => void] {
    const [loading, setLoading] = useState(false)
    const [submitGiveUpGame, setSubmitGiveUpGame] = useState(false)
    const [content, setContent] = useState(undefined)
    const [error, setError] = useState(undefined)
    const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session]);
    useEffect(() => {
        let cancelled = false
        async function doFetch() {
            const requestOptions = {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${cookies.user_session}`,
                },
            };
            await fetch(url_give_up_game, requestOptions)
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
                        const body = await response.json() as DefaultAnswerModel;
                        setLoading(false)
                        setContent(body)
                    }
                })
                .catch(error => {
                    console.error('There was an error!', error);
                });
        }
        if (cookies.user_session != undefined && submitGiveUpGame) {
            setLoading(true)
            doFetch()
            setSubmitGiveUpGame(!submitGiveUpGame)
        }
        return () => {
            cancelled = true
        }
    }, [submitGiveUpGame, setSubmitGiveUpGame])

    const handleGiveUpGame = () => {
        setSubmitGiveUpGame(!submitGiveUpGame)
        window.localStorage.removeItem('gameId');
    }

    return [content, error, loading, handleGiveUpGame]
}