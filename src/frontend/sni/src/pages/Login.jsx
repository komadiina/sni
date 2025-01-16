import api from "../api/axios";
import { useEffect, useState } from "react";

const Login = () => {
  const [message, setMessage] = useState("");

  useEffect(() => {
    const fetchSignedIn = async () => {
      return await api.autoLogin();
    }

    api.autoLogin().then((res) => {
      if (res) {
        window.location.href = "/dashboard";
      }
    })
  }, []);


  const signIn = async () => {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    try {
      let data, status = {};
      let alreadySignedIn = false;

      const response = await api.instance.post(
        `${import.meta.env.VITE_API_HOST}/auth/login`,
        JSON.stringify({ username, password })
      ).then((response) => {
        data = response.data;
        status = response.status;
        // console.log(response.data.additional.token)

        if (status === 202) {
          api.setToken(response.data.additional.token)
          console.log(api.getToken())
          alreadySignedIn = true;
        }
      })
        .catch((error) => {
          console.log(error);
          // data = error.response.data;
          status = error.response.status;
        });

      if (alreadySignedIn)
      {
        window.location.href = '/dashboard'
        return;
      }

      localStorage.removeItem('jwt');

      if (status === 200) {
        localStorage.setItem("message", data.message);
        localStorage.setItem("username", username)
        window.location.href = "/otp"
      } else {
        setMessage(data.message);
      }
    } catch (error) {
      console.log(error);
      setMessage("An unknown error happened.");
    }
  }

  return (
    <div className={"flex items-center justify-center h-screen"}>
      <div className="flex flex-col gap-4 items-start">
        <h1 className={"text-4xl"}>Hello!</h1>
        <p className="text-2xl">Sign in to continue.</p>

        <input id="username" type="text" placeholder="Username" className="w-80 h-12 p-4 rounded-xl text-lg"
               onKeyDown={async (event) => { if (event.key === "Enter") { await signIn(); } }}/>
        <input id="password" type="password" placeholder="Password" className="w-80 h-12 p-4 rounded-xl text-lg"
               onKeyDown={async (event) => { if (event.key === "Enter") { await signIn(); } }}/>

        <button className="w-32 h-12 rounded-xl bg-neutral-700 text-white text-lg"
                onClick={async () => { await signIn(); }}
        >
          Sign in
        </button>

        <hr />
        <p>{"Don't"} have an account? <a href="/register">Register here</a></p>

        {
          message !== "" &&
          <p className="text-lg text-red-400">{message}</p>
        }
      </div>
    </div>
  )
}

export default Login;
