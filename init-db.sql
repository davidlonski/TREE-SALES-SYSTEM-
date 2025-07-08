-- Tree Sales Management System - Sample Data
-- This script initializes the database with sample data for testing

USE tree_sales_db;

-- Insert sample tree types
INSERT INTO TreeType (TypeDescription, Cost, BarcodePrefix) VALUES
('Noble Fir', 45.99, 'NF'),
('Douglas Fir', 35.99, 'DF'),
('Grand Fir', 42.99, 'GF'),
('Fraser Fir', 55.99, 'FF'),
('Balsam Fir', 38.99, 'BF'),
('Nordmann Fir', 48.99, 'NDF'),
('Blue Spruce', 52.99, 'BS'),
('Norway Spruce', 32.99, 'NS');

-- Insert sample scouts
INSERT INTO Scout (FirstName, MiddleName, LastName, DateOfBirth, PhoneNumber, Email, TroopID, Status, DateStatusUpdated) VALUES
('John', 'Michael', 'Smith', '2010-05-15', '555-0101', 'john.smith@email.com', 'TROOP001', 'Active', CURDATE()),
('Sarah', 'Anne', 'Johnson', '2011-03-22', '555-0102', 'sarah.johnson@email.com', 'TROOP001', 'Active', CURDATE()),
('Michael', NULL, 'Brown', '2009-08-10', '555-0103', 'michael.brown@email.com', 'TROOP002', 'Active', CURDATE()),
('Emily', 'Rose', 'Davis', '2012-01-30', '555-0104', 'emily.davis@email.com', 'TROOP001', 'Active', CURDATE()),
('David', 'James', 'Wilson', '2010-11-18', '555-0105', 'david.wilson@email.com', 'TROOP003', 'Active', CURDATE()),
('Ashley', 'Marie', 'Taylor', '2011-07-03', '555-0106', 'ashley.taylor@email.com', 'TROOP002', 'Inactive', CURDATE()),
('Christopher', 'Lee', 'Anderson', '2009-12-25', '555-0107', 'chris.anderson@email.com', 'TROOP003', 'Active', CURDATE()),
('Jessica', 'Lynn', 'Thomas', '2012-04-14', '555-0108', 'jessica.thomas@email.com', 'TROOP001', 'Active', CURDATE());

-- Insert sample trees
INSERT INTO Tree (Barcode, TreeType, Notes, Status, DateStatusUpdated) VALUES
('NF001', 'Noble Fir', 'Premium quality, 6-7 feet', 'Available', CURDATE()),
('NF002', 'Noble Fir', 'Premium quality, 7-8 feet', 'Available', CURDATE()),
('DF001', 'Douglas Fir', 'Classic choice, 6 feet', 'Available', CURDATE()),
('DF002', 'Douglas Fir', 'Classic choice, 7 feet', 'Sold', CURDATE()),
('GF001', 'Grand Fir', 'Beautiful shape, 6-7 feet', 'Available', CURDATE()),
('FF001', 'Fraser Fir', 'Excellent needle retention, 6 feet', 'Available', CURDATE()),
('FF002', 'Fraser Fir', 'Excellent needle retention, 7 feet', 'Available', CURDATE()),
('BF001', 'Balsam Fir', 'Traditional fragrance, 5-6 feet', 'Available', CURDATE()),
('BS001', 'Blue Spruce', 'Silvery-blue color, 6 feet', 'Available', CURDATE()),
('NS001', 'Norway Spruce', 'Traditional choice, 7-8 feet', 'Available', CURDATE()),
('NF003', 'Noble Fir', 'Small nick on trunk', 'Damaged', CURDATE()),
('DF003', 'Douglas Fir', 'Past season inventory', 'Inactive', CURDATE()),
('GF002', 'Grand Fir', 'Reserved for customer', 'Sold', CURDATE()),
('NDF001', 'Nordmann Fir', 'Non-drop variety, 6 feet', 'Available', CURDATE()),
('NDF002', 'Nordmann Fir', 'Non-drop variety, 7 feet', 'Available', CURDATE());

-- Display summary of inserted data
SELECT 'Tree Types' as Category, COUNT(*) as Count FROM TreeType
UNION ALL
SELECT 'Scouts' as Category, COUNT(*) as Count FROM Scout
UNION ALL
SELECT 'Trees' as Category, COUNT(*) as Count FROM Tree;

-- Display tree status summary
SELECT Status, COUNT(*) as Count FROM Tree GROUP BY Status ORDER BY Status;