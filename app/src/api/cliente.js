import axios from "axios";

export const DEFAULT_API_BASE_URL = "http://172.20.10.10:8080";

export const normalizeApiBaseUrl = (url) => {
  const cleanUrl = url.trim().replace(/\/$/, "");

  if (!cleanUrl) {
    return DEFAULT_API_BASE_URL;
  }

  const urlWithProtocol = /^https?:\/\//i.test(cleanUrl)
    ? cleanUrl
    : `http://${cleanUrl}`;

  return /:\d+(\/|$)/.test(urlWithProtocol)
    ? urlWithProtocol
    : `${urlWithProtocol}:8080`;
};

const api = axios.create({
  baseURL: DEFAULT_API_BASE_URL, // IP da máquina para acesso via celular físico
  headers: {
    "Content-Type": "application/json",
  },
});

export const setApiBaseUrl = (url) => {
  api.defaults.baseURL = normalizeApiBaseUrl(url);
};

export const setAuthToken = (token) => {
  if (token) {
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common["Authorization"];
  }
};

export default api;