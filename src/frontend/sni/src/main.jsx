import { PayPalButtons, PayPalScriptProvider } from "@paypal/react-paypal-js";
import { createRoot } from 'react-dom/client'

import './index.css'
import Navbar from "./components/Navbar.jsx";
import SNIApp from './SNIApp';


createRoot(document.getElementById('root')).render(
  <>
    <Navbar/>
    <SNIApp/>
  </>
)
