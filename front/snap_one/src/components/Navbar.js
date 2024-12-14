import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css"; // CSS dosyasını import ediyoruz
import cavi from "./images/cavi.jpeg";

function Navbar() {
  const navigate = useNavigate();

  // Logout fonksiyonu
  const handleLogout = () => {
    // sessionStorage'daki apiKey ve username'i temizle
    sessionStorage.removeItem("apiKey");
    sessionStorage.removeItem("username");

    // Kullanıcıyı login sayfasına yönlendir
    navigate("/login");
    alert("Logged out successfully");
  };

  return (
    <nav className="navbar">
      <ul className="navbar-list">
        <li className="navbar-item">
          <Link to="/" className="navbar-link">
            <img src={cavi} alt="Home" className="navbar-icon" />
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/login" className="navbar-link">
            <img src={cavi} alt="Login" className="navbar-icon" />
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/register" className="navbar-link">
            <img src={cavi} alt="Register" className="navbar-icon" />
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/profile" className="navbar-link">
            <img src={cavi} alt="Profile" className="navbar-icon" />
          </Link>
        </li>
        <li className="navbar-item">
          <button onClick={handleLogout} className="navbar-link logout-btn">
            <img src="/images/cavi.jpeg" alt="Logout" className="navbar-icon" />
          </button>
        </li>
      </ul>
    </nav>
  );
}

export default Navbar;
