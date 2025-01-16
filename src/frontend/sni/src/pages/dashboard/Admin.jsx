import api from "../../api/axios"
import AdminPortal from "../../components/AdminPortal.jsx";
import Navbar from "../../components/Navbar.jsx";

const role = async () => {
  const role = await api.getRole();
  await Promise.all([role]);

  return await role;
}

export default function DashboardAdmin(props) {
  console.log(api.getToken());

  return (
    <div>
      <Navbar/>
      <AdminPortal/>
    </div>
  )
}
