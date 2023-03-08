import * as React from 'react';
import Paper from '@mui/material/Paper';
import Table from '@mui/material/Table';
import TableBody from '@mui/material/TableBody';
import TableCell from '@mui/material/TableCell';
import TableContainer from '@mui/material/TableContainer';
import TableHead from '@mui/material/TableHead';
import TablePagination from '@mui/material/TablePagination';
import TableRow from '@mui/material/TableRow';
import {
  useState, useEffect
} from 'react'
import { Box } from '@mui/system';
import LinearIndeterminate from '../Loading/LinearIndeterminated';
import { url_ranking } from '../../utils/GameConfig';
import { ProblemOutputModel, SirenGetRanking } from '../../utils/types';

interface Column {
  id: 'username' | 'playedGames' | 'winGames' | 'lostGames' | 'rankPoints';
  label: string;
  minWidth?: number;
  align?: 'right';
  format?: (value: number) => string;
}

const columns: readonly Column[] = [
  { id: 'username', label: 'Username', minWidth: 170 },
  { id: 'playedGames', label: 'Played games', minWidth: 100 },
  { id: 'winGames', label: 'Win games', minWidth: 100 },
  { id: 'lostGames', label: 'Lost games', minWidth: 100 },
  { id: 'rankPoints', label: 'Rank points', minWidth: 100 },
];

export function StickyHeadTableLeaderboard() {
  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(10);

  const handleChangePage = (event: unknown, newPage: number) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event: React.ChangeEvent<HTMLInputElement>) => {
    setRowsPerPage(+event.target.value);
    setPage(0);
  };

  const [loading, setLoading] = useState(false)
  const [content, setContent] = useState([])
  const [error, setError] = useState(undefined)
  useEffect(() => {
    let cancelled = false
    async function doFetch() {
      const requestOptions = {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        }
      };
      await fetch(url_ranking, requestOptions)
        .then(async response => {
          const isJson = response.headers.get('content-type')?.includes('application/problem+json');
          // check for error response
          if (!response.ok) {
            var bodyError = await response.json()
            if (isJson) {
              bodyError = bodyError as ProblemOutputModel;
              if (response.status == 420 && bodyError.title == 'Ranking is empty') {
                setLoading(false)
                setError(undefined)
                setContent([])
                return
              }
              else {
                setError(bodyError)
              }
            }
            setLoading(false)
            return Promise.reject(bodyError);
          }
          if (!cancelled) {
            const body = await response.json() as SirenGetRanking;
            setLoading(false)
            setContent(body.properties)
          }
        })
        .catch(error => {
          console.error('There was an error!', error);
        });
    }
    setLoading(true)
    doFetch()
    return () => {
      cancelled = true
    }
  }, [])

  return (
    <Box>{loading || error != undefined || content == undefined ? (
      <LinearIndeterminate></LinearIndeterminate>
    ) : (
      <Paper sx={{ width: '100%', overflow: 'hidden' }}>
        <TableContainer>
          <Table stickyHeader aria-label="sticky table">
            <TableHead>
              <TableRow>
                {columns.map((column) => (
                  <TableCell
                    key={column.id}
                    align={column.align}
                    style={{ minWidth: column.minWidth }}
                  >
                    {column.label}
                  </TableCell>
                ))}
              </TableRow>
            </TableHead>
            <TableBody>
              {content
                .slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
                .map((row, i) => {
                  return (
                    <TableRow hover role="checkbox" tabIndex={-1} key={i}>
                      {columns.map((column) => {
                        const value = row[column.id];
                        return (
                          <TableCell key={column.id} align={column.align}>
                            {column.format && typeof value === 'number'
                              ? column.format(value)
                              : column.id == 'username' ? row.user.username : value}
                          </TableCell>
                        );
                      })}
                    </TableRow>
                  );
                })}
            </TableBody>
          </Table>
        </TableContainer>
        <TablePagination
          rowsPerPageOptions={[10, 25, 100]}
          component="div"
          count={content.length}
          rowsPerPage={rowsPerPage}
          page={page}
          onPageChange={handleChangePage}
          onRowsPerPageChange={handleChangeRowsPerPage}
        />
      </Paper>
    )}</Box>
  );
}