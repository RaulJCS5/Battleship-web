# **Battleship user entity**

## **User**

Below is a JSON Siren example for person

The media type for JSON Siren is `application/vnd.siren+json`.

Example user create input model

### CREATE
- - -
``POST`` http request

*Authorization: no*

```json
"http://localhost:8080/register"
```

Input model

```json
    "username": "chucknorris", 
    "email" : "chucknorris@alunos.isel.pt",
    "password" : "Chucknorris123!"

    "username": "jonrambo", 
    "email" : "jonrambo@alunos.isel.pt",
    "password" : "Jonrambo123!"
```

Output model

```json
{
  "class":["user"],
  "properties":{
    "username": "chucknorris", 
    "email" : "chucknorris@alunos.isel.pt",
    "password" : "Chucknorris123!"
  },
  "entities":[
    {
      "rel": ["http://localhost:8080/users"],
      "properties": 
      {
        "username": "jonrambo", 
        "email" : "jonrambo@alunos.isel.pt"
      },
      "links": 
      [
        {
          "rel": ["self"], 
          "href": "http://localhost:8080/users/1"
        }
      ]
    },
    {
      "rel": ["http://localhost:8080/users"],
      "properties": 
      {
        "username": "bob", 
        "email" : "bob@alunos.isel.pt"
      },
      "links": 
      [
        {
          "rel": ["self"], 
          "href": "http://localhost:8080/users/2"
        }
      ]
    }
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users/3"
    }
  ],
  "actions": [
    {
      "name": "home", 
      "href":"http://localhost:8080/me", 
      "method":"GET", 
      "type": "application/json",
      "fields": []
    },
    {
      "name": "login", 
      "href":"http://localhost:8080/users/auth", 
      "method":"POST", 
      "type": "application/json",
      "fields": 
      [
        {
          "name":"username", 
          "type": "text"
        },
        {
          "name":"password", 
          "type": "text"
        }
      ]
    },
    {
      "name": "logout", 
      "href":"http://localhost:8080/users/logout", 
      "method":"DELETE", 
      "type": "application/json",
      "fields": []
    },
    {
      "name": "get-user", 
      "href":"http://localhost:8080/users/3", 
      "method":"GET", 
      "type": "application/json",
      "fields": []
    },
    {
      "name": "get-all-user", 
      "href":"http://localhost:8080/users", 
      "method":"GET", 
      "type": "application/json",
      "fields": []
    }
  ]
}
```

### LOGIN
- - -
``POST`` http request

*Authorization: no*

```json
"http://localhost:8080/users/auth"
```

Input model

```json
    "username": "chucknorris", 
    "password" : "Chucknorris123!",

    "username": "jonrambo", 
    "password" : "Jonrambo123!"
```

Output model

```json
{
  "class":["user"],
  "properties":{
    "username": "chucknorris", 
    "password" : "Chucknorris123!"
  },
  "entities":[
    ...
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users/3"
    }
  ],
  "actions": [
    {
      "name": "home", 
      ...
    },
    {
      "name": "logout", 
      ...
    },
    {
      "name": "get-user", 
      ...
    },
    {
      "name": "get-all-user", 
      ...
    },
    {
      "name": "register", 
      "href":"http://localhost:8080/register", 
      "method":"POST", 
      "type": "application/json",
      "fields": 
      [
        {
          "name":"username", 
          "type": "text"
        },
        {
          "name":"email", 
          "type": "text"
        },
        {
          "name":"password", 
          "type": "text"
        }
      ]
    }
  ]
}
```

### HOME
- - -
``GET`` http request

*Authorization: yes*

```json
"http://localhost:8080/me"
```

Input model

```json

```

Output model

```json
{
  "class":["user"],
  "properties":{},
  "entities":[
    ??? //TODO:
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users/3"
    }
  ],
  "actions": [
    {
      "name": "logout", 
      ...
    },
    {
      "name": "get-user", 
      ...
    },
    {
      "name": "get-all-user", 
      ...
    },
    {
      "name": "register", 
      ...
    },
    {
      "name": "login", 
      ...
    }
  ]
}
```

### LOGOUT
- - -

``DELETE`` http request

*Authorization: yes*

```json
"http://localhost:8080/users/logout"
```

Input model

```json

```

Output model

```json
{
  "class":["user"],
  "properties":{},
  "entities":[
    ??? //TODO:
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users/3"
    }
  ],
  "actions": [
    {
      "name": "home", 
      ...
    },
    {
      "name": "get-user", 
      ...
    },
    {
      "name": "get-all-user", 
      ...
    },
    {
      "name": "register", 
      ...
    },
    {
      "name": "login", 
      ...
    }
  ]
}
```

### GET_BY_ID
- - -
``GET`` http request

*Authorization: no*

```json
"http://localhost:8080/users/{id}"
```

Input model

```json

```

Output model

```json
{
  "class":["user"],
  "properties":{},
  "entities":[
    ??? //TODO:
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users/3"
    }
  ],
  "actions": [
    {
      "name": "home", 
      ...
    },
    {
      "name": "logout", 
      ...
    },
    {
      "name": "get-all-user", 
      ...
    },
    {
      "name": "register", 
      ...
    },
    {
      "name": "login", 
      ...
    }
  ]
}
```

### GET_USERS
- - -
``GET`` http request

*Authorization: no*

```json
"http://localhost:8080/users"
```

Input model

```json

```

Output model

```json
{
  "class":["user"],
  "properties":{},
  "entities":[
    ??? //TODO:
  ],
  "links": [
    {
      "rel": ["self"], 
      "href": "http://localhost:8080/users"
    }
  ],
  "actions": [
    {
      "name": "home", 
      ...
    },
    {
      "name": "logout", 
      ...
    },
    {
      "name": "get-user", 
      ...
    },
    {
      "name": "register", 
      ...
    },
    {
      "name": "login", 
      ...
    }
  ]
}
```