import axios from "axios";

const api = axios.create({
  baseURL: "http://172.20.10.10:8080/", // IP da máquina para acesso via celular físico
  headers: {
    "Content-Type": "application/json",
  },
});

export const setApiBaseUrl = (url) => {
  api.defaults.baseURL = url.replace(/\/$/, "");
};

export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common["Authorization"];
  }
};

export default api;
