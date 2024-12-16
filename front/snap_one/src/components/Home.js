import React, { useEffect, useState } from "react";
import "./Home.css";
import { useNavigate } from "react-router-dom";
import Button from "react-bootstrap/Button";

function Home() {
  const [userInfo, setUserInfo] = useState(null);
  const [photos, setPhotos] = useState([]);
  const [selectedPhoto, setSelectedPhoto] = useState(null);
  const [rating, setRating] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    const apiKey = sessionStorage.getItem("apiKey");
    const username = sessionStorage.getItem("username");
    const userId = sessionStorage.getItem("userId");

    if (!apiKey || !username || !userId) {
      navigate("/login");
      return;
    }

    setUserInfo({
      userId,
      username,
      apiKey,
    });

    const fetchPhotos = async () => {
      try {
        const response = await fetch(
          `/api/photos/all?api=${apiKey}&uname=${username}`
        );
        const data = await response.json();
        if (response.ok) {
          setPhotos(data);
        } else {
          console.error("Error fetching photos:", data.error);
        }
      } catch (error) {
        console.error("Error:", error);
      }
    };

    fetchPhotos();
  }, [navigate]);

  const handleImageClick = (photo) => {
    setSelectedPhoto({ id: photo });
  };

  const handleStarClick = (index) => {
    setRating(index + 1);
  };

  const handleSubmitRating = async () => {
    if (selectedPhoto && rating) {
      const { userId, apiKey, username } = userInfo || {};

      try {
        const response = await fetch(
          `/api/photos/${selectedPhoto.id}/rate?uid=${userId}&score=${rating}&api=${apiKey}&uname=${username}`,
          {
            method: "POST",
          }
        );

        if (response.ok) {
          alert("Rating submitted successfully!");
          setRating(0);
          setSelectedPhoto(null);
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

  return (
    <>
      <div className="content">
        <div className="pinterest-feed">
          {photos.map((photoId) => (
            <div key={photoId} className="pinterest-item">
              <img
                src={`/api/photos/${photoId}?api=${userInfo?.apiKey}&uname=${userInfo?.username}`}
                alt={`Photo ${photoId}`}
                onClick={() => handleImageClick(photoId)}
              />
            </div>
          ))}
        </div>
      </div>

      {selectedPhoto && (
        <div className="modal-backdrop">
          <div className="modal-content">
            <div className="modal-header">
              <button
                className="close-button"
                onClick={() => setSelectedPhoto(null)}
              >
                ✕
              </button>
            </div>
            <div className="modal-body">
              <div className="modal-image-container">
                <img
                  src={`/api/photos/${selectedPhoto.id}?api=${userInfo?.apiKey}&uname=${userInfo?.username}`}
                  alt="Selected Photo"
                  className="modal-image"
                />
              </div>
              <div className="rating-container">
                <h4>Rate this photo:</h4>
                <div className="star-rating">
                  {[...Array(5)].map((_, index) => (
                    <span
                      key={index}
                      className={`star ${index < rating ? "selected" : ""}`}
                      onClick={() => handleStarClick(index)}
                    >
                      ★
                    </span>
                  ))}
                </div>
              </div>
            </div>
            <div className="modal-footer">
              <button
                className="submit-button"
                onClick={() => handleSubmitRating()}
              >
                Submit Rating
              </button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}

export default Home;
