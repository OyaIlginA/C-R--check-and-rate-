import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Register = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const handleRegister = async () => {
    try {
      const response = await axios.post("/auth/register", {
        username,
        password,
      });
      setMessage("User registered successfully!");
      navigate("/login"); // Kullanıcıyı login sayfasına yönlendir
    } catch (error) {
      setMessage("Registration failed!");
      console.error(error);
    }
  };

  return (
    <div className="typing-container">
      <h2>Sen De Aramıza Katıl!!</h2>
      <div className="login-form-container">
        <h2>Kayıt Ol</h2>
        <input
          type="text"
          placeholder="Kullanıcı Adı"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Şifre"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
        <button onClick={handleRegister}>Kayıt Ol</button>
        {message && <p>{message}</p>}
      </div>
    </div>
  );
};

export default Register;
