export const url_demo = '/api/demo'
//demo only

export const url_new_game = '/api/users/game/new'
//POST
//Authorization
/*
{
    "maxShots" : maxShots
}
*/

export function url_set_fleet(gameId:string) {
  return `/api/users/game/${Number(gameId)}/setFleet`
}
//POST
//Authorization
/*
[
  {
    "shipType": "CARRIER",
    "shipLayout": "UP",
    "referencePoint": {
        "row":0,
        "col":0
    }
  },
    {
    "shipType": "BATTLESHIP",
    "shipLayout": "UP",
    "referencePoint": {
        "row":0,
        "col":1
    }
  },
    {
    "shipType": "SUBMARINE",
    "shipLayout": "DOWN",
    "referencePoint": {
        "row":4,
        "col":7
    }
  },
    {
    "shipType": "CRUISER",
    "shipLayout": "LEFT",
    "referencePoint": {
        "row":8,
        "col":1
    }
  },
    {
    "shipType": "DESTROYER",
    "shipLayout": "RIGHT",
    "referencePoint": {
        "row":9,
        "col":6
    }
  }
]
*/

export const url_game_history = '/api/users/game/history'
//GET
//Authorization
/*
*/

export function url_check_fleet(gameId:string) {
  return `/api/users/game/${Number(gameId)}/getFleet`
}
//GET
//Authorization
/*
{
    "myBoard" : true
}
*/

export function url_set_shoot(gameId:string) {
  return `/api/users/game/${Number(gameId)}/shoot`
}
//POST
//Authorization
/*
{
    "row" : 0,
    "col" : 0
}
*/

export function url_check_current_game_phase(gameId:string) {
  return `/api/users/game/${Number(gameId)}/getCurrentPhase`
}
//GET
//Authorization
/*
*/

export const url_get_user_current_game = '/api/users/game'
//GET
//Authorization
/*
*/

export const url_give_up_lobby = '/api/users/game/giveUpLobby'
//POST
//Authorization
/*
*/

export const url_give_up_game = '/api/users/game/giveUp'
//POST
//Authorization
/*
*/

export const url_ranking = '/api/public/ranking/'
//GET
//No Authorization
/*
*/