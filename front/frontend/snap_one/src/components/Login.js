import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const Login = ({ onLoginSuccess }) => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();  // Yönlendirme için kullanılır

  const handleLogin = async () => {
    try {
      const response = await axios.post('/auth/login', {
        username,
        password,
      });
      setMessage('Login successful!');
      onLoginSuccess();  // Giriş başarılı olduğunda bu fonksiyon çağrılır
      navigate('/');  // Anasayfaya yönlendir
    } catch (error) {
      setMessage('Login failed!');
      console.error(error);
    }
  };

  return (
    <div>
      <h2>Login</h2>
      <div>
        <input
          type="text"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />
      </div>
      <div>
        <button onClick={handleLogin}>Login</button>
      </div>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Login;
