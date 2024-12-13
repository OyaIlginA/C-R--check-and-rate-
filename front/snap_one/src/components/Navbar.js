import React from 'react';
import { Link, useNavigate } from 'react-router-dom';

function Navbar() {
    const navigate = useNavigate();

    // Logout fonksiyonu
    const handleLogout = () => {
        // sessionStorage'daki apiKey ve username'i temizle
        sessionStorage.removeItem('apiKey');
        sessionStorage.removeItem('username');
        
        // Kullanıcıyı login sayfasına yönlendir
        navigate('/login');
        alert('Logged out successfully');
    };

    return (
        <nav>
            <ul>
                <li>
                    <Link to="/">Home</Link>
                </li>
                <li>
                    <Link to="/login">Login</Link>
                </li>
                <li>
                    <Link to="/register">Register</Link>
                </li>
                <li>
                    <Link to="/profile">Profil</Link>
                </li>
                <li>
                    <button onClick={handleLogout}>Logout</button>
                </li>
            </ul>
        </nav>
    );
}

export default Navbar;
