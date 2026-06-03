import { createContext, useState, useContext } from "react";
import { setAuthToken, setApiBaseUrl } from "../api/cliente";

const AuthContext = createContext({});

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [baseUrl, setBaseUrl] = useState("http://172.20.10.10:8080");

  const login = (userData, userToken) => {
    setUser(userData);
    setToken(userToken);
    setAuthToken(userToken);
  };

  const logout = () => {
    setUser(null);
    setToken(null);
    setAuthToken(null);
  };

  const updateBaseUrl = (url) => {
    const cleanUrl = url.trim().replace(/\/$/, "");
    setBaseUrl(cleanUrl);
    setApiBaseUrl(cleanUrl);
  };

  return (
    <AuthContext.Provider
      value={{ user, token, baseUrl, login, logout, updateBaseUrl }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
