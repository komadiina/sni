import { useEffect, useState } from "react";
import api from "../api/axios.js";
import '../index.css';


const getUsers = async () => {
  let data = { };
  const [response] = await Promise.all([api.instance.get("/user").then((res) => { data = res.data })]);
  return data;
}

const getBalances = async (users) => {
  let data = {}

  for (const user of users) {
    let balance = 0.00;
    const [response] = await Promise.all([api.instance.get(`/balance/${user.username}`).then((res) => { balance = res.data.additional.balance; })]);

    data[user.username] = balance ?? 0.00;
  }


  console.log(data)
  return data;
}

const mapRole = (role) => {
  switch (role) {
    case 0:
      return "Admin";
    case 1:
      return "User";
    default:
      return "Unknown";
  }
}

export default function AdminPortal() {
  const [users, setUsers] = useState([]);
  const [balances, setBalances] = useState({});

  useEffect(() => {
    async function fetchData() {
      const apiResponse = await getUsers();
      const balanceResponse = await getBalances(apiResponse);
      return apiResponse;
    }

    fetchData().then(apiResponse => {
      setUsers(apiResponse);
    })
  }, []);

  return (
    <div className={"flex flex-col gap-2 items-center"}>
      {
        users.length > 0
          ? users.map((user, idx) => {
            const color = user.active ? "text-green-400" : "text-red-400";

            return (
              <div key={ idx }
                   className={ "flex flex-col gap-2 border-2 rounded-xl p-4 w-1/2 items-start hover-highlight clickable" }
                   onClick={() => { window.location.href = `/user-info/${user.username}` }}
                >
                <p className={ "text-4xl font-semibold" }>{ user.username } ({mapRole(user.role)})</p>
                {/*<p className={"text-lg mt-1"}>{ mapRole(user.role) }</p>*/}
                <p className={"text-xl mt-1"}>{ user.email }</p>
                <p className={"text-xl " + color}>{ user.active ? "Active." : "Inactive." }</p>
                <p className={"text-xl"}>{user.firstName} {user.lastName}, {user.contactPhone}, {user.billingAddress}</p>
              </div>
            )
          })
          : <p>No users found.</p>
      }
    </div>
  )
}
