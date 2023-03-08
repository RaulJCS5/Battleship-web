import * as React from 'react'
import { useEffect } from 'react';
import { Navigate } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { cookie_logged_in } from '../../fetch/useFetch';
import { Typography, Box, Button } from '@mui/material';
import { Board } from '../Board/Board';
import { styleFlexCenter } from '../AppBar/Home';
import { Position, PositionStateBoard } from '../../utils/types';

export function Play({setUpdateShoot,updateBoards,setUpdateBoards,contentOpponentBoard,contentMyBoard,loadingOpponentBoard,loadingSetShoot}:{setUpdateShoot:(p:Position)=>void,updateBoards:boolean,setUpdateBoards:(b:boolean)=>void,contentOpponentBoard:PositionStateBoard[][],contentMyBoard:PositionStateBoard[][],loadingOpponentBoard:boolean,loadingSetShoot:boolean}) {
	const [cookies, setCookie, removeCookie] = useCookies([cookie_logged_in]);
	useEffect(()=>{
		setUpdateBoards(!updateBoards)
	},[])
	function handleOnClickShoot(row: number, col: number) {
		if(!(loadingOpponentBoard||loadingSetShoot)){
			setUpdateShoot({ row: row, col: col })
			setUpdateBoards(!updateBoards)
		}
	}

	function handleUpdateBoards() {
		setUpdateBoards(!updateBoards)
	}

	return (
		<Box>
			{cookies.logged_in == 'true' ?
				(
					<Box>
						<Box>
							<Box sx={styleFlexCenter}>
								<Typography variant='h2'>Play</Typography>
							</Box>
							<Box sx={styleFlexCenter}>
								<Box>
									<Typography variant='h3'>Shoots</Typography>
									<Board state={{ cells: contentOpponentBoard }} handleOnClick={handleOnClickShoot}></Board>
								</Box>
								<Box sx={{ m: 2 }}></Box>
								<Box>
									<Typography variant='h3'>My fleet</Typography>
									<Board state={{ cells: contentMyBoard }} handleOnClick={()=>{}}></Board>
								</Box>
							</Box>
							<Button onClick={handleUpdateBoards} color={'success'}>Update boards</Button>
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