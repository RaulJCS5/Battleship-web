import * as React from 'react'
import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useCookies } from 'react-cookie';
import { cookie_dotcom_user, cookie_logged_in} from '../../fetch/useFetch';
import { Box, Typography } from '@mui/material';
export function Me() {
	const [cookies, setCookie, removeCookie] = useCookies([cookie_logged_in, cookie_dotcom_user]);
	return (
		<>
			{cookies.logged_in == 'true' ?
				(<Box>
					<Typography fontSize={'medium'} color={'gray'} variant='h4'>{cookies.dotcom_user}</Typography>
				</Box>) :
				(
					<Navigate replace to='/login'></Navigate>
				)
			}
		</>
	)
}