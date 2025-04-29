-- Flight
-- Seeding Flight_Class table
INSERT INTO Flight_Class (class_name, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                          ('Economy', NOW(), 'system', NOW(), 'system'),
                                                                                          ('Premium Economy', NOW(), 'system', NOW(), 'system'),
                                                                                          ('Business', NOW(), 'system', NOW(), 'system'),
                                                                                          ('First Class', NOW(), 'system', NOW(), 'system');

-- Seeding Airline table
INSERT INTO Airline (name, code, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                     ('Singapore Airlines', 'SQ', NOW(), 'system', NOW(), 'system'),
                                                                                     ('Emirates', 'EK', NOW(), 'system', NOW(), 'system'),
                                                                                     ('Qatar Airways', 'QR', NOW(), 'system', NOW(), 'system'),
                                                                                     ('Cathay Pacific', 'CX', NOW(), 'system', NOW(), 'system');

-- Seeding Flight table (assuming airlines exist)
INSERT INTO Flight (airline_id, flight_number, origin, destination, departure_time, arrival_time, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                                                                      (1, 'SQ001', 'SIN', 'LHR', '2025-04-01 08:00:00', '2025-04-01 20:00:00', NOW(), 'system', NOW(), 'system'),
                                                                                                                                                      (2, 'EK202', 'DXB', 'JFK', '2025-04-02 10:00:00', '2025-04-02 22:00:00', NOW(), 'system', NOW(), 'system'),
                                                                                                                                                      (3, 'QR303', 'DOH', 'CDG', '2025-04-03 12:00:00', '2025-04-03 18:00:00', NOW(), 'system', NOW(), 'system');

-- Seeding Flight_Addon table
INSERT INTO Flight_Addon (name, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                    ('Extra Baggage', NOW(), 'system', NOW(), 'system'),
                                                                                    ('Priority Boarding', NOW(), 'system', NOW(), 'system'),
                                                                                    ('Lounge Access', NOW(), 'system', NOW(), 'system');

-- Seeding Flight_Addon_Price table (assuming flights and addons exist)
INSERT INTO Flight_Addon_Price (flight_id, flight_addon_id, price, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                                       (1, 1, 50.00, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (1, 2, 20.00, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (2, 1, 60.00, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (2, 3, 35.00, NOW(), 'system', NOW(), 'system');
-- Seeding Flight_Price table
INSERT INTO Flight_Price (flight_id, flight_class_id, departure_date, base_price, discount, surge_multiplier, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                                                                                  (1, 1, '2025-04-05T08:30:00', 500.00, 5.00, 1.2, NOW(), 'system', NOW(), 'system'),
                                                                                                                                                                  (1, 2, '2025-04-05T08:30:00', 800.00, 10.00, 1.5, NOW(), 'system', NOW(), 'system'),
                                                                                                                                                                  (2, 3, '2025-04-06T12:00:00', 1200.00, 15.00, 1.8, NOW(), 'system', NOW(), 'system'),
                                                                                                                                                                  (2, 4, '2025-04-06T12:00:00', 2000.00, 20.00, 2.0, NOW(), 'system', NOW(), 'system');

-- Hotel
-- Seeding Hotel table
INSERT INTO Hotel (name, location, description, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                    ('Marina Bay Sands', 'Singapore', 'Luxury hotel with infinity pool', NOW(), 'system', NOW(), 'system'),
                                                                                                    ('The Ritz-Carlton', 'Hong Kong', '5-star hotel with breathtaking views', NOW(), 'system', NOW(), 'system'),
                                                                                                    ('Four Seasons', 'Paris', 'Elegant rooms with world-class service', NOW(), 'system', NOW(), 'system');

-- Seeding Hotel_Room_Type table
INSERT INTO Hotel_Room_Type (hotel_id, name, base_price, capacity, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                                       (1, 'Deluxe Room', 350.00, 2, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (1, 'Premier Suite', 700.00, 4, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (2, 'Sky View Suite', 1000.00, 3, NOW(), 'system', NOW(), 'system'),
                                                                                                                       (3, 'Luxury King Suite', 1500.00, 2, NOW(), 'system', NOW(), 'system');

-- Seeding Hotel_Room_Price table
INSERT INTO Hotel_Room_Price (hotel_room_type_id, start_date, end_date, price, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                                                   (1, '2025-06-01 14:00:00', '2025-06-05 12:00:00', 320.00, NOW(), 'system', NOW(), 'system'),
                                                                                                                                   (2, '2025-06-10 14:00:00', '2025-06-15 12:00:00', 680.00, NOW(), 'system', NOW(), 'system'),
                                                                                                                                   (3, '2025-07-01 14:00:00', '2025-07-07 12:00:00', 950.00, NOW(), 'system', NOW(), 'system');

-- Seeding Hotel_Addon table
INSERT INTO Hotel_Addon (hotel_id, name, price, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                                    (1, 'Breakfast Buffet', 50.00, NOW(), 'system', NOW(), 'system'),
                                                                                                    (1, 'Airport Transfer', 80.00, NOW(), 'system', NOW(), 'system'),
                                                                                                    (2, 'Spa Package', 120.00, NOW(), 'system', NOW(), 'system'),
                                                                                                    (3, 'Romantic Dinner', 200.00, NOW(), 'system', NOW(), 'system');

-- Seeding User_Status table
INSERT INTO user_status (status, created_dt, created_by, updated_dt, updated_by) VALUES
                                                                                     ('ACTIVE', NOW(), 'system', NOW(), 'system'),
                                                                                     ('INACTIVE', NOW(), 'system', NOW(), 'system'),
                                                                                     ('BANNED', NOW(), 'system', NOW(), 'system');