import './App.css'

import './index.css'
import { BrowserRouter, Routes, Route } from "react-router-dom";
import Login from './pages/Login'
import Register from './pages/Register'
import OTP from './pages/OTP';
import DashboardUser from './pages/dashboard/User';
import DashboardAdmin from './pages/dashboard/Admin';
import NotFound from './pages/NotFound';

function SNIApp() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/otp" element={<OTP />} />
        <Route path="/dashboard/user" element={<DashboardUser />} />
        <Route path="/dashboard/admin" element={<DashboardAdmin />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  )
}

export default SNIApp
