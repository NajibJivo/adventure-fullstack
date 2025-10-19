-- ============================================
-- SEED DATA FOR ADVENTUREXP
-- ============================================
-- Kunder med KORREKTE BCrypt-hashede passwords
-- admin123 hashet med BCrypt
-- kunde123 hashet med BCrypt

INSERT INTO customer (id, name, phone, email, username, password, user_role) VALUES
                                                                                 (1, 'Admin', '12345678', 'admin@adventurexp.dk', 'admin',
                                                                                  '$2a$10$8ldkKMyvY3/ZeHdb/sLUbOwrykYlsTTJ3u0nCqccxxGbvxZCZGsqm', 'OWNER'),

                                                                                 (2, 'Test Kunde', '87654321', 'kunde@test.dk', 'kunde',
                                                                                  '$2a$10$8ldkKMyvY3/ZeHdb/sLUbOwrykYlsTTJ3u0nCqccxxGbvxZCZGsqm', 'CUSTOMER'),

                                                                                 (3, 'Lars Nielsen', '23456789', 'lars@example.dk', 'lars',
                                                                                  '$2a$10$8ldkKMyvY3/ZeHdb/sLUbOwrykYlsTTJ3u0nCqccxxGbvxZCZGsqm', 'CUSTOMER');
-- Aktiviteter
INSERT INTO activity (id, name, description, price, duration, min_age, min_height, available_from, available_to, image_url) VALUES
                                                                                                                                (1, 'Go-kart', 'Oplev spænding og adrenalin på vores professionelle go-kart bane. Perfekt til både begyndere og erfarne kørere.',
                                                                                                                                 150.00, 15, 12, 150, '2024-10-01 10:00:00', '2024-12-31 20:00:00',
                                                                                                                                 'https://images.unsplash.com/photo-1652451991281-e637ec408bec'),

                                                                                                                                (2, 'Minigolf', 'Hyggelig 18-hullers minigolf bane med sjove forhindringer for hele familien. Ingen alderskrav.',
                                                                                                                                 75.00, 60, 5, 0, '2024-10-01 10:00:00', '2024-12-31 20:00:00',
                                                                                                                                 'https://images.unsplash.com/photo-1730198439547-413dc40624bf'),

                                                                                                                                (3, 'Paintball', 'Taktisk paintball i vores udendørs arenaer. Udstyret inkluderet - kom klar til kamp!',
                                                                                                                                 200.00, 90, 15, 160, '2024-10-01 10:00:00', '2024-12-31 20:00:00',
                                                                                                                                 'https://paintballsports.co.uk/wp-content/uploads/2022/04/FF0924_2015_2_Bundesliga_Spieltag1-2-19-400x400.jpg'),

                                                                                                                                (4, 'Sumo Wrestling', 'Sjovt og sikkert sumo wrestling for alle aldre. Kostumer og sikkerhedsudstyr leveres.',
                                                                                                                                 100.00, 30, 8, 0, '2024-10-01 10:00:00', '2024-12-31 20:00:00',
                                                                                                                                 'https://thumbs.dreamstime.com/b/sumo-wrestlers-4836066.jpg');

-- Produkter til butikken
INSERT INTO product (id, name, price, is_active) VALUES
                                                     (1, 'AdventureXP T-shirt (M)', 149.00, TRUE),
                                                     (2, 'AdventureXP T-shirt (L)', 149.00, TRUE),
                                                     (3, 'Sportsdrik 0.5L', 25.00, TRUE),
                                                     (4, 'Energibar', 15.00, TRUE),
                                                     (5, 'Cola 0.5L', 20.00, TRUE),
                                                     (6, 'Vand 0.5L', 15.00, TRUE);

-- Bookinger
INSERT INTO booking (id, activity_id, customer_id, start_datetime, participants, booking_status, instructor_name) VALUES
                                                                                                                      (1, 1, 2, '2025-10-15 14:00:00', 2, 'CONFIRMED', 'Anders Hansen'),
                                                                                                                      (2, 2, 3, '2025-10-16 11:00:00', 4, 'PENDING', 'Betina Olsen'),
                                                                                                                      (3, 3, 2, '2025-10-17 15:30:00', 6, 'CONFIRMED', 'Carsten Mortensen');

-- Udstyr
INSERT INTO equipment (id, equipment_name, maintenance_date) VALUES
                                                                 (1, 'Go-kart #1', '2025-09-01'),
                                                                 (2, 'Go-kart #2', '2025-09-01'),
                                                                 (3, 'Paintball marker (10 stk)', '2025-08-15'),
                                                                 (4, 'Paintball beskyttelse (10 sæt)', '2025-08-15'),
                                                                 (5, 'Sumo kostume (voksen)', '2025-07-20'),
                                                                 (6, 'Minigolf køller (20 stk)', NULL);

-- Kobling mellem aktiviteter og udstyr
INSERT INTO activity_equipment (activity_id, equipment_id) VALUES
                                                               (1, 1),
                                                               (1, 2),
                                                               (3, 3),
                                                               (3, 4),
                                                               (4, 5),
                                                               (2, 6);
