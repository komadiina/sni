import api from "../api/axios.js";
import '../styles/navbar.css'

export default function Navbar(props) {
  return (
    <div className={"flex flex-row items-start gap-4 fixed top-0 my-4 mx-2 left-0"}>
      {
        window.location.href !== 'https://localhost:3000/login'
        && window.location.href !== 'https://localhost:3000/'
        && <div className={ "nav-component" } onClick={() => {
          history.back()
        }}>
          <i className="fa-solid fa-arrow-left"/>
        </div>
      }

      {
        api.isSignedIn() &&
        api.getRole() === "0" &&
          <div className={"nav-component"} onClick={() => { window.location.href = "/admin" }}
          >
            Admin
          </div>
      }

      {
        api.isSignedIn()
          && <div className={ "nav-component" } onClick={ () => { window.location.href = "/dashboard" }}
        >
          Dashboard
        </div>
      }

      {
        api.isSignedIn()
        && <div className={ "nav-component" } onClick={() => {
          api.signOut()
        }}>
          Sign out
        </div>
      }
    </div>
  );
}
