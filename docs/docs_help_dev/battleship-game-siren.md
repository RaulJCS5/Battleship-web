# Battleship **GAME** entity

Below is a JSON Siren example for game

The media type for JSON Siren is `application/vnd.siren+json`.

Example game create input model

### **INTENDS_TO_PLAY**
- - -
Before the game starts, the player 1 will be in a waiting lobby so that the player 2 match the same game rule and the game start.

### **START**
- - -
``POST`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/game/start"
```

Input model

```json
{
  "maxShots" : 1
}
```

Output model

```json
{
  "class":["game"],
  "properties": {
    "maxShots" : 1
  },
  "entities":[
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "alice",
        "email":"alice@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/1"}
      ]
    },
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "bob",
        "email":"bob@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/2"}
      ]
    },
  ],
  "links": [
    {"rel": ["self"], "href": "http://localhost:8080/game/1"}
  ],
  "actions": [
    ...
  ]
}
```

### **LAYOUTFLEET**
- - -
``POST`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/game/{gameId}"
```

Input model

```json
[
    {
        "shipName" : "destroyer",
        "shipSize" : 6,
        "shipLayout" : "LEFT",
        "row" : 1,
        "col" : 9
    }, 
    {
        "shipName" : "carrier",
        "shipSize" : 2,
        "shipLayout" : "DOWN",
        "row" : 1,
        "col" : 1
    }
]
```

Output model

```json
{
  "class":["game"],
  "properties": [
    {
        "shipName" : "destroyer",
        "shipSize" : 6,
        "shipLayout" : "LEFT",
        "row" : 1,
        "col" : 9
    }, 
    {
        "shipName" : "carrier",
        "shipSize" : 2,
        "shipLayout" : "DOWN",
        "row" : 1,
        "col" : 1
    }
  ],
  "entities":[
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "alice",
        "email":"alice@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/1"}
      ]
    },
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "bob",
        "email":"bob@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/2"}
      ]
    }
  ],
  "links": [
    {"rel": ["self"], "href": "http://localhost:8080/game/1"}
  ],
  "actions": [
    ...
  ]
}
```

### **SETSHOTS**
- - -
``POST`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/game/{gameId}/shoot"
```

Input model

```json
{
    "row":1,
    "col":1
}
```

Output model

```json
{
  "class":["game"],
  "properties": [
    {
      "row":1,
      "col":1
    }
  ],
  "entities":[
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "alice",
        "email":"alice@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/1"}
      ]
    },
    {
      "rel": ["http://localhost:8080/users"],
      "properties": {
        "username": "bob",
        "email":"bob@alunos.isel.pt"
      },
      "links": [
        {"rel": ["self"], "href": "http://localhost:8080/users/2"}
      ]
    }
  ],
  "links": [
    {"rel": ["self"], "href": "http://localhost:8080/game/1"}
  ],
  "actions": [
    ...
  ]
}
```

### **FLEET**
- - -
``GET`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/game/{gameId}"
```

Input model

```json

```

Output model

```json

```

### **OPPONENTFLEET**
- - -
``GET`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/game/{gameId}/opponentfleet"
```

Input model

```json

```

Output model

```json

```

### **OVERALL**
- - -
``GET`` http request

*Authorization: yes*

```json
"http://localhost:8080/game{gameId}"
```

Input model

```json

```

Output model

```json

```