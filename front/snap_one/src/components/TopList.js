import React, { useEffect, useState } from 'react';

const TopPhotos = () => {
  const [TopPhotos, setTopPhotos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Backend credentials (example placeholders)
  const API_KEY = sessionStorage.getItem('apiKey');
  const USERNAME = sessionStorage.getItem('username');

  useEffect(() => {
    const fetchTopPhotos = async () => {
      try {
        // Fetch all photo IDs
        const allPhotosResponse = await fetch(`/api/photos/all?api=${API_KEY}&uname=${USERNAME}`);
        const allPhotosData = await allPhotosResponse.json();

        const photoIds = allPhotosData;

        // Fetch average score for each photo
        const scorePromises = photoIds.map(async (photoId) => {
          const response = await fetch(`/api/photos/${photoId}/averageScore?api=${API_KEY}&uname=${USERNAME}`);
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

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Top 10 Fotoğraf</h1>
      <ul>
        {TopPhotos.map((user, index) => (
          <li key={user.photoId}>
            <strong>Rank {index + 1}:</strong> <img
              key={user.photoId}
              src={`/api/photos/${user.photoId}?api=${API_KEY}&uname=${USERNAME}`}
              alt="User Photo"
              style={{ width: "150px", height: "150px", margin: "10px" }}
            /> 
            Ortalama Puan: {user.averageScore.toFixed(2)}
          </li>
        ))}
      </ul>
    </div>
  );
};

export default TopPhotos;
