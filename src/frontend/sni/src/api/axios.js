import axios from "axios"

let axiosConfig = {
  headers: {
    'Content-Type': 'application/json',
    'Accept': '*/*',
    // 'Accept-Encoding': 'gzip, deflate, br',
    // 'Connection': 'keep-alive',
  },
  baseURL: 'https://localhost:8443',
  url: '/api'
}

const redirectUnauthorized = async () => {
  localStorage.clear();
  axiosConfig.headers.Authorization = null;

  window.location.href = "/login";
}

const api = {
  get: async (url) => axios.get(url, axiosConfig).catch(error => {
    redirectUnauthorized(error);
  }),

  post: async (url, data) => axios.post(url, data, axiosConfig).catch(error => {
    redirectUnauthorized(error);
  }),

  put: async (url, data) => axios.put(url, data, axiosConfig).catch(error => {
    redirectUnauthorized(error);
  }),

  delete: async (url) => axios.delete(url, axiosConfig).catch(error => {
    redirectUnauthorized(error);
  }),

  setToken: async (token) => axiosConfig.headers.Authorization = `Bearer ${token}`,
  setRole: async (role) => localStorage.setItem("role", role),

  redirectToDashboard: async () => {
    if (localStorage.getItem("role") === "0") {
      window.location.href = "/dashboard/admin";
    } else if (localStorage.getItem("role") === "1") {
      window.location.href = "/dashboard/user";
    } else {
      window.location.href = "/login";
    }
  }
}

export default api;