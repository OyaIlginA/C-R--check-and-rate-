import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./Auth.css";

function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [typingText, setTypingText] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post("/auth/login", {
        username,
        password,
      });

      // Backend'den API key ve User ID'yi al
      const apiKey = response.data
        .split("API Key: ")[1]
        .split("User ID")[0]
        .trim();
      const userId = response.data.split("User ID: ")[1].trim();

      // API Key ve kullanıcı adını sessionStorage'a kaydet
      sessionStorage.setItem("apiKey", apiKey);
      sessionStorage.setItem("username", username);
      sessionStorage.setItem("userId", userId);

      alert("Login successful");
      navigate("/"); // Başarılı giriş sonrası yönlendirme
    } catch (error) {
      setErrorMessage("Invalid credentials");
      console.error(error);
    }
  };

  useEffect(() => {
    const targetText = "Piuanlıyoruz ama yargılamıyoruz";
    let index = 0;

    const interval = setInterval(() => {
      setTypingText((prev) => {
        if (index < targetText.length) {
          return prev + targetText[index]; // Harfi ekle
        } else {
          clearInterval(interval); // Tüm harfler eklenince dur
          return prev; // Mevcut metni koru
        }
      });
      index++;
    }, 100);

    return () => clearInterval(interval); // Component unmount edilirse interval'ı temizle
  }, []);

  return (
    <div className="login-container">
      <div className="typing-container">
        <div className="typing-text">{typingText}</div>
      </div>
      <div className="login-form-container">
        <h2>Login</h2>
        <form onSubmit={handleSubmit}>
          <div>
            <label>Username</label>
            <input
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label>Password</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          {errorMessage && <div>{errorMessage}</div>}
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
}

export default Login;
