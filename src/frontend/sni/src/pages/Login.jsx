import api from "../api/axios";
import { useState } from "react";
import { AxiosError } from "axios";

const Login = () => {
  const [message, setMessage] = useState("");

  const signIn = async () => {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
      let data, status = {};
      const response = await api.post(
        `${import.meta.env.VITE_API_HOST}/auth/login`,
        JSON.stringify({ username, password })
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
        localStorage.setItem("username", username)
        window.location.href = "/otp"
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      setMessage(error.response.data.message);
    }
  }

  return (
    <div className="flex flex-col gap-4 items-start">
      <h1>Hello!</h1>
      <p className="text-2xl">Sign in to continue.</p>

      <input id="username" type="text" placeholder="Username" className="w-80 h-12 p-4 rounded-xl text-lg" />
      <input id="password" type="password" placeholder="Password" className="w-80 h-12 p-4 rounded-xl text-lg" />

      <button className="w-full h-12 rounded-xl bg-neutral-700 text-white text-lg" onClick={async () => { await signIn(); }}>Sign in</button>

      <hr />
      <p>{"Don't"} have an account? <a href="/register">Register here</a></p>

      {
        message !== "" &&
        <p className="text-lg text-red-400">{message}</p>
      }
    </div>
  )
}

export default Login;