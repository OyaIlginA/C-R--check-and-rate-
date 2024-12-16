import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import star_photo from "./images/star.jpg";
import "./Profile.css";

function Profile() {
  const [userInfo, setUserInfo] = useState(null);
  const [averageScore, setAverageScore] = useState(null); // Ortalama puanı tutacak state
  const [photos, setPhotos] = useState([]); // Fotoğrafları tutacak state
  const [file, setFile] = useState(null); // Yüklenecek dosyayı tutacak state
  const [uploadMessage, setUploadMessage] = useState(""); // Yükleme durumu mesajı
  const navigate = useNavigate();

  // Fotoğrafları almak için fonksiyon
  const fetchPhotos = async () => {
    const apiKey = sessionStorage.getItem("apiKey");
    const username = sessionStorage.getItem("username");
    const userId = sessionStorage.getItem("userId");

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

  // Kullanıcı bilgilerini ve fotoğrafları almak için useEffect
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

    // Fotoğrafları ve puanları almak için çağır
    fetchPhotos();
    fetchAverageScore();
  }, [navigate]);

  // Fotoğraf yükleme işlemi
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
        // Fotoğraf yüklendikten sonra listeyi güncelle
        fetchPhotos(); // Re-fetch photos after uploading
      } else {
        setUploadMessage("Failed to upload photo.");
      }
    } catch (error) {
      console.error("Error uploading photo:", error);
      setUploadMessage("Error occurred while uploading photo.");
    }
  };

  // Fotoğraf silme işlemi
  // Fotoğraf silme işlemi
  const handlePhotoDelete = async (photoId) => {
    const apiKey = sessionStorage.getItem("apiKey");
    const username = sessionStorage.getItem("username");

    try {
      const response = await fetch(
        `/api/photos/${photoId}?api=${apiKey}&uname=${username}`,
        {
          method: "DELETE", //Burası 403 dönüyor g.o silmiyorsun işte amaaaa
        }
      );

      if (response.ok) {
        // Fotoğraf silindikten sonra listeyi tekrar güncelle
        setPhotos(photos.filter((photo) => photo !== photoId)); // Re-fetch photos after deletion to ensure it's up to date
        setUploadMessage("Photo deleted successfully!");
        console.log("response.ok");
      } else {
        // Eğer backend'den bir hata dönerse
        const data = await response.json();
        setUploadMessage(`Failed to delete photo: ${data.error}`);
        console.log("response.error");
      }
    } catch (error) {
      console.error("Error deleting photo:", error);
      setUploadMessage("Error occurred while deleting photo.");
      console.log("func.error");
    }
  };

  return (
    <div style={{ padding: "20px" }}>
      <h2>Profil Sayfan</h2>
      {userInfo ? (
        <div>
          <h3 className="title">Hoşgeldin, {userInfo.username}</h3>
          <img
            src={star_photo}
            alt="Profile"
            style={{ width: "150px", height: "150px", borderRadius: "50%" }}
          />
          <p>
            <strong>Kullanıcı ID:</strong> {userInfo.userId}
          </p>
          <p>
            <strong>Kullanıcı Adı:</strong> {userInfo.username}
          </p>
          {averageScore !== null ? (
            <p>
              <strong>Ortalama Skor:</strong> {averageScore}
            </p>
          ) : (
            <p>Ortalama skor yükleniyor...</p>
          )}

          <h3>Senin Arşivin^^ </h3>
          <div className="photo-grid">
            {photos.map((photoId) => (
              <div key={photoId} className="photo-container">
                <img
                  className="photo"
                  src={`/api/photos/${photoId}?api=${sessionStorage.getItem(
                    "apiKey"
                  )}&uname=${sessionStorage.getItem(
                    "username"
                  )}&timestamp=${new Date().getTime()}`}
                />
                <button
                  className="delete-button"
                  onClick={() => handlePhotoDelete(photoId)}
                >
                  X
                </button>
              </div>
            ))}
          </div>

          <h3>Bir Fotoğraf Daha Yükle!</h3>
          <form onSubmit={handlePhotoUpload}>
            <input
              type="file"
              accept="image/*"
              onChange={(e) => setFile(e.target.files[0])}
            />
            <button className="upload-button" type="submit">
              Yükle Hadii
            </button>
            <h5>Yükle yükle,yargılamıyoruz ^_'</h5>
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
