import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import api from "../api/axios.js";
import "../index.css"


const fetchUserData = async (username) => {
  // fetch user info
  let data = {}

  try {
    await Promise.all([
      api.instance.get(`/user/${ username }`).then((res) => {
        data["user"] = res.data;
      })
    ]);

    // fetch balance
    await Promise.all([
      api.instance.get(`/balance/${ username }`).then((res) => {
        data["balance"] = res.data.additional.balance ?? 0.00;
      })
    ]);
  } catch (err) {
    console.error(err)
  }

  return data;
}

const mapRole = (role) => {
  switch (role) {
    case "0":
      return "Admin";
    case "1":
      return "User";
    default:
      return "Unknown";
  }
}

const mapBackRole = (roleText) => {
  switch (roleText) {
    case "Admin":
      return "0";
    case "User":
      return "1";
    default:
      return "0";
  }
}

const mapActive = (active) => {
  // xddddddd
  switch (active) {
    case "Yes":
      return true;
    case "yes":
      return true;
    case "No":
      return false;
    case "no":
      return false;
  }
}


export default function UserInfo(props) {
  const username = useParams().username
  const [info, setInfo] = useState({});
  const [isEdited, setIsEdited] = useState(false);
  const [responseText, setResponseText] = useState("")

  const elements = document.getElementsByName("input")
  elements.forEach((element) => {
    element.addEventListener("onChange", () => {
      setIsEdited(true);
    });
  })

  useEffect(() => {
    const fetchData = async () => {
      return await fetchUserData(username);
    }

    fetchData().then(data => {
      setInfo(data);
    })
  }, []);

  const handleEdit = (field, ev) => {
    setInfo({
      ...info,
      [field]: ev.target.value
    })

    setIsEdited(true)
  }

  const saveChanges = async () => {
    const preprocess = (user) => {
      return {
        username: user.username,
        email: user.email,
        firstName: user.firstName,
        lastName: user.lastName,
        contactPhone: user.contactPhone,
        active: mapActive(user.active),
        billingAddress: user.billingAddress,
        role: mapBackRole(user.role)
      }
    }

    try {
      await Promise.all([
        api.instance.put(`/user/${info.user.username}`, JSON.stringify(preprocess(info.user))).then((res) => {
          setResponseText(res.data.message)
        })
      ])
    } catch (err) {
      setResponseText(err)
    }
  }


  return (
    <div>
      {
        info.user && info.balance
        && <div className={ "flex flex-col gap-4 items-start" }>
          {
            isEdited &&
            <div className={ "flex flex-row gap-4 items-start " }>
              <button className={ "p-4 bg-opacity-15 bg-teal-100 pl-8 pr-8 font-semibold text-2xl" }
                      onClick={() => { saveChanges(); }}
              >
                Save
              </button>
            </div>
          }

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("username").focus();
               } }>
            <p className="text-2xl font-semibold">Username</p>
            <input type={ "text" } id={ "username" } defaultValue={ info.user.username }
                   onChange={ (ev) => {
                     handleEdit("username", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("email").focus();
               } }>
            <p className="text-2xl font-semibold">Email</p>
            <input type={ "text" } id={ "email" } defaultValue={ info.user.email }
                   onChange={ (ev) => {
                     handleEdit("email", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("role").focus();
               } }>
            <p className="text-2xl font-semibold">Role</p>
            <input type={ "text" } id={ "role" } defaultValue={ mapRole(info.user.role) }
                   onChange={ (ev) => {
                     handleEdit("role", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("active").focus();
               } }>
            <p className="text-2xl font-semibold">Active</p>
            <input type={ "text" } id={ "active" } defaultValue={ info.user.active ? "Yes" : "No" }
                   onChange={ (ev) => {
                     handleEdit("active", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("firstName").focus();
               } }>
            <p className="text-2xl font-semibold">First name</p>
            <input type={ "text" } id={ "firstName" } defaultValue={ info.user.firstName ? info.user.firstName : "N/A" }
                   onChange={ (ev) => {
                     handleEdit("firstName", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("lastName").focus();
               } }>
            <p className="text-2xl font-semibold">Last name</p>
            <input type={ "text" } id={ "lastName" } defaultValue={ info.user.lastName ? info.user.lastName : "N/A" }
                   onChange={ (ev) => {
                     handleEdit("lastName", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("billingAddress").focus();
               } }>
            <p className="text-2xl font-semibold">Billing Address</p>
            <input type={ "text" } id={ "billingAddress" }
                   defaultValue={ info.user.billingAddress ? info.user.billingAddress : "N/A" }
                   onChange={ (ev) => {
                     handleEdit("billingAddress", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("contactPhone").focus();
               } }>
            <p className="text-2xl font-semibold">Contact Phone</p>
            <input type={ "text" } id={ "contactPhone" }
                   defaultValue={ info.user.contactPhone ? info.user.contactPhone : "N/A" }
                   onChange={ (ev) => {
                     handleEdit("contactPhone", ev)
                   } }/>
          </div>

          <div className={ "user-info-editable" }
               onClick={ () => {
                 document.getElementById("balance").focus();
               } }>
            <p className="text-2xl font-semibold">Balance</p>
            <input type={ "text" } id={ "balance" } defaultValue={ info.balance }
                   onChange={ (ev) => {
                     handleEdit("balance", ev)
                   } }/>
          </div>
        </div>
      }
    </div>
  )
}
