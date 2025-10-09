-- Default data for aktiviteter
INSERT INTO activity (id, name, description, price, duration, min_age, min_height, available_from, available_to, image_url)
VALUES
(1, 'Go-kart', 'Oplev spænding ...', 150.00, 15, 12, 150, '2024-10-01T10:00:00', '2024-12-31T20:00:00', 'https://images.unsplash.com/photo-1652451991281-e637ec408bec'),
(2, 'Minigolf', 'Hyggelig 18-hullers ...', 75.00, 60, 5, 0, '2024-10-01T10:00:00', '2024-12-31T20:00:00', 'https://images.unsplash.com/photo-1730198439547-413dc40624bf');

-- Default data for bookinger
INSERT INTO booking (id, activity_id, customer_id, start_datetime, participants, booking_status, instructor_name)
VALUES
(1, 1, 1, '2024-10-10T14:00:00', 2, 'CONFIRMED', 'Anders Hansen'),
(2, 2, 2, '2024-10-11T11:00:00', 4, 'PENDING', 'Betina Olsen');
