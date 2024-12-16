import React, { useEffect, useState } from "react";
import "./TopList.css";

const TopPhotos = () => {
  const [TopPhotos, setTopPhotos] = useState([]);
  const [topUsers, setTopUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Backend credentials (example placeholders)
  const API_KEY = sessionStorage.getItem("apiKey");
  const USERNAME = sessionStorage.getItem("username");

  useEffect(() => {
    const fetchTopPhotos = async () => {
      try {
        // Fetch all photo IDs
        const allPhotosResponse = await fetch(
          `/api/photos/all?api=${API_KEY}&uname=${USERNAME}`
        );
        const allPhotosData = await allPhotosResponse.json();

        const photoIds = allPhotosData;

        // Fetch average score for each photo
        const scorePromises = photoIds.map(async (photoId) => {
          const response = await fetch(
            `/api/photos/${photoId}/averageScore?api=${API_KEY}&uname=${USERNAME}`
          );
          const data = await response.json();
          return { photoId, averageScore: data.averageScore };
        });

        const scores = await Promise.all(scorePromises);

        // Sort photos by average score in descending order and take the top 10
        const sortedScores = scores
          .sort((a, b) => b.averageScore - a.averageScore)
          .slice(0, 10);

        setTopPhotos(sortedScores);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching top users:", err);
        setError("Failed to fetch data. Please try again later.");
        setLoading(false);
      }
    };

    fetchTopPhotos();
  }, []);

  useEffect(() => {
    const fetchTopUsers = async () => {
      try {
        // Fetch all users
        const allUsersResponse = await fetch(
          `/api/users?api=${API_KEY}&uname=${USERNAME}`
        );

        if (!allUsersResponse.ok) {
          throw new Error("Failed to fetch users");
        }

        const allUsers = await allUsersResponse.json();

        // Fetch average scores for all users
        const scorePromises = allUsers.map(async (user) => {
          try {
            const response = await fetch(
              `/api/users/${user.id}/averageScore?api=${API_KEY}&uname=${USERNAME}`
            );

            if (!response.ok) {
              console.error(`Failed to fetch score for user ${user.id}`);
              return null; // Skip this user if there's an error
            }

            const data = await response.json();

            return {
              userId: user.id,
              username: user.username,
              averageScore: data.averageScore,
            };
          } catch (err) {
            console.error(`Error fetching score for user ${user.id}:`, err);
            return null;
          }
        });

        // Wait for all scores to resolve
        const userScores = await Promise.all(scorePromises);

        // Filter out null values and sort by averageScore descending
        const sortedUsers = userScores
          .filter((user) => user && user.averageScore !== undefined)
          .sort((a, b) => b.averageScore - a.averageScore)
          .slice(0, 10); // Take the top 10 users

        setTopUsers(sortedUsers);
        setLoading(false);
      } catch (err) {
        console.error("Error fetching users or scores:", err);
        setError("Failed to fetch data. Please try again later.");
        setLoading(false);
      }
    };

    fetchTopUsers();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="content">
      {/* Top Photos Section */}
      <div className="top-photos-container">
        <h1>Top 10 Fotoğraf</h1>
        <div className="photo-grid">
          {TopPhotos.map((photo, index) => (
            <div key={photo.photoId} className="photo-item">
              <span className="photo-rank">Rank {index + 1}</span>
              <img
                src={`/api/photos/${photo.photoId}?api=${API_KEY}&uname=${USERNAME}`}
                alt={`User Photo Rank ${index + 1}`}
                className="photo-img"
              />
              <span className="photo-score">
                Ortalama Puan: {photo.averageScore.toFixed(2)}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* Top Users Section */}
      <div className="top-users-container">
        <h1>Top 10 Kullanıcı</h1>
        <ul className="top-users-list">
          {topUsers.map((user, index) => (
            <li key={user.userId}>
              <span className="username">
                Rank {index + 1}: {user.username}
              </span>
              <span className="user-score">
                Puan: {user.averageScore.toFixed(2)}
              </span>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default TopPhotos;
