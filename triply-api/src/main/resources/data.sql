-- Insert data into Flight_Class table
INSERT INTO Flight_Class (class_name, created_at, updated_at) VALUES
                                                                  ('Economy', NOW(), NOW()),
                                                                  ('Premium Economy', NOW(), NOW()),
                                                                  ('Business', NOW(), NOW()),
                                                                  ('First Class', NOW(), NOW());

-- Insert data into Airline table
INSERT INTO Airline (name, code, created_at, updated_at) VALUES
                                                             ('Singapore Airlines', 'SQ', NOW(), NOW()),
                                                             ('Emirates', 'EK', NOW(), NOW()),
                                                             ('Qatar Airways', 'QR', NOW(), NOW()),
                                                             ('Cathay Pacific', 'CX', NOW(), NOW());