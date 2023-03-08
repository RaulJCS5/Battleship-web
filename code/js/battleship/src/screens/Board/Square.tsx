import * as React from 'react'
import { Grid } from '@mui/material';
import { water } from './Board';
export default function Square({ dims, onClick, content, index_col }: { dims: number, onClick: () => void, content: string, index_col: number }) {
  const cursor = content == water ? 'crosshair' : 'not-allowed';
  return (
    <Grid item
      m={0.2}
      style={
        {
          textAlign: 'center',
          width: dims,
          height: dims,
          backgroundColor: content,
          color: 'white',
          border: "1px solid black",
          cursor: cursor,
          borderRadius: '10px',
        }
      }
      onClick={onClick}
    ></Grid>
  )
}
