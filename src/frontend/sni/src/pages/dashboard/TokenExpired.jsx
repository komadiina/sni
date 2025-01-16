import { useEffect, useState } from "react";
import api from "../../api/axios.js";


export default function TokenExpired(props) {

  const handleExtend = async () => {
    try {
      await api.extendToken();
    } catch (err) {
      console.error('Unable to refresh token: ' + err)
      window.location.href = '/login'
    }
  }

  const handleLogout = async () => {
    api.signOut()
    localStorage.removeItem('jwt')
    window.location.href = '/login'
  }

  const [hasToken, setHasToken] = useState(false);
  useEffect(() => {
    if (localStorage.getItem('jwt') && localStorage.getItem('jwt') !== 'undefined') {
      setHasToken(true);
    }
  }, []);

  if (hasToken) return (
      <div className={ "flex items-start flex-col gap-2 w-1/2" }>
        <p className={ "text-4xl font-semibold" }>Session expired.</p>
        <p className={ "text-2xl" }>Your session has expired. Would you like to extend it?</p>
        <div className={ "flex flex-row items-start gap-8 w-full my-4 text-xl" }>
          <button
            className={ "p-3 rounded-xl bg-opacity-10 bg-green-300 hover:cursor-pointer hover:bg-opacity-20 duration-200 transition-all" }
            onClick={ handleExtend }
          >
            Extend Session
          </button>
          <button
            className={ "p-3 rounded-xl  bg-opacity-10 bg-red-300 hover:cursor-pointer hover:bg-opacity-20 duration-200 transition-all" }
            onClick={ handleLogout }
          >
            Log out
          </button>
        </div>
      </div>
  );
  if (!hasToken) return (
    <div className={ "flex items-start flex-col gap-4 w-1/2" }>
      <p className={ "text-4xl font-semibold" }>No valid session exists.</p>
      <button
        className={ "text-2xl p-3 rounded-xl bg-opacity-15 bg-teal-50" }
        onClick={() => { window.location.href = '/login'; }}
      >
        Back to login
      </button>
    </div>
  )
}
