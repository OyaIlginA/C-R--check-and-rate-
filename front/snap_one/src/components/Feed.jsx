import React from 'react';
import photo from './images/cavi.jpeg';
import photo2 from './images/star.jpg';
import photo3 from './images/cavi.jpeg';
import photo4 from './images/star.jpg';
import './Feed.css';

const images = [
  { id: 1, src: photo, alt: 'Image 1' },
  { id: 2, src: photo2, alt: 'Image 2' },
  { id: 3, src: photo3, alt: 'Image 3' },
  { id: 4, src: photo4, alt: 'Image 4' },
  { id: 5, src: photo4, alt: 'Image 5' },
  { id: 6, src: photo3, alt: 'Image 6' },
  { id: 7, src: photo2, alt: 'Image 6' },
  { id: 8, src: photo, alt: 'Image 6' },
  { id: 9, src: photo, alt: 'Image 6' },
  // Add more image objects here
];

const Feed = () => {
  return (
    <div className="pinterest-feed">
      {images.map((image) => (
        <div key={image.id} className="pinterest-item">
          <img className="pic" alt={image.alt} src={image.src} />
        </div>
      ))}
    </div>
  );
};

export default Feed;
