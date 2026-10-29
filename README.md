# Smart Pantry Manager

## About the Application

Smart Pantry Manager is an Android application developed in Java to help users manage pantry ingredients and find recipes they can prepare using the ingredients they have available.

The application uses a simple pink-themed interface and stores pantry information locally so that ingredients remain saved when the application is closed and reopened.

## Features

- **Home Screen:** Provides navigation to the main application features.
- **Pantry Management:** Add, view, edit and delete pantry ingredients.
- **Ingredient Quantities:** Record ingredient quantities and measurement units.
- **Recipe Collection:** Includes 20 preloaded recipes with ingredients and preparation instructions.
- **Suggested Recipes:** Find recipes that can be prepared using available ingredients and sufficient quantities.
- **Recipe Details:** View the ingredients required and preparation method for each recipe.
- **Settings:** Clear pantry ingredients with a confirmation prompt.
- **Data Persistence:** Stores information locally using SQLite.
- **Custom RecyclerView Adapter:** Displays pantry ingredients in a structured list with edit and delete actions.

## Technologies Used

- Java
- Android Studio
- XML layouts
- SQLite database
- Android Intents
- RecyclerView and a custom RecyclerView Adapter
- Git and GitHub

## Database

The application uses SQLite for local data storage.

The database is named `SmartPantry.db` and contains the following tables:

- `ingredients` — stores ingredient names, quantities and units.
- `recipes` — stores recipe names and preparation instructions.
- `recipe_ingredients` — stores the ingredients and quantities required for each recipe.

SQLite allows the application to retain its data locally without requiring an external database server.

## Recipe Matching

The application compares the ingredients and quantities stored in the pantry with the requirements of each recipe.

A recipe is suggested only when all its required ingredients are available in sufficient quantities. The application also normalises common ingredient name and unit variations to improve matching.

## Requirements

- Android Studio
- Android SDK compatible with the project's configured SDK settings
- Java support for Android development

## How to Run the Application

1. Clone or download this repository from GitHub.
2. Open Android Studio.
3. Select **Open** and choose the `SmartPantryManagerJava` project folder.
4. Allow Gradle to sync and download the required dependencies.
5. Create or start an Android emulator, or connect a compatible Android device with USB debugging enabled.
6. Click **Run** in Android Studio to build and launch the application.

The SQLite database is created locally by the application when it is first launched.

## Project Structure

The project contains Java activity classes for the main screens, a database helper for SQLite operations, an ingredient model, a custom ingredient adapter, and XML layout files for the user interface.

## Version Control

This project is maintained using Git and hosted in a public GitHub repository.

## Author

Developed as part of the Mobile App Development 700 module.
