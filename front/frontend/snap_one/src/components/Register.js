import React, { useState } from 'react';
import axios from 'axios';
import  { useNavigate } from 'react-router-dom';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate();  // Yönlendirme için kullanılır

  // Kullanıcı kaydı
  const handleRegister = async () => {
    try {
      const response = await axios.post('/auth/register', {
        username,
        password,
      });
      setMessage('User registered successfully!');
      navigate('/login');  // logine yönlendir
    } catch (error) {
      setMessage('Registration failed!');
      console.error(error);
    }
  };

  return (
    <div>
      <h2>Register</h2>
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
        <button onClick={handleRegister}>Register</button>
      </div>
      {message && <p>{message}</p>}
    </div>
  );
};

export default Register;
