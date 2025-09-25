-- Initialize database with some default data
USE adventurepark;

-- Insert test activities if they don't exist
INSERT IGNORE INTO activities (id, name, description, min_age, max_participants, duration_minutes, price, equipment_required) VALUES
(1, 'Go-kart', 'Spændende go-kart bane med hurtige biler', 12, 8, 15, 150.0, true),
(2, 'Minigolf', '18 hullers minigolf bane for hele familien', 5, 10, 60, 75.0, false),
(3, 'Paintball', 'Taktisk paintball kamp i professionelt setup', 16, 12, 90, 200.0, true),
(4, 'Sumo Wrestling', 'Sjov sumo brydning med oppustelige dragter', 8, 4, 20, 100.0, true);