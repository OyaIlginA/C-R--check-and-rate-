import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Profile() {
  const [userInfo, setUserInfo] = useState(null);
  const [averageScore, setAverageScore] = useState(null); // Ortalamayı tutacak state
  const navigate = useNavigate();

  useEffect(() => {
    // sessionStorage'dan kullanıcı bilgilerini al
    const apiKey = sessionStorage.getItem('apiKey');
    const username = sessionStorage.getItem('username');
    const userId = sessionStorage.getItem('userId');

    // Eğer gerekli bilgiler yoksa, login sayfasına yönlendir
    if (!apiKey || !username || !userId) {
      navigate('/login');
      return;
    }

    // Kullanıcı bilgilerini state'e kaydet
    setUserInfo({
      userId,
      username,
    });

    // Ortalama puanı almak için API isteği gönder
    const fetchAverageScore = async () => {
      try {
        const response = await fetch(`/api/users/${userId}/averageScore?api=${apiKey}&uname=${username}`);
        const data = await response.json();

        if (response.ok) {
          setAverageScore(data.averageScore); // Ortalamayı state'e kaydet
        } else {
          console.error('Error fetching average score:', data.error);
        }
      } catch (error) {
        console.error('Error:', error);
      }
    };

    fetchAverageScore(); // API'yi çağır
  }, [navigate]);

  return (
    <div style={{ padding: '20px' }}>
      <h2>Profile Page</h2>
      {userInfo ? (
        <div>
          <h3>Welcome, {userInfo.username}</h3>
          <img
            src={"images/cavi.jpg"}
            alt="Profile"
            style={{ width: '150px', height: '150px', borderRadius: '50%' }}
          />
          <p><strong>User ID:</strong> {userInfo.userId}</p>
          <p><strong>Username:</strong> {userInfo.username}</p>
          {averageScore !== null ? (
            <p><strong>Average Score:</strong> {averageScore}</p>
          ) : (
            <p>Loading average score...</p>
          )}
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
}

export default Profile;
