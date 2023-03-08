import * as React from 'react';
import CircularProgress from '@mui/material/CircularProgress';
import Box from '@mui/material/Box';
import { styleFlexCenter } from '../AppBar/Home';

export default function CircularIndeterminate() {
  return (
    <Box sx={styleFlexCenter}>
      <CircularProgress />
    </Box>
  );
}