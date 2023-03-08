## BattleShip Frontend Manual (G05)

### Creating the build process

* Create an NPM project: Run the ``npm init`` command to create a new NPM project and generate a ``package.json`` file.

* Install webpack-cli: Run the ``npm install webpack-cli --save-dev`` command to install the ``webpack-cli`` tool, which is a Command Line Interface (CLI) application used to bundle JavaScript modules.

* Set up the build script: In the ``package.json`` file, add a scripts field with a build script that calls the webpack command. This allows you to run the build process by calling ``npm run build``.

* Copy source files to the ``src`` folder: Create a ``src`` folder and copy the source files for the application into it. By default, webpack expects to find the JavaScript files for the application in the ``src`` folder.

* Create a new directory in your project called ``public``. This directory should be at the root level of your project, alongside the ``src`` directory that contains your React code. Inside the ``public`` directory, create a new file called ``index.html`` with ``<div id="container"></div>``. This provides a container for the React application. It acts as a mounting point for the React components that make up the application.

* Rename the entry point module: Rename the entry point module for the application to ``index.js``, as this is the default assumption made by webpack.

* Run the build script: Run the ``npm run build`` command to build the application. This will create a ``dist`` folder with a bundled file called ``main.js``.

* Configure webpack behavior: Create a ``webpack.config.js`` file and use the ``module.exports`` object to configure the behavior of webpack.

* Automate the build and HTML serving process: Install the ``webpack-dev-server`` tool by running ``npm i webpack-dev-server --save-dev``. Then, add a scripts field with a start script that calls the ``webpack-dev-server`` command. This will allow you to run the build process and serve the HTML file automatically whenever changes are made to the source files.

* Add TypeScript support: Install the ``typescript`` and ``ts-loader`` packages by running ``npm i typescript ts-loader --save-dev``. Then, edit the ``webpack.config.js`` file to configure the ``ts-loader`` and create a ``tsconfig.json`` file to configure the TypeScript compilation. This will allow you to use the TypeScript language, which adds static type information to JavaScript, in your development workflow.

* Install ``react-router-dom``: Run the ``npm install react-router-dom`` command to install the ``react-router-dom`` React Router is a collection of navigational components. It allows you to build single-page applications with navigation without the page refreshing as the user navigates. React Router DOM is a part of React Router that it provides DOM bindings for the React Router and makes it easy to use in web browsers. It includes components like ``<Routes>``, ``<Route>``, and ``<Link>`` that allow you to build a declarative routing system for your application.

* Install ``react-dom``: Run the ``npm install react-dom`` command to install the ``react-dom``. ``react-dom`` is a library for rendering React components to the DOM (Document Object Model). It provides a way to take a React component and display it on a webpage. ``createRoot`` is a function provided by the ``react-dom`` library that creates a root container where React components can be rendered.

* Add configuration in file ``webpack.config.js`` for a development server that is used when running a React application in development mode. This development server configuration is designed to make it easy to develop and test a React application locally, while also allowing the frontend application to communicate with a backend server running on a different origin.

* Install ``MUI``: Run the ``npm install @mui/material @emotion/react @emotion/styled`` command to instal ``mui``. ``MUI`` is a effective and efficient tool for building UIs.

### Application internal software organization

```
📂src/
    📂context/
        📜AuthnContainer.tsx
        📜ShipContainer.tsx
    📂fetch/
        📜useFetch.tsx
    📂screens/
        📂Alert/
            📜DialogBox.component.tsx
        📂AppBar/
            📜DarkMode.tsx
            📜Home.tsx
            📜Me.tsx
            📜ResponsiveAppBar.tsx
            📜SignIn.tsx
            📜SignUp.tsx
            📜StickyHeadTableLeaderboard.tsx
            📜StickyHeadTableUsersGameHistory.tsx
        📂Board/
            📜Board.tsx
            📜Square.tsx
        📂Loading/
            📜CircularIndeterminate.tsx
            📜LinearIndeterminated.tsx
        📂Lobby/
            📜CreateLobby.tsx
            📜DefineFleet.tsx
            📜DefineFleetBoard.tsx
            📜DemoCreateLobby.tsx
            📜Play.tsx
    📂utils/
        📜GameConfig.tsx
        📜UserConfigs.tsx
    📜App.tsx
    📜index.tsx
```

### Main implementation challenges

- **Managing state**
    - One of the main challenges in building a React app was managing state. In our app, we need to manage state for the authentication process, the game state, and the use of ``Polling`` for multiplayer functionality. This can be particularly challenging if we are working with a large and complex application.

- **Implementing cookies-based authorization**
    - Implementing cookies-based authorization was also a challenge. We need to ensure that we are storing and managing the cookies correctly, and that we are properly protecting against security vulnerabilities such as ``cross-site request forgery (CSRF)``.

- **Using ``Polling`` for multiplayer functionality**
    - ``Polling`` can be an effective way to implement multiplayer functionality, but it can also be resource-intensive and may not function well if we lead with a large number of users and games. We also have in consideration that this method of ``Polling`` is not as efficient and resilient as other techniques such as ``Server-Sent-Events (SSE)`` but due to ease of use we chose to use ``Polling``.

- **Implementing game mechanics**
    - With this battleship game we need to carefully design and implement the game mechanics to ensure that the game is both fun and fair for all players.

- **User experience design**
    - Designing a good user experience is crucial for any application, and it can be particularly important in a multiplayer game where players expect a smooth and enjoyable experience. We need to carefully consider how to design the user interface and interactions to ensure that they are intuitive and enjoyable for the users.
