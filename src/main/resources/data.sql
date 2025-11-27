-- Countries
INSERT INTO country (id, name) VALUES
(1, 'France'),
(2, 'Germany'),
(3, 'Italy'),
(4, 'Spain'),
(5, 'USA'),
(6, 'UK'),
(7, 'Japan'),
(8, 'Australia'),
(9, 'Brazil'),
(10, 'Canada')
ON CONFLICT (id) DO NOTHING;

-- Cities
INSERT INTO city (id, name, country_id) VALUES
(1, 'Paris', 1),
(2, 'Lyon', 1),
(3, 'Berlin', 2),
(4, 'Munich', 2),
(5, 'Rome', 3),
(6, 'Milan', 3),
(7, 'Madrid', 4),
(8, 'Barcelona', 4),
(9, 'New York', 5),
(10, 'Los Angeles', 5),
(11, 'London', 6),
(12, 'Manchester', 6),
(13, 'Tokyo', 7),
(14, 'Kyoto', 7),
(15, 'Sydney', 8),
(16, 'Melbourne', 8),
(17, 'Rio de Janeiro', 9),
(18, 'Sao Paulo', 9),
(19, 'Toronto', 10),
(20, 'Vancouver', 10)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tour (id, name, price, city_id, available_seats, description, start_date, end_date) VALUES
(1, 'Romantic Paris', 1200, 1, 20, 'A 5-day romantic trip to Paris', '2025-12-01', '2025-12-05'),
(2, 'Berlin History Tour', 900, 3, 25, 'Explore Berlin''s history and landmarks', '2025-12-10', '2025-12-15'),
(3, 'Italian Culinary Tour', 1500, 5, 15, 'Taste the best Italian food in Rome and Milan', '2025-12-20', '2025-12-25'),
(4, 'Spanish Beaches', 1100, 8, 30, 'Relax on the beaches of Barcelona and Madrid', '2026-01-05', '2026-01-12'),
(5, 'New York City Highlights', 1300, 9, 20, 'See the top attractions of NYC', '2026-01-15', '2026-01-20'),
(6, 'London Weekend', 1000, 11, 25, 'A weekend getaway to London', '2026-01-22', '2026-01-25'),
(7, 'Tokyo Adventure', 1600, 13, 18, 'Experience the culture and sights of Tokyo', '2026-02-01', '2026-02-07'),
(8, 'Sydney & Melbourne Exploration', 1800, 15, 22, 'Discover Australia''s best cities', '2026-02-10', '2026-02-20'),
(9, 'Brazil Carnival', 1400, 17, 28, 'Join the famous Rio Carnival', '2026-02-25', '2026-03-03'),
(16, 'Lyon Food & Wine Tour', 1200, 2, 20, 'Taste the best wines and cuisines in Lyon', '2026-03-05', '2026-03-10'),
(17, 'Munich Beer Festival', 1000, 4, 25, 'Enjoy the traditional beer festival in Munich', '2026-03-15', '2026-03-20'),
(18, 'Rome Historical Sites', 1300, 5, 18, 'Visit the Colosseum, Vatican, and ancient Rome', '2026-03-22', '2026-03-27'),
(19, 'Madrid Art Tour', 1100, 7, 22, 'Explore Madrid''s museums and art galleries', '2026-04-01', '2026-04-06'),
(20, 'Barcelona Architecture Walk', 1150, 8, 20, 'Discover Gaudi''s masterpieces in Barcelona', '2026-04-08', '2026-04-13')
ON CONFLICT (id) DO NOTHING;


