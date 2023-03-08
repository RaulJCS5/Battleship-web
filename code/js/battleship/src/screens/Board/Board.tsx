import * as React from 'react'
import Square from './Square'
import { Grid } from '@mui/material';
import Box from '@mui/material/Box';
import { BoardT, PositionStateBoard } from '../../utils/types';

export const hit = 'red'
export const miss = 'white'
export const boat = 'gray'
export const water = 'blue'

export function Board({ state, handleOnClick }: { state: BoardT, handleOnClick: (x: number, y: number) => void }): React.ReactElement {
    var buildTable = undefined
    if (state.cells != undefined) {
        buildTable = state.cells.map((row: Array<PositionStateBoard>, index_row) => {
            return (
                <Grid container key={'row_' + index_row}>
                    {
                        row.map((col: PositionStateBoard, index_col) => {
                            return (
                                <Square key={index_row + '_' + index_col} dims={40} onClick={() => handleOnClick(index_row, index_col)} content={col.wasShoot ? col.wasShip ? hit : miss : col.wasShip ? boat : water} index_col={index_col}></Square>
                            )
                        })
                    }
                </Grid>
            )
        })
    }
    return (
        <Box>{buildTable}</Box>
    )
}