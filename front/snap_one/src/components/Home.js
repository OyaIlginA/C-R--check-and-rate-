import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Home() {
    const [userInfo, setUserInfo] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const apiKey = sessionStorage.getItem('apiKey');
        const username = sessionStorage.getItem('username');

        if (!apiKey || !username) {
            // Kullanıcı giriş yapmamışsa login sayfasına yönlendir
            navigate('/login');
            return;
        }

        // API key ve username doğrulaması yapıldığında, kullanıcı bilgilerini yükle
        const fetchUserInfo = async () => {
            try {
                const response = await fetch('/auth/user', {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${apiKey}`, // API key'i header'a ekleyerek gönderiyoruz
                    }
                });
                const data = await response.json();
                setUserInfo(data);
            } catch (error) {
                console.error('Error fetching user info:', error);
            }
        };

        fetchUserInfo();
    }, [navigate]);

    return (
        <div>
            <h2>Dashboard</h2>
            {userInfo ? (
                <div>
                    <h3>Welcome, {userInfo.username}</h3>
                    {/* Kullanıcı bilgilerini burada göster */}
                </div>
            ) : (
                <p>Loading...</p>
            )}
        </div>
    );
}

export default Home;
