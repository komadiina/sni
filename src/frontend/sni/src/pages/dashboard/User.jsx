import { useEffect, useState } from "react";
import api from "../../api/axios.js";
import BuyMenu from "../../components/BuyMenu.jsx";
import Navbar from "../../components/Navbar.jsx";


export default function DashboardUser(props) {

  return (
    <div className={""}>
      <Navbar/>
      <div> </div>
      <BuyMenu/>
    </div>
  )
}
