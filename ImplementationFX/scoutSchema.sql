-- Tree Type Table
CREATE TABLE TreeType (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    TypeDescription VARCHAR(25) NOT NULL,
    Cost VARCHAR(20) NOT NULL,
    BarcodePrefix VARCHAR(2) NOT NULL
);

-- Tree Table
CREATE TABLE Tree (
    Barcode VARCHAR(20) PRIMARY KEY,
    TreeType INT NOT NULL,
    Notes VARCHAR(200),
    Status VARCHAR(10) NOT NULL CHECK (Status IN ('Available', 'Sold', 'Damaged')),
    DateStatusUpdated VARCHAR(12) NOT NULL,
    FOREIGN KEY (TreeType) REFERENCES TreeType(ID)
);

-- Scout Table
CREATE TABLE Scout (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    LastName VARCHAR(25) NOT NULL,
    FirstName VARCHAR(25) NOT NULL,
    MiddleName VARCHAR(25),
    DateOfBirth VARCHAR(12) NOT NULL,
    PhoneNumber VARCHAR(14) NOT NULL,
    Email VARCHAR(30) NOT NULL,
    TroopID VARCHAR(10) NOT NULL,
    Status VARCHAR(10) NOT NULL CHECK (Status IN ('Active', 'Inactive')),
    DateStatusUpdated VARCHAR(12) NOT NULL
);
