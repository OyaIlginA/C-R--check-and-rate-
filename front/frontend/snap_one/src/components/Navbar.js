import React from 'react';
import { Link } from 'react-router-dom';

const Navbar = () => {
  return (
    <div className="navbar">
      <div className="navbar-content">
        <Link to="/" className="nav-link">Anasayfa</Link>
        <Link to="/login" className="nav-link">Giriş Yap</Link>
        <Link to="/register" className="nav-link">Kayıt Ol</Link>
      </div>
    </div>
  );
};

export default Navbar;
