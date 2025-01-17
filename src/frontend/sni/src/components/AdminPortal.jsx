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
  const [message, setMessage] = useState(null);

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

  const handleAddProduct = async (e) => {
    e.preventDefault();

    const productName = document.getElementById("productName").value;
    const productDescription = document.getElementById("productDescription").value;
    const productPrice = document.getElementById("productPrice").value;

    try {
      await api.instance.post(
        "/stripe/product",
        { name: productName, description: productDescription, price: productPrice },
          {headers: {Authorization: `Bearer ${api.getToken()}`}}
        )
        .then((res) => {
        setMessage("Product added successfully.");
      });
    } catch (err) {
      setMessage("Failed to add product.");
      console.error(err);
    }
  }

  return (
    <div>
    <div className={"grid grid-flow-row grid-cols-3 gap-2 my-12 items-start"}>
      {
        users.length > 0
          ? users.map((user, idx) => {
            const color = user.active ? "text-green-400" : "text-red-400";

            return (
              <div key={ idx }
                   className={ "w-full gap-2 border-2 rounded-xl p-4 items-start hover-highlight clickable" }
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
      {/* create product panel (name, description, price)*/}
      <div className={"flex flex-col items-start gap-2 w-1/2"}>
        <p className={"text-4xl font-semibold"}>Create Product</p>
        <p className={"text-xl"}>Create a new product for the store.</p>
        <input id={"productName"} className={"p-4 w-full rounded-xl bg-opacity-15 bg-amber-50 text-lg hover-highlight"} placeholder={"Product Name"} />
        <input id={"productDescription"} className={"p-4 w-full rounded-xl bg-opacity-15 bg-amber-50 text-lg hover-highlight"} placeholder={"Product Description"} />
        <input id={"productPrice"} className={"p-4 w-full rounded-xl bg-opacity-15 bg-amber-50 text-gl hover-highlight"} placeholder={"Product Price (e.g. 19.99)"} />
        <button className={"p-4 rounded-xl bg-opacity-15 bg-amber-50" }
                onClick={handleAddProduct}
        >
          Add
        </button>

        {
          message
            ? <p className={"text-xl"}>{message}</p>
            : <p></p>
        }
      </div>
    </div>
  )
}
