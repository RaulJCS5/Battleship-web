import * as React from 'react'
import { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import { Button, Typography } from '@mui/material';
import { useCookies } from 'react-cookie';
import { cookie_logged_in, useFetchCheckFleet, useFetchSetShoot, useFetchNewGame, useFetchGiveUpLobby, useFetchGetUserCurrentGame, useFetchCheckCurrentGamePhase, useFetchGiveUpGame } from '../../fetch/useFetch';
import { Navigate, useNavigate } from 'react-router-dom';
import { styleFlexCenter } from '../AppBar/Home';
import DefineFleet from './DefineFleet';
import { Play } from './Play';
import CircularIndeterminate from '../Loading/CircularIndeterminate';
import { Position } from '../../utils/types';

export function CreateLobby() {
    const [cookies, setCookie, removeCookie] = useCookies([cookie_logged_in]);
    const [maxShots, setMaxShots] = useState('')

    const [updateShoot, setUpdateShoot] = useState<Position>(null)
    const [updateBoards, setUpdateBoards] = useState(false)
    const [contentMyBoard, errorMyBoard, loadingMyBoard] = useFetchCheckFleet(true, updateBoards)
    const [contentOpponentBoard, errorOpponentBoard, loadingOpponentBoard] = useFetchCheckFleet(false, updateBoards)
    const [contentSetShoot, errorSetShoot, loadingSetShoot] = useFetchSetShoot(updateShoot)
    const [contentNewGame, errorNewGame, loadingNewGame, handleNewGame] = useFetchNewGame(maxShots)
    const [contentGiveUpLobby, errorGiveUpLobby, loadingGiveUpLobby, handleGiveUpLobby] = useFetchGiveUpLobby()
    const [contentGame, errorGame, loadingGame, handleGame] = useFetchGetUserCurrentGame()
    const [contentPhase, errorPhase, loadingPhase, handlePhase] = useFetchCheckCurrentGamePhase()
    const [contentGiveUpGame, errorGiveUpGame, loadingGiveUpGame, handleGiveUpGame] = useFetchGiveUpGame()

    const [time, setTime] = useState(0)
    useEffect(() => {
        const timeoutId = setTimeout(() => {
            if (time === 86400) {
                clearTimeout(timeoutId)
                return;
            }
            setTime(time + 1)
            if (contentPhase != undefined && (contentPhase.properties.name == 'PLAYER_ONE_WON' || contentPhase.properties.name == 'PLAYER_TWO_WON')) {
                console.log('WINNER')
            }
            else {
                handleGame()
                handlePhase()
            }
        }, 1000);
        return () => clearTimeout(timeoutId)
    }, [time]);

    const navigate = useNavigate()
    function handleClickLeaveGame() {
        navigate('/')
        window.localStorage.removeItem('gameId');
    }

    return (
        <Box sx={styleFlexCenter}>
            {cookies.logged_in == 'true' ?
                (
                    <Box>
                        {time}
                        <Box>
                            {window.localStorage.getItem('gameId') != null || contentPhase != undefined && (contentPhase.properties.name == 'PLAYER_ONE_WON' || contentPhase.properties.name == 'PLAYER_TWO_WON') ? (
                                <Box>
                                    {
                                        <Box>
                                            {contentPhase == undefined ? (
                                                <CircularIndeterminate></CircularIndeterminate>
                                            ) : (
                                                <Box>
                                                    {contentPhase.properties.name == 'LAYOUT' ? (
                                                        <Box>
                                                            <Box>{contentPhase.properties.name}</Box>
                                                            <DefineFleet></DefineFleet>
                                                            <Box>
                                                                <Button onClick={handleGame}>Game</Button>
                                                                <Button onClick={handlePhase}>Phase</Button>
                                                                <Button onClick={handleGiveUpGame}>Give up</Button>
                                                            </Box>
                                                        </Box>
                                                    ) : contentPhase.properties.name == 'SHOOTING_PLAYER_ONE' || contentPhase.properties.name == 'SHOOTING_PLAYER_TWO' ? (
                                                        <Box>
                                                            <Box>{contentPhase.properties.name}</Box>
                                                            <Play setUpdateShoot={setUpdateShoot} updateBoards={updateBoards} setUpdateBoards={setUpdateBoards} contentOpponentBoard={contentOpponentBoard} contentMyBoard={contentMyBoard} loadingOpponentBoard={loadingOpponentBoard} loadingSetShoot={loadingSetShoot} ></Play>
                                                            <Box>
                                                                <Button onClick={handleGame}>Game</Button>
                                                                <Button onClick={handlePhase}>Phase</Button>
                                                                <Button onClick={handleGiveUpGame}>Give up</Button>
                                                            </Box>
                                                        </Box>
                                                    ) : (
                                                        <Box>
                                                            <Box>{contentPhase.properties.name}</Box>
                                                            <Button onClick={handleClickLeaveGame}>Go home</Button>
                                                        </Box>
                                                    )
                                                    }
                                                </Box>
                                            )}
                                        </Box>
                                    }
                                </Box>
                            ) : (<Box>
                                <Typography variant='h4'>Create new lobby</Typography>
                                <TextField
                                    id="standard-number"
                                    label="Max shots"
                                    type="number"
                                    InputLabelProps={{
                                        shrink: true,
                                    }}
                                    onChange={(e) => { setMaxShots(e.target.value) }}
                                    value={maxShots}
                                    variant="standard"
                                />
                                <Box>
                                    <Button onClick={handleNewGame}>New game</Button>
                                    <Button onClick={handleGiveUpLobby}>Give up lobby</Button>
                                </Box>
                            </Box>)
                            }
                        </Box>
                    </Box>
                ) :
                (
                    <Navigate replace to='/login'></Navigate>
                )
            }
        </Box>
    )
}