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
    // sessionStorage'dan kullanıcı bilgilerini al
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

    // Ortalama puanı almak için API isteği gönder
    const fetchAverageScore = async () => {
      try {
        const response = await fetch(
          `/api/users/${userId}/averageScore?api=${apiKey}&uname=${username}`
        );
        const data = await response.json();

        if (response.ok) {
          setAverageScore(data.averageScore); // Ortalamayı state'e kaydet
        } else {
          console.error("Error fetching average score:", data.error);
        }
      } catch (error) {
        console.error("Error:", error);
      }
    };

    // Kullanıcının fotoğraflarını almak için API isteği
    const fetchUserPhotos = async () => {
      try {
        const response = await fetch(
          `/api/photos/user/${userId}?api=${apiKey}&uname=${username}`
        );
        const data = await response.json();

        if (response.ok) {
          setPhotos(data); // Fotoğrafları state'e kaydet
        } else {
          console.error("Error fetching user photos:", data.error);
        }
      } catch (error) {
        console.error("Error:", error);
      }
    };

    // API'leri çağır
    fetchAverageScore();
    fetchUserPhotos();
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

        // Fotoğrafları yeniden yükle
        const updatedPhotosResponse = await fetch(
          `/api/photos/all?api=${apiKey}&uname=${username}`
        );
        const updatedPhotos = await updatedPhotosResponse.json();
        setPhotos(updatedPhotos); // Fotoğraf listesini güncelle
      } else {
        setUploadMessage("Failed to upload photo.");
      }
    } catch (error) {
      console.error("Error uploading photo:", error);
      setUploadMessage("Error occurred while uploading photo.");
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
          {photos.length > 0 ? (
            <div style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}>
              {photos.map((photo) => (
                <div
                  key={photo.id}
                  style={{ border: "1px solid #ddd", padding: "10px" }}
                >
                  <img
                    src={photo.url} // Fotoğrafın URL'si
                    alt={photo.description || "User photo"}
                    style={{
                      width: "150px",
                      height: "150px",
                      objectFit: "cover",
                    }}
                  />
                  <p>{photo.description}</p>
                </div>
              ))}
            </div>
          ) : (
            <p>No photos available.</p> // Fotoğraf yoksa mesaj
          )}

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
