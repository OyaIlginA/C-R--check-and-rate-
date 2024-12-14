import React, { useEffect, useState } from "react";
import "./Home.css";
import { useNavigate } from "react-router-dom";
import Modal from "react-bootstrap/Modal";
import Button from "react-bootstrap/Button";

function Home() {
  const [userInfo, setUserInfo] = useState(null);
  const [photos, setPhotos] = useState([]); // Fotoğrafları tutacak state
  const [selectedPhoto, setSelectedPhoto] = useState(null); // Seçilen fotoğraf
  const [rating, setRating] = useState(""); // Puan
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
      apiKey, // API key de state'e eklenmeli
    });

    // Kullanıcıya ait fotoğrafları çek
    const fetchPhotos = async () => {
      try {
        const response = await fetch(
          `/api/photos/all?api=${apiKey}&uname=${username}`
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

    fetchPhotos(); // fetchPhotos'u çağırıyoruz
  }, [navigate]); // 'navigate' dependency'si eklendi

  // Fotoğraf seçildiğinde çalışacak fonksiyon
  const handleImageClick = (photo) => {
    setSelectedPhoto({ id: photo });
  };

  const handleSubmitRating = async () => {
    if (selectedPhoto && rating) {
      const { userId, apiKey, username } = userInfo || {}; // userInfo'dan çekiliyor

      try {
        const response = await fetch(
          `/api/photos/${selectedPhoto.id}/rate?uid=${userId}&score=${rating}&api=${apiKey}&uname=${username}`,
          {
            method: "POST",
          }
        );

        if (response.ok) {
          alert("Rating submitted successfully!");
          setRating(""); // Puanlama sonrası input temizlenir
          setSelectedPhoto(null); // Modal kapatılır
        } else {
          const errorData = await response.json();
          alert(`Failed to submit rating: ${errorData.message}`);
        }
      } catch (error) {
        console.error("Error while submitting rating:", error);
        alert("An error occurred while submitting your rating.");
      }
    } else {
      alert("Please select a photo and provide a rating.");
    }
  };

  const handleRatingChange = (e) => {
    setRating(Number(e.target.value)); // Kullanıcının girdiği puanı sayıya çeviriyoruz
  };

  return (
    <>
      <div className="content">
        <div className="pinterest-feed">
          {photos.map((photoId) => (
            <div key={photoId} className="pinterest-item">
              <img
                key={photoId}
                src={`/api/photos/${photoId}?api=${userInfo?.apiKey}&uname=${userInfo?.username}`}
                alt={`Photo ${photoId}`}
                onClick={() => handleImageClick(photoId)}
              />
            </div>
          ))}
        </div>
      </div>
      {/* Modal: Fotoğrafın büyük hali ve puanlama kısmı */}
      {selectedPhoto && (
        <Modal
          show={true}
          onHide={() => setSelectedPhoto(null)} // Modal'ı kapat
          backdrop="static"
          centered // Modal'ı kapat
        >
          <Modal.Header closeButton>
            <Modal.Title>Rate this photo</Modal.Title>
          </Modal.Header>
          <Modal.Body>
            <div className="modal-image-container">
              <img
                src={`/api/photos/${selectedPhoto.id}?api=${userInfo?.apiKey}&uname=${userInfo?.username}`}
                alt="Selected Photo"
                className="modal-image"
                style={{ width: "30%", height: "auto" }}
              />
            </div>
            <div className="rating-container">
              <h4>Rate this photo:</h4>
              <input
                type="number"
                min="1"
                max="5"
                value={rating}
                onChange={handleRatingChange}
                placeholder="Rate (1-5)"
              />
            </div>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setSelectedPhoto(null)}>
              Close
            </Button>
            <Button variant="primary" onClick={handleSubmitRating}>
              Submit Rating
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </>
  );
}

export default Home;
