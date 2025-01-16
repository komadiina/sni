import { useState } from "react";
import api from "../api/axios";


const Register = (props) => {
  const [message, setMessage] = useState("");
  const [registrationSuccessful, setRegistrationSuccessful] = useState(false);

  const register = async () => {
    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const passwordConfirmation = document.getElementById("passwordConfirm").value;
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const contactPhone = document.getElementById("contactPhone").value;
    const active = true;
    const billingAddress = document.getElementById("billingAddress").value;

    try {
      let data, status = {};
      const response = await api.instance.post(
        `${ import.meta.env.VITE_API_HOST }/auth/register`,
        JSON.stringify({
          username,
          email,
          password,
          passwordConfirmation,
          firstName,
          lastName,
          contactPhone,
          active,
          billingAddress
        })
      ).then((response) => {
          data = response.data;
          status = response.status;
        })
        .catch((error) => {
          data = error.response.data;
          status = error.response.status;
        });

      if (status === 200) {
        localStorage.setItem("message", data.message);
        setRegistrationSuccessful(true);
        setTimeout(() => {
        }, 3000);
        window.location.href = "/login";
      } else {
        setMessage(data.message);
        setRegistrationSuccessful(false);
      }
    } catch (error) {
      setMessage(error.response.data.message);
    }
  }

  return (
    <div className={ "flex items-center justify-center h-screen" }>
      <div className="flex flex-col items-start gap-4 max-w-screen-md w-screen">
        <h1 className="my-4">Registration</h1>
        <input id="username" type="text" placeholder="Username" className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* email */ }
        <input id="email" type="text" placeholder="Email" className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* password */ }
        <input id="password" type="password" placeholder="Password" className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* confirmPassword */ }
        <input id="passwordConfirm" type="password" placeholder="Confirm Password"
               className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* first name */ }
        <input id="firstName" type="text" placeholder="First Name" className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* lastName */ }
        <input id="lastName" type="text" placeholder="Last Name" className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* contact phone */ }
        <input id="contactPhone" type="text" placeholder="Contact Phone (e.g. +38766...)"
               className="w-full h-12 p-4 rounded-xl text-xl"/>

        {/* billing address */ }
        <input id="billingAddress" type="text" placeholder="Billing Address"
               className="w-full h-12 p-4 rounded-xl text-xl"/>

        <button className="my-4 w-full h-12 rounded-xl bg-neutral-700 text-white text-xl" onClick={ () => {
          register();
        } }>Register
        </button>

        {
          message && (
            <p className="text-2xl text-red-600">{ message }</p>
          )
        }

        {
          registrationSuccessful == true && (
            <p>Success! You can <a href="/login">sign in now.</a></p>
          )
        }
      </div>
    </div>
  )
}

export default Register;
