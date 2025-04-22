# TREE-SALES-SYSTEM

## Overview
A Tree Sales Management System developed as part of CSC 427/429: Software Systems Engineering/Object-Oriented Software Development (Term Project).

## Prerequisites
- IntelliJ IDEA
- JavaFX SDK ([Download here](https://gluonhq.com/products/javafx/))
- MariaDB Java Client (version 3.0.3)

## Setup Instructions

### 1. Project Setup
1. Clone this repository
2. Open the project in IntelliJ IDEA

### 2. Dependencies Configuration
1. Configure MariaDB Driver:
   - Go to Project Structure → Modules
   - Add `mariadb-java-client-3.0.3.jar` as a module dependency

2. Configure JavaFX:
   - Go to Project Structure → Libraries
   - Add the JavaFX SDK lib folder as a library
   - Return to Modules and add the JavaFX library as a module dependency
   - Apply changes

### 3. Runtime Configuration
1. Go to "Edit Configurations"
2. Create a new Application configuration
3. Set the run class to `TLC`
4. Add VM Options (Modify Options → Add VM Options):
   ```
   --module-path "path/to/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.web,javafx.media,javafx.graphics,javafx.base
   ```
   Note: Replace "path/to/javafx/lib" with your actual JavaFX lib directory path

## Running the Application
After completing the setup, you can run the application using the green "Run" button in IntelliJ IDEA.

## Technologies Used
- Java
- JavaFX
- MariaDB



