import React, { useState, useEffect } from 'react';
import { restaurantService } from '../services/api';

interface HomePageProps {
  user: any;
  onLogout: () => void;
}

interface Restaurant {
  id: number;
  name: string;
  description: string;
  cuisineType: string;
  avgRating: number;
  imageUrl: string;
}

const HomePage: React.FC<HomePageProps> = ({ user, onLogout }) => {
  const [restaurants, setRestaurants] = useState<Restaurant[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchRestaurants();
  }, []);

  const fetchRestaurants = async () => {
    try {
      setIsLoading(true);
      const response = await restaurantService.getAll();
      setRestaurants(response.data.data || []);
    } catch (err: any) {
      setError('Failed to load restaurants');
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      {/* Header */}
      <div style={styles.header}>
        <h1>🍔 FoodApp</h1>
        <div style={styles.userInfo}>
          <span>Welcome, {user?.name}!</span>
          <button onClick={onLogout} style={styles.logoutBtn}>
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div style={styles.content}>
        <h2>Order from Best Restaurants</h2>

        {error && <div style={styles.error}>{error}</div>}

        {isLoading ? (
          <p style={styles.loading}>Loading restaurants...</p>
        ) : restaurants.length === 0 ? (
          <p style={styles.noData}>No restaurants available</p>
        ) : (
          <div style={styles.grid}>
            {restaurants.map((restaurant) => (
              <div key={restaurant.id} style={styles.restaurantCard}>
                {restaurant.imageUrl && (
                  <img
                    src={restaurant.imageUrl}
                    alt={restaurant.name}
                    style={styles.restaurantImage}
                  />
                )}
                <div style={styles.cardContent}>
                  <h3>{restaurant.name}</h3>
                  <p>{restaurant.description}</p>
                  <p style={styles.cuisine}>
                    🍽️ {restaurant.cuisineType}
                  </p>
                  <div style={styles.footer}>
                    <span style={styles.rating}>⭐ {restaurant.avgRating}</span>
                    <button style={styles.orderBtn}>View Menu</button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

const styles = {
  container: {
    minHeight: '100vh',
    backgroundColor: '#f5f5f5',
  } as React.CSSProperties,
  header: {
    backgroundColor: '#ff6b6b',
    color: 'white',
    padding: '20px',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
  } as React.CSSProperties,
  userInfo: {
    display: 'flex',
    gap: '15px',
    alignItems: 'center',
  } as React.CSSProperties,
  logoutBtn: {
    backgroundColor: 'white',
    color: '#ff6b6b',
    border: 'none',
    padding: '8px 16px',
    borderRadius: '5px',
    cursor: 'pointer',
    fontWeight: 'bold',
  } as React.CSSProperties,
  content: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '40px 20px',
  } as React.CSSProperties,
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
    gap: '20px',
    marginTop: '20px',
  } as React.CSSProperties,
  restaurantCard: {
    backgroundColor: 'white',
    borderRadius: '10px',
    overflow: 'hidden',
    boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
    transition: 'transform 0.2s',
    cursor: 'pointer',
  } as React.CSSProperties,
  restaurantImage: {
    width: '100%',
    height: '200px',
    objectFit: 'cover',
    backgroundColor: '#ddd',
  } as React.CSSProperties,
  cardContent: {
    padding: '15px',
  } as React.CSSProperties,
  cuisine: {
    color: '#666',
    margin: '8px 0',
  } as React.CSSProperties,
  footer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '10px',
  } as React.CSSProperties,
  rating: {
    fontSize: '18px',
    fontWeight: 'bold',
  } as React.CSSProperties,
  orderBtn: {
    backgroundColor: '#ff6b6b',
    color: 'white',
    border: 'none',
    padding: '8px 16px',
    borderRadius: '5px',
    cursor: 'pointer',
  } as React.CSSProperties,
  error: {
    backgroundColor: '#ffebee',
    color: '#c62828',
    padding: '12px',
    borderRadius: '5px',
    marginBottom: '20px',
  } as React.CSSProperties,
  loading: {
    textAlign: 'center' as const,
    color: '#666',
    padding: '40px',
  } as React.CSSProperties,
  noData: {
    textAlign: 'center' as const,
    color: '#999',
    padding: '40px',
  } as React.CSSProperties,
};

export default HomePage;
