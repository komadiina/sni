import axios from "axios";

export const api = {
  instance: axios.create({ baseURL: import.meta.env.VITE_API_HOST }),
  setToken: (token) => localStorage.setItem("jwt", token),
  getToken: () => localStorage.getItem("jwt"),
  isSignedIn: () => api.getToken() !== null && api.getToken() !== "undefined" && api.getToken() !== undefined,
  getRole: () => {
    if (api.isSignedIn()) {
      return JSON.parse(atob(api.getToken().split(".")[1])).role;
    }
  },
  clearToken: () => localStorage.removeItem("jwt"),
  redirectUnauthorized: () => {
    api.clearToken();
    window.location.href = "/login";
  },
  redirectToDashboard: () => {
    let role = api.getRole();
    console.log("role:" +role);

    if (api.getRole() === "0")
      window.location.href = "/admin";
    else if (api.getRole() === "1")
      window.location.href = "/dashboard"
    else window.location.href = "/login"
  },
  signOut: () => {
    api.instance.post("https://localhost:8443/api/auth/logout", {}, {headers: {Authorization: `Bearer ${api.getToken()}`}});

    api.clearToken();
    localStorage.removeItem('username')
    window.location.href = "/login";
  },
  extendToken: () => {
    api.instance.post(
      "https://localhost:8443/api/auth/extend-token",
      {},
      {headers: {Authorization: `Bearer ${api.getToken()}`}}
    ).then((res) => {
      console.log(res.data)
      api.redirectToDashboard();
    })
  }
};

api.instance.interceptors.request.use((config) => {
  const jwt = api.getToken();
  if (jwt) config.headers.Authorization = `Bearer ${api.getToken()}`;
  config.headers["Content-Type"] = "application/json";
  return config;
});

api.instance.interceptors.response.use(
  response => response,
  error => {
    if (error.response.status === 401 || error.response.status === 403) {
      window.location.href = '/token-expired'
    }
    return Promise.reject(error);
  }
);


export default api;
