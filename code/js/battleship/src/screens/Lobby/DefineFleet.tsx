import * as React from 'react'
import { useState, useEffect } from 'react'
import { Board } from '../Board/Board'
import Box from '@mui/material/Box';
import { Button, ButtonGroup, IconButton } from '@mui/material';
import Typography from '@mui/material/Typography';
import { cookie_logged_in, useFetchSetFleet } from '../../fetch/useFetch';
import { useCookies } from 'react-cookie';
import { Navigate } from 'react-router-dom';
import DeleteIcon from '@mui/icons-material/Delete';
import DoneIcon from '@mui/icons-material/Done';
import { styleFlexCenter } from '../AppBar/Home';
import { BoardT, initilize_Ships_And_Layouts_View, ShipAndLayout, ShipAndLayoutsView, ShipTypeSize } from '../../utils/types';

export default function DefineFleet() {
  const shipsAndLayoutsView = initilize_Ships_And_Layouts_View()
  const [shipAndLayoutsView, setShipAndLayoutsView] = useState<Array<ShipAndLayoutsView>>(shipsAndLayoutsView)
  const [shipAndLayout, setShipAndLayout] = useState<Array<ShipAndLayout>>([]);
  const [currentShipAndLayout, setCurrentShipAndLayout] = useState<ShipAndLayout>(null);
  const [reset, setReset] = useState(false)
  const [confirmFleet, setConfirmFleet] = useState(false)

  const [state, setState] = useState<BoardT>(
    {
      cells: Array(10).fill(0).map(x => Array(10).fill(0).map(y => {
        return {
          boardPosition: {
            row: x,
            col: y
          },
          wasShoot: false,
          wasShip: false,
          shipType: null,
          shipLayout: null
        }
      }
      )),
    }
  )
  const [cookies, setCookie, removeCookie] = useCookies([cookie_logged_in]);
  const [content, error, loading] = useFetchSetFleet(confirmFleet, shipAndLayout)
  useEffect(() => {
    if (reset) {
      setState(
        {
          cells: Array(10).fill(0).map(x => Array(10).fill(0).map(y => {
            return {
              boardPosition: {
                row: x,
                col: y
              },
              wasShoot: false,
              wasShip: false,
              shipType: null,
              shipLayout: null
            }
          }
          )),
        }
      )
      setShipAndLayoutsView([...initilize_Ships_And_Layouts_View()])
      setShipAndLayout([])
      setCurrentShipAndLayout(null)
      setReset(false)
      setConfirmFleet(false)
    } else {
      const g = state.cells
      shipAndLayout.map((sl, i) => {
        g[sl.referencePoint.row][sl.referencePoint.col]
      })
      setState(state => ({
        ...state,
        'cells': g
      }))
    }
  }, [reset])
  
  function handleOnClick(x: number, y: number) {
    if (shipAndLayout.length <= 5 && currentShipAndLayout != null && currentShipAndLayout.referencePoint == null) {
      const g = state.cells
      if (!g[x][y].wasShip) {
        const size = getShipSize(currentShipAndLayout)
        if (currentShipAndLayout.shipLayout == 'UP') {
          for (var i = 0; i < size; i++) {
            g[x + i][y].wasShip = true
          }
        } else if (currentShipAndLayout.shipLayout == 'DOWN') {
          for (var i = 0; i < size; i++) {
            g[x - i][y].wasShip = true
          }
        } else if (currentShipAndLayout.shipLayout == 'LEFT') {
          for (var i = 0; i < size; i++) {
            g[x][y + i].wasShip = true
          }
        } else if (currentShipAndLayout.shipLayout == 'RIGHT') {
          for (var i = 0; i < size; i++) {
            g[x][y - i].wasShip = true
          }
        }
        const p = { row: x, col: y }
        setCurrentShipAndLayout(
          { ...currentShipAndLayout, referencePoint: p }
        )
        shipAndLayout.find((s) => { return s.shipType == currentShipAndLayout.shipType }).referencePoint = p
        setShipAndLayout([...shipAndLayout])
        setState(state => ({
          ...state,
          'grid': g,
        }))
      }
      else {
        alert('Please select an empty square')
      }
    } else {
      alert('Choose ship to layout')
    }
  }

  function handleReset() {
    setReset(true)
  }
  return (
    <Box>
      {cookies.logged_in == 'true' ?
        (
          <Box>
            <Box sx={styleFlexCenter}>
              <Typography variant='h2'>Define fleet</Typography>
            </Box>
            <Box sx={styleFlexCenter}>
              <Box>
                {shipAndLayoutsView.map((s, i) => {
                  return (
                    <Box key={i}>
                      <Typography variantMapping={{ h3: 'h3' }} style={{ color: s.ship.color }}>{s.ship.shipName}</Typography>
                      <Box
                        sx={{
                          display: 'flex',
                          '& > *': {
                            m: 1,
                          },
                        }}
                      >
                        <ButtonGroup size="small" variant="outlined">
                          {s.shipLayout.map((data, i) => (
                            <Button key={i} style={{ cursor: 'pointer' }} disabled={data.selected} onClick={() => {
                              if (shipAndLayout.length == 0) {
                                if (!shipAndLayout.find((ship) => { return ship.shipType == s.ship.shipName })) {//when layout choose cannot change
                                  s.selected = false
                                  data.selected = true
                                  const shipLayoutValue = {
                                    shipType: s.ship.shipName,
                                    shipLayout: data.shipLayout,
                                    referencePoint: null
                                  }
                                  setCurrentShipAndLayout(shipLayoutValue)
                                  shipAndLayout.push(shipLayoutValue)
                                  setShipAndLayout([...shipAndLayout])
                                }
                              }
                              else {
                                if (currentShipAndLayout.referencePoint != null) {
                                  if (!shipAndLayout.find((ship) => { return ship.shipType == s.ship.shipName })) {//when layout choose cannot change
                                    s.selected = false
                                    data.selected = true
                                    const shipLayoutValue = {
                                      shipType: s.ship.shipName,
                                      shipLayout: data.shipLayout,
                                      referencePoint: null
                                    }
                                    setCurrentShipAndLayout(shipLayoutValue)
                                    shipAndLayout.push(shipLayoutValue)
                                    setShipAndLayout([...shipAndLayout])
                                  }
                                }
                              }
                            }} >{data.shipLayout}</Button>
                          ))}
                        </ButtonGroup>
                      </Box>
                    </Box>
                  )
                })}
              </Box>
              <Board state={state} handleOnClick={handleOnClick}></Board>
              <Box>
                <Box>
                  <IconButton size="small" disabled={shipAndLayout.length == 0} onClick={handleReset}>
                    <DeleteIcon fontSize='large'></DeleteIcon>
                  </IconButton>
                </Box>
                <Box>
                  {shipAndLayout.length == 5 && currentShipAndLayout.referencePoint != null ? (
                    <IconButton size="small" onClick={() => { setConfirmFleet(true) }}>
                      <DoneIcon fontSize='large'></DoneIcon>
                    </IconButton>
                  ) : (<></>)}
                </Box>
              </Box>
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

function getShipSize(currentShipLayout: ShipAndLayout) {
  const keys = Object.keys(ShipTypeSize);
  for (let i = 0; i < keys.length; i++) {
    if (keys[i] === currentShipLayout.shipType) {
      return ShipTypeSize[keys[i]];
    }
  }
  return null;
}