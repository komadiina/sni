import { useState } from "react";
import api from "../api/axios";


export default function OTP(props) {
  const [message, setMessage] = useState("");

  const submitOtp = async () => {
    try {
      const otp = document.querySelector("input").value.trim();
      let data, status = {};

      const response = await api.instance.post(
        `${ import.meta.env.VITE_API_HOST }/auth/otp?otp=${ otp }&username=${ localStorage.getItem("username") }`
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

        const token = data.additional.token;
        api.setToken(token);
        api.redirectToDashboard();
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      setMessage(error.response.data.message);
    }
  }

  return (
    <div className={ "flex items-center justify-center h-screen" }>
      <div className="flex flex-col items-start gap-4">
        <p className="text-2xl">2FA</p>
        <p className="italic">{ localStorage.getItem("message") ?? " " }</p>

        <input type="text" placeholder="Code" className="w-full h-12 p-4 rounded-xl text-lg"
          onKeyDown={(event) => {
            if (event.key === 'Enter')
              submitOtp();
          }}/>

        <button className="w-full h-12 rounded-xl bg-neutral-700 text-white text-lg" onClick={ () => {
          submitOtp();
        } }>Submit
        </button>

        {
          message &&
          <p className="italic text-red-600 text-lg">{ message }</p>

        }
      </div>
    </div>
  )
}
