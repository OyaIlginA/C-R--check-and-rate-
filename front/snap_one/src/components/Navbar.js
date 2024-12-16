import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css"; // CSS dosyasını import ediyoruz
import cavi from "./images/cavi.jpeg";
import login from "./images/login.jpg";
import register from "./images/register.jpg";
import home from "./images/home.jpg";
import logout from "./images/logout.jpg";
import profile from "./images/profile.jpg";
import list from "./images/list.png";

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
            <img src={home} alt="Akış" className="navbar-icon" />
            <span>Akış</span>
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/login" className="navbar-link">
            <img src={login} alt="Login" className="navbar-icon" />
            <span>Giriş</span>
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/register" className="navbar-link">
            <img src={register} alt="Register" className="navbar-icon" />
            <span>Kayıt Ol!</span>
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/profile" className="navbar-link">
            <img src={profile} alt="Profile" className="navbar-icon" />
            <span>Profil</span>
          </Link>
        </li>
        <li className="navbar-item">
          <Link to="/list" className="navbar-link">
            <img src={list} alt="Profile" className="navbar-icon" />
            <span>Listeler</span>
          </Link>
        </li>
      </ul>
      <div className="logout-btn-container">
        <button className="logout-btn" onClick={handleLogout}>
          <img src={logout} alt="Logout" className="navbar-icon" />
          <span>Çıkış</span>
        </button>
      </div>
    </nav>
  );
}

export default Navbar;
