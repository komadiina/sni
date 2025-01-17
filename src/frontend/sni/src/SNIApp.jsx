import './App.css'

import './index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import TokenExpired from "./pages/dashboard/TokenExpired.jsx";
import StripePayment from "./pages/stripe/TestStripePayment.jsx";
import StripePaymentCompletion from "./pages/stripe/StripePaymentCompletion.jsx";
import Login from './pages/Login'
import Register from './pages/Register'
import OTP from './pages/OTP';
import DashboardUser from './pages/dashboard/User';
import DashboardAdmin from './pages/dashboard/Admin';
import NotFound from './pages/NotFound';
import UserInfo from "./pages/UserInfo.jsx";

function SNIApp() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/otp" element={<OTP />} />

        <Route path="/dashboard" element={<DashboardUser />} />
        <Route path="/admin" element={<DashboardAdmin />} />
        <Route path="/user-info/:username" element={<UserInfo />} />

        <Route path="/token-expired" element={<TokenExpired />} />

        <Route path="/checkout/:productId" element={<StripePayment/>}/>
        <Route path="/completion" element={<StripePaymentCompletion/>}/>

        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  )
}

export default SNIApp
