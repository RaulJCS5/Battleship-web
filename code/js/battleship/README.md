# Battleship browser application

### How to run? (Internal debug without docker)

* npm install
* npm start
* web app served in : http://localhost:8000/


### **How the browser application was build**

- Create an NPM project
    - Why:
        - To have a build automation tool.
        - To run node-based tools, such as the "bundler"
        - to include third-party libraries available on NPM
    - `npm init`
    - `npm install webpack-cli --save-dev`
- Copy source files to the `src` folder (by default webpack uses that as the JavaScript files location).
- Create `public/index.html` and setup a basic `html` document with an id `container`
    ```html
    <!DOCTYPE html>
    <html>
        <head>
            <title>Battleship</title>
            <script src="/main.js" type="module"></script>
        </head>
        <body>
            <h1>Battleship game</h1>
            <div id="container"></div>
        </body>
    </html>
    ```
- Rename the entry point module to `index.js` (again, it's the default webpack assumption).
    ```tsx
    import * as React from 'react'
    import { createRoot } from 'react-dom/client'
    function HelloMessage({ name }) {
        return (
            <div>Hello {name}</div>
        )
    }
    const root = createRoot(document.getElementById("container"))
    root.render(
        <HelloMessage name="Taylor" />
    )
    ```
- Automating the build and HTML serving process
    - `npm i webpack-dev-server --save-dev`.
    - Set `package.json` `scripts/start` to call `webpack serve`.
    - `webpack serve` keeps running and
        - Rebuilding the project when changes are detected.
        - Serving the updated bundle.
    - IMPORTANT: this is a development-time tool. In production, the bundled file will not be served using Webpack.
    ```js
        "scripts": {
        "start": "webpack serve"
        }
    ```
- The [TypeScript language](https://www.typescriptlang.org/)
    - JavaScript plus static type information.
    - Using in on our development workflow.
        - `npm i typescript ts-loader --save-dev`.
        - Edit `webpack.config.js` to configure a _loader_.
        ```js
        module.exports = {
            mode: "development",
            resolve: {
                extensions: [".js", ".ts", ".tsx"]
            },
            module: {
                rules: [
                    {
                        test: /\.tsx?$/,
                        use: 'ts-loader',
                        exclude: /node_modules/
                    }
                ]
            }
        }
        ```
        - Create the file `tsconfig.json` to configure the TypeScript compilation.
        ```json
        {
            "compilerOptions": {
                "jsx": "react",
                "target": "ES6",
                "moduleResolution": "node",
            }
        }
        ```
- React tools
    - `npm i react-router-dom`
    - The react-router-dom package contains bindings for using React Router in web applications.
    - `npm install react react-dom`
    - This package serves as the entry point to the DOM and server renderers for React. It is intended to be paired with the generic React package, which is shipped as react to npm.
    - `npm install --save @types/react`
    - This package contains type definitions for React
    - `npm install --save @types/react-dom`
    - This package contains type definitions for React (react-dom)
- The `package.json` should contain this dependencies
    ```json
    {
        ...
        "dependencies": {
            "react-dom": "^18.2.0",
            "react-router-dom": "^6.4.3"
        },
        "devDependencies": {
            "@types/react": "^18.0.25",
            "@types/react-dom": "^18.0.8",
            "ts-loader": "^9.4.1",
            "typescript": "^4.8.4",
            "webpack-cli": "^4.10.0",
            "webpack-dev-server": "^4.11.1"
        }
    }
    ```
- To build the browser application in the root directory type `npm install` and then run `npm start`
- In browser search for the host name [localhost:8080](http://localhost:8080/)