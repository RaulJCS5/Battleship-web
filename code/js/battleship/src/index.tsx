import * as React from 'react'
import { CookiesProvider } from 'react-cookie'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { App } from './App'
import { AuthnContainer } from './context/AuthnContainer'


const root = createRoot(document.getElementById("container"))

root.render(
    /*<React.StrictMode>*/
        <BrowserRouter>
            <CookiesProvider>
                <AuthnContainer>
                        <App />
                </AuthnContainer>
            </CookiesProvider>
        </BrowserRouter>
    /*</React.StrictMode>*/
)
/** React Fetch API Being Called 2 Times on page load
It's because React renders components 2 times in the development environment. To avoid this, you can comment out the <React.StrictMode> tag in index.js file.

Rendering twice will only appear in the development environment and StrictMode has many benefits for development:

Identifying components with unsafe lifecycles
Warning about legacy string ref API usage
Warning about deprecated findDOMNode usage
Detecting unexpected side effects
Detecting legacy context API
Ensuring reusable state
So it's better to keep the <React.StrictMode> tag if it doesn't affect your normal development work.
 */