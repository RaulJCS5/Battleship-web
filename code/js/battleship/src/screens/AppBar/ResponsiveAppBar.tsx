import * as React from 'react';
import AppBar from '@mui/material/AppBar';
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import IconButton from '@mui/material/IconButton';
import Typography from '@mui/material/Typography';
import Menu from '@mui/material/Menu';
import MenuIcon from '@mui/icons-material/Menu';
import Container from '@mui/material/Container';
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import Tooltip from '@mui/material/Tooltip';
import MenuItem from '@mui/material/MenuItem';
import DirectionsBoatIcon from '@mui/icons-material/DirectionsBoat';
import { Link } from 'react-router-dom';
import { DarkMode } from './DarkMode';
import { useCookies } from 'react-cookie';
import { cookie_dotcom_user, cookie_logged_in, cookie_user_session } from '../../fetch/useFetch';
import { useEffect, useState } from 'react';
import { url_logout } from '../../utils/UserConfigs';
const pagesName = ['Leaderboard', 'Users game history'];
const pagesUrl = ['leaderboard', 'usersgamehistory'];

const buttonLogout = 'Logout';
const settingsPage = ['Your profile'];
const settingsUrl = ['me']
const styleLink = { textDecoration: 'none', color: 'inherit' }
function ResponsiveAppBar() {
  const [errMsg, setErrMsg] = useState('');
  const [cookies, setCookie, removeCookie] = useCookies([cookie_user_session, cookie_logged_in, cookie_dotcom_user]);
  const [pagesCredentialsName, setPagesCredentialsName] = useState([])
  const [pagesCredentialsUrl, setPagesCredentialsUrl] = useState([])
  const [logout, setLogout] = useState(false)
  const [loading, setLoading] = useState(false)
  useEffect(() => {
    if (cookies.logged_in == 'true') {
      setPagesCredentialsName([])
      setPagesCredentialsName([])
    } else {
      setPagesCredentialsName(['Sign in', 'Sign up'])
      setPagesCredentialsUrl(['login', 'register'])
    }
  }, [cookies.logged_in])

  useEffect(() => {
    async function doFetch() {
      // POST request using fetch with error handling
      //Supose to be cookie instead of Authorization
      const requestOptions = {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${cookies.user_session}`,
        }
      };
      await fetch(url_logout, requestOptions)
        .then(async response => {
          if (!response.ok) {
            const isJson = response.headers.get('content-type')?.includes('application/json');
            const body = isJson && await response.json();
            // check for error response
            // get error message from body or default to response status
            const error = (body && body.message) || response.status;
            return Promise.reject(error);
          }
          try {
            if (cookies.user_session != undefined && cookies.logged_in == 'true') {
              removeCookie(cookie_user_session)
              removeCookie(cookie_dotcom_user)
              const minutes = 60 * 4 //4 hours
              var date = new Date()
              date.setTime(date.getTime() + (minutes * 60 * 1000))
              setCookie(cookie_logged_in, false, { expires: date, secure: true, sameSite: 'strict', path: '/' })
              setLoading(false)
            }
          } catch (err) {
            if (!err?.response) {
              setErrMsg('No Server Response');
            } else if (err.response?.status === 401) {
              setErrMsg('Unauthorized');
            } else {
              setErrMsg('Logout Failed');
            }
          }
        })
        .catch(error => {
          console.error(`There was an error!`, error);
        });
    }
    if (logout) {
      setLogout(false)
      setLoading(true)
      doFetch()
    }
  }, [logout])

  const handleLogout = async (e) => {
    e.preventDefault();

    if (cookies.user_session != undefined && cookies.logged_in == 'true') {
      //Remove Cookie
      setLogout(true)
    }
  };
  const [anchorElNav, setAnchorElNav] = React.useState<null | HTMLElement>(null);
  const [anchorElUser, setAnchorElUser] = React.useState<null | HTMLElement>(null);

  const handleOpenNavMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElNav(event.currentTarget);
  };
  const handleOpenUserMenu = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorElUser(event.currentTarget);
  };

  const handleCloseNavMenu = () => {
    setAnchorElNav(null);
  };

  const handleCloseUserMenu = () => {
    setAnchorElUser(null);
  };

  return (
    <AppBar position="static">
      <Container maxWidth="xl">
        <Toolbar disableGutters>
          <DirectionsBoatIcon sx={{ display: { xs: 'none', md: 'flex' }, mr: 1 }} />
          <Typography
            variant="h6"
            noWrap
            sx={{
              mr: 2,
              display: { xs: 'none', md: 'flex' },
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            <Link style={styleLink} to="/">Battleship</Link>
          </Typography>

          <Box sx={{ flexGrow: 1, display: { xs: 'flex', md: 'none' } }}>
            <IconButton
              size="large"
              aria-label="account of current user"
              aria-controls="menu-appbar"
              aria-haspopup="true"
              onClick={handleOpenNavMenu}
              color="inherit"
            >
              <MenuIcon />
            </IconButton>
            <Menu
              id="menu-appbar"
              anchorEl={anchorElNav}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'left',
              }}
              keepMounted
              transformOrigin={{
                vertical: 'top',
                horizontal: 'left',
              }}
              open={Boolean(anchorElNav)}
              onClose={handleCloseNavMenu}
              sx={{
                display: { xs: 'block', md: 'none' },
              }}
            >
              {pagesName.concat(pagesCredentialsName).map((page, i) => (
                <MenuItem key={page} onClick={handleCloseNavMenu}>
                  <Typography textAlign="center">
                    <Link style={styleLink} to={`${pagesUrl.concat(pagesCredentialsUrl)[i]}`}>{page}</Link>
                  </Typography>
                </MenuItem>
              ))}
            </Menu>
          </Box>
          <DirectionsBoatIcon sx={{ display: { xs: 'flex', md: 'none' }, mr: 1 }} />
          <Typography
            variant="h5"
            noWrap
            sx={{
              mr: 2,
              display: { xs: 'flex', md: 'none' },
              flexGrow: 1,
              fontFamily: 'monospace',
              fontWeight: 700,
              letterSpacing: '.3rem',
              color: 'inherit',
              textDecoration: 'none',
            }}
          >
            <Link style={styleLink} to="/">Battleship</Link>
          </Typography>
          <Box sx={{ flexGrow: 1, display: { xs: 'none', md: 'flex' } }}>
            {pagesName.map((page, i) => (
              <Button
                key={page}
                onClick={handleCloseNavMenu}
                sx={{ my: 2, color: 'white', display: 'block' }}
              >
                <Link style={styleLink} to={`${pagesUrl[i]}`}>{page}</Link>
              </Button>
            ))}
          </Box>
          <Box sx={{ display: { xs: 'none', md: 'flex' } }}>
            {pagesCredentialsName.map((page, i) => (
              <Button
                key={page}
                onClick={handleCloseNavMenu}
                sx={{ my: 2, color: 'white', display: 'block' }}
              >
                <Link style={styleLink} to={`${pagesCredentialsUrl[i]}`}>{page}</Link>
              </Button>
            ))}
          </Box>
          <DarkMode />
          {cookies.logged_in == 'true' ? (
            <Box sx={{ flexGrow: 0 }}>
              <Tooltip title="Open settings">
                <IconButton onClick={handleOpenUserMenu} sx={{ p: 0 }}>
                  <Avatar />
                </IconButton>
              </Tooltip>
              <Menu
                sx={{ mt: '45px' }}
                id="menu-appbar"
                anchorEl={anchorElUser}
                anchorOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                keepMounted
                transformOrigin={{
                  vertical: 'top',
                  horizontal: 'right',
                }}
                open={Boolean(anchorElUser)}
                onClose={handleCloseUserMenu}
              >
                {settingsPage.map((setting, i) => (
                  <MenuItem key={setting} onClick={handleCloseUserMenu}>
                    <Typography textAlign="center">
                      <Link style={styleLink} to={`${settingsUrl[i]}`}>{setting}</Link>
                    </Typography>
                  </MenuItem>
                ))}
                <MenuItem key={buttonLogout} onClick={handleCloseUserMenu}>
                  <Typography textAlign="center" onClick={handleLogout}>
                    {buttonLogout}
                  </Typography>
                </MenuItem>
              </Menu>
            </Box>
          ) : (
            <></>
          )
          }
        </Toolbar>
      </Container>
    </AppBar>
  );
}
export default ResponsiveAppBar;