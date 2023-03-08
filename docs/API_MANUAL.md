## BattleShip Backend API Manual (G05)

### Lista de links disponibilizados web API

* Registo de um utilizador: http://localhost:8080/api/users/register
  * Request method: POST / Authorization: FALSE
    * Body example:
  
```
{
    "username" : "td2",
    "email" : "td39@hotamail.com",
    "password" : "password"
}
```
* Recuperar password de um utilizador através do seu email: http://localhost:8080/api/users/recovery
  * Method: POST / Authorization: FALSE
     * Body example:
```
{
    "email" : "td2@sapo.pt"
}
```
* Autenticar um utilizador: http://localhost:8080/api/users/auth
  * Method: POST / Authorization: FALSE
      * Body example:
```
{
    "username" : "td1",
    "password" : "password"
}
```
* Logout de um utlizador, ou seja invalidar o token na BD: http://localhost:8080/api/users/logout
  * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
```
Empty body and no response
```
* Aceder à home do utilizador autenticado: http://localhost:8080/api/me
  * Method: GET / Authorization: TRUE -> with "Bearer {{token}}"
    * Response example:
```
{
    "class": [
        "userHome"
    ],
    "properties": {
        "id": 3,
        "username": "td1",
        "email": "td391122@hotamail.com"
    },
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/me"
        }
    ],
    "actions": [
        {
            "name": "newGame",
            "href": "/api/users/game/new",
            "method": "POST",
            "type": "application/json",
            "fields": [
                {
                    "name": "maxShots",
                    "type": "number",
                    "value": null
                }
            ]
        },
        {
            "name": "gameHistory",
            "href": "/api/users/game/history",
            "method": "GET",
            "type": "application/json",
            "fields": []
        },
        {
            "name": "logout",
            "href": "/api/users/logout",
            "method": "POST",
            "type": "application/json",
            "fields": []
        }
    ]
}
```
* Indicar ao sistema que pretende iniciar um jogo com a regra de emparelhamento @maxShoots: http://localhost:8080/api/users/game/new
    * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
      * Body example:
```
{
    "maxShots" : 1
}
```
* Obter o jogo atual do utilizador autenticado: http://localhost:8080/api/users/game
    * Method: GET / Authorization: TRUE -> with "Bearer {{token}}"
      * Response example:
```
{
    "class": [
        "getUserCurrentGameId"
    ],
    "properties": {
        "gameId": 32
    },
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/game"
        }
    ],
    "actions": []
}
```
* Obter o histórico de jogos jogados pelo utilizador autenticado: http://localhost:8080/api/users/game/history
    * Method: GET / Authorization: TRUE -> with "Bearer {{token}}"
      * Response example:
```
{
    "class": [
        "gameHistory"
    ],
    "properties": [
        {
            "gameId": 1,
            "playerOne": {
                "id": 4,
                "username": "td2",
                "email": "td39122@hotamail.com"
            },
            "playerTwo": {
                "id": 3,
                "username": "td1",
                "email": "td391122@hotamail.com"
            },
            "gamePhase": {
                "id": 4,
                "name": "PLAYER_ONE_WON"
            },
            "roundNumber": 0,
            "playerWin": {
                "id": 4,
                "username": "td2",
                "email": "td39122@hotamail.com"
            }
        }
    ],
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/game/history"
        }
    ],
    "actions": []
}
```
* Obter o estado da fleet para o @gameId. Se @myBoard=true devolve a fleet do utilizador autenticado, caso contrário devolve a fleet do adversário: http://localhost:8080/api/users/game/{{gameId}}/getFleet
    * Method: GET / Authorization: TRUE -> with "Bearer {{token}}"
      * Body example:
```
{
    "myBoard" : true
}
```
* Obter a "phase" do jogo @gameId: http://localhost:8080/api/users/game/{{gameId}}/getCurrentPhase
    * Method: GET / Authorization: TRUE -> with "Bearer {{token}}"
      * Response example:
```
{
    "class": [
        "getCurrentGamePhase"
    ],
    "properties": {
        "id": 4,
        "name": "PLAYER_ONE_WON"
    },
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/users/game/17/getCurrentPhase"
        }
    ],
    "actions": []
}
```
* Definir a feet do utilizador autenticado para o @gameId: http://localhost:8080/api/users/game/{{gameId}}/setFleet
    * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
        * Body example:
```
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
```
* Dar um tiro na fllet adversária do utilizador autenticado para o @gameId: http://localhost:8080/api/users/game/{{gameId}}/shoot
    * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
      * Body example:
```
{
    "row" : 0,
    "col" : 0
}
```
* Sair do lobby caso ainda não exista emparelhamento: http://localhost:8080/api/users/game/giveUpLobby
    * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
      * Response example:
```
{
    "type": "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/problems/user-lobby-missing",
    "title": "User not exists in lobby room",
    "detail": "User not exists in lobby room! Operation failed!"
}
```
* Desistir do jogo atual e dar a vitória ao adversário: http://localhost:8080/api/users/game/giveUp
    * Method: POST / Authorization: TRUE -> with "Bearer {{token}}"
      * Response example:
```
{
    "message": "You give up game! The other player was declared winner!"
}
```
* Obter o raking global do sistema: http://localhost:8080/api/public/ranking
    * Method: GET / Authorization: FALSE
      * Response example:
```
{
    "class": [
        "getRanking"
    ],
    "properties": [
        {
            "user": {
                "id": 4,
                "username": "td2",
                "email": "td39122@hotamail.com"
            },
            "playedGames": 2,
            "winGames": 2,
            "lostGames": 0,
            "rankPoints": 200
        },
        {
            "user": {
                "id": 3,
                "username": "td1",
                "email": "td391122@hotamail.com"
            },
            "playedGames": 2,
            "winGames": 0,
            "lostGames": 2,
            "rankPoints": 100
        }
    ],
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/public/ranking/"
        }
    ],
    "actions": []
}
```
* Home path do sistema: http://localhost:8080/api/
    * Method: GET / Authorization: FALSE
      * Response example:
```
{
    "class": [
        "home"
    ],
    "properties": {
        "credits": "BattleShip API, Made by L51N G05 group! Check our public page"
    },
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api"
        },
        {
            "rel": [
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/rels/register"
            ],
            "href": "/api/users/register"
        },
        {
            "rel": [
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/rels/login"
            ],
            "href": "/api/users/auth"
        },
        {
            "rel": [
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/rels/public"
            ],
            "href": "/api/public"
        }
    ],
    "actions": []
}
```
* Public path do sistema: http://localhost:8080/api/public
    * Method: GET / Authorization: FALSE
        * Response example:
```
{
    "class": [
        "public"
    ],
    "properties": {
        "systemVersion": "0.1.0 (phase 1)",
        "systemAuthors": "Tiago Duarte (42525); Raul Santos(44806);"
    },
    "entities": [],
    "links": [
        {
            "rel": [
                "self"
            ],
            "href": "/api/public"
        },
        {
            "rel": [
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/rels/home"
            ],
            "href": "/api"
        },
        {
            "rel": [
                "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/rels/leaderBoard"
            ],
            "href": "/api/public/ranking/"
        }
    ],
    "actions": []
```

### Response, caso o token do "authorization" não seja válido
* Para todos os links que requerem autenticação

```
{
    "type": "https://github.com/isel-leic-daw/2022-daw-leic51n-g05/tree/main/code/jvm/battleship/docs/problems/invalid-token-auth",
    "title": "Invalid user token!",
    "detail": "Invalid user token! Current path needs valid auth token!"
}
```