import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import star_photo from "./images/star.jpg";

function Profile() {
  const [userInfo, setUserInfo] = useState(null);
  const [averageScore, setAverageScore] = useState(null); // Ortalama puanı tutacak state
  const [photos, setPhotos] = useState([]); // Fotoğrafları tutacak state
  const [file, setFile] = useState(null); // Yüklenecek dosyayı tutacak state
  const [uploadMessage, setUploadMessage] = useState(""); // Yükleme durumu mesajı
  const navigate = useNavigate();

  useEffect(() => {
    const apiKey = sessionStorage.getItem("apiKey");
    const username = sessionStorage.getItem("username");
    const userId = sessionStorage.getItem("userId");

    // Eğer gerekli bilgiler yoksa, login sayfasına yönlendir
    if (!apiKey || !username || !userId) {
      navigate("/login");
      return;
    }

    // Kullanıcı bilgilerini state'e kaydet
    setUserInfo({
      userId,
      username,
    });

    // Kullanıcıya ait fotoğrafları çek
    const fetchPhotos = async () => {
      try {
        const response = await fetch(
          `/api/photos/user/${userId}?api=${apiKey}&uname=${username}`
        );
        const data = await response.json();
        if (response.ok) {
          setPhotos(data); // Fotoğraf ID'lerini kaydet
        } else {
          console.error("Error fetching photos:", data.error);
        }
      } catch (error) {
        console.error("Error:", error);
      }
    };

    // Ortalama puanı almak için API isteği
    const fetchAverageScore = async () => {
      try {
        const response = await fetch(
          `/api/users/${userId}/averageScore?api=${apiKey}&uname=${username}`
        );
        const data = await response.json();
        if (response.ok) {
          setAverageScore(data.averageScore);
        } else {
          console.error("Error fetching average score:", data.error);
        }
      } catch (error) {
        console.error("Error:", error);
      }
    };

    // API'leri çağır
    fetchPhotos();
    fetchAverageScore();
  }, [navigate]);

  // Fotoğraf yükleme işlemi
  const handlePhotoUpload = async (e) => {
    e.preventDefault();

    if (!file) {
      setUploadMessage("Please select a file to upload.");
      return;
    }

    const apiKey = sessionStorage.getItem("apiKey");
    const username = sessionStorage.getItem("username");
    const userId = sessionStorage.getItem("userId");

    const formData = new FormData();
    formData.append("file", file);
    formData.append("api", apiKey);
    formData.append("uname", username);
    formData.append("ownerId", userId);

    try {
      const response = await fetch(`/api/photos/upload`, {
        method: "POST",
        body: formData,
      });

      if (response.ok) {
        const data = await response.json();
        setUploadMessage("Photo uploaded successfully!");
        setFile(null);
      } else {
        setUploadMessage("Failed to upload photo.");
      }
    } catch (error) {
      console.error("Error uploading photo:", error);
      setUploadMessage("Error occurred while uploading photo.");
    }

    try {
      const response = await fetch("/api/photos/${photoId}", {
        method: "DELETE",
      });
      if (response.ok) {
        const data = await response.json();
        setUploadMessage("Photo deleted successfully!");
        setFile(null);
      }
    } catch (error) {
      console.error("Error deleting photo:", error);
      setUploadMessage("Error occurred while deleting photo.");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Profile Page</h2>
      {userInfo ? (
        <div>
          <h3>Welcome, {userInfo.username}</h3>
          <img
            src={star_photo}
            alt="Profile"
            style={{ width: "150px", height: "150px", borderRadius: "50%" }}
          />
          <p>
            <strong>User ID:</strong> {userInfo.userId}
          </p>
          <p>
            <strong>Username:</strong> {userInfo.username}
          </p>
          {averageScore !== null ? (
            <p>
              <strong>Average Score:</strong> {averageScore}
            </p>
          ) : (
            <p>Loading average score...</p>
          )}

          <h3>Your Photos</h3>
          <div style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}>
            {photos.map((photoId) => (
              <img
                key={photoId}
                src={`/api/photos/${photoId}?api=${sessionStorage.getItem(
                  "apiKey"
                )}&uname=${sessionStorage.getItem("username")}`}
                alt={`Photo ${photoId}`}
                style={{ width: "150px", height: "150px", objectFit: "cover" }}
              />
            ))}
          </div>

          <h3>Upload a Photo</h3>
          <form onSubmit={handlePhotoUpload}>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setFile(e.target.files[0])}
            />
            <button type="submit">Upload</button>
          </form>
          {uploadMessage && <p>{uploadMessage}</p>}
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </div>
  );
}

export default Profile;
