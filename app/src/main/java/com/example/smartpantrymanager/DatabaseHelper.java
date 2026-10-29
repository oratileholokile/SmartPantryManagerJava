
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "instructions TEXT NOT NULL)");

        db.execSQL("CREATE TABLE recipe_ingredients (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "recipe_id INTEGER NOT NULL, " +
                "ingredient_name TEXT NOT NULL, " +
                "required_quantity REAL NOT NULL, " +
                "unit TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Database upgrade handling can be added here when the schema changes.
    }

    // Add an ingredient to the pantry
    public boolean addIngredient(String name, double quantity, String unit) {
        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || quantity <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        return db.insert("ingredients", null, values) != -1;
    }

    // Get all pantry ingredients
    public Cursor getAllIngredients() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery(
                "SELECT * FROM ingredients ORDER BY name ASC", null);
    }

    // Update an existing ingredient
    public boolean updateIngredient(int id, String name,
                                    double quantity, String unit) {
        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || quantity <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        int result = db.update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // Delete an ingredient
    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = getWritableDatabase();

        int result = db.delete(
                "ingredients",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // Add a recipe
    public long addRecipe(String name, String instructions) {
        if (name == null || name.trim().isEmpty()
                || instructions == null || instructions.trim().isEmpty()) {
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("instructions", instructions.trim());

        return db.insert("recipes", null, values);
    }

    // Add an ingredient required by a recipe
    public boolean addRecipeIngredient(long recipeId, String name,
                                       double quantity, String unit) {
        if (name == null || name.trim().isEmpty()
                || unit == null || unit.trim().isEmpty()
                || quantity <= 0) {
            return false;
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", name.trim());
        values.put("required_quantity", quantity);
        values.put("unit", unit.trim());

        return db.insert("recipe_ingredients", null, values) != -1;
    }

    // Insert sample recipes once
    public void insertSampleRecipes() {
        SQLiteDatabase db = getWritableDatabase();

        Cursor existing = db.rawQuery("SELECT COUNT(*) FROM recipes", null);
        existing.moveToFirst();
        int count = existing.getInt(0);
        existing.close();

        if (count > 0) {
            return;
        }

        db.beginTransaction();

        try {
            addSampleRecipe(db, "Scrambled Eggs",
                    "1. Beat the eggs. 2. Heat a pan. 3. Cook while stirring.",
                    new String[]{"Eggs", "Butter"},
                    new double[]{2, 1},
                    new String[]{"pieces", "tablespoon"});

            addSampleRecipe(db, "Egg Fried Rice",
                    "1. Cook the rice. 2. Scramble the eggs. 3. Fry everything together.",
                    new String[]{"Rice", "Eggs", "Oil"},
                    new double[]{1, 2, 1},
                    new String[]{"cup", "pieces", "tablespoon"});

            addSampleRecipe(db, "Pancakes",
                    "1. Mix the ingredients. 2. Pour into a pan. 3. Cook both sides.",
                    new String[]{"Flour", "Milk", "Eggs"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "cup", "pieces"});

            addSampleRecipe(db, "Cheese Sandwich",
                    "1. Place cheese between bread slices. 2. Toast if desired.",
                    new String[]{"Bread", "Cheese"},
                    new double[]{2, 2},
                    new String[]{"slices", "slices"});

            addSampleRecipe(db, "Tomato Pasta",
                    "1. Boil pasta. 2. Cook tomatoes with oil. 3. Mix and serve.",
                    new String[]{"Pasta", "Tomato", "Oil"},
                    new double[]{1, 2, 1},
                    new String[]{"cup", "pieces", "tablespoon"});

            addSampleRecipe(db, "Banana Smoothie",
                    "1. Peel the banana. 2. Blend with milk until smooth.",
                    new String[]{"Banana", "Milk"},
                    new double[]{1, 1},
                    new String[]{"pieces", "cup"});

            addSampleRecipe(db, "Omelette",
                    "1. Beat the eggs. 2. Cook in a pan. 3. Fold and serve.",
                    new String[]{"Eggs", "Cheese"},
                    new double[]{2, 1},
                    new String[]{"pieces", "slices"});

            addSampleRecipe(db, "Garlic Rice",
                    "1. Cook rice. 2. Fry garlic in oil. 3. Mix together.",
                    new String[]{"Rice", "Garlic", "Oil"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "teaspoon", "tablespoon"});

            addSampleRecipe(db, "French Toast",
                    "1. Beat eggs. 2. Dip bread in egg. 3. Fry until golden.",
                    new String[]{"Bread", "Eggs", "Milk"},
                    new double[]{2, 1, 2},
                    new String[]{"slices", "pieces", "tablespoon"});

            addSampleRecipe(db, "Tomato Salad",
                    "1. Wash and chop the tomatoes. 2. Add oil and serve.",
                    new String[]{"Tomato", "Oil"},
                    new double[]{2, 1},
                    new String[]{"pieces", "tablespoon"});

            addSampleRecipe(db, "Butter Toast",
                    "1. Toast the bread. 2. Spread butter on top.",
                    new String[]{"Bread", "Butter"},
                    new double[]{2, 1},
                    new String[]{"slices", "tablespoon"});

            addSampleRecipe(db, "Cheese Omelette",
                    "1. Beat eggs. 2. Cook in a pan. 3. Add cheese and fold.",
                    new String[]{"Eggs", "Cheese", "Butter"},
                    new double[]{2, 1, 1},
                    new String[]{"pieces", "slices", "teaspoon"});

            addSampleRecipe(db, "Banana Pancakes",
                    "1. Mash the banana. 2. Mix with flour and eggs. 3. Cook in a pan.",
                    new String[]{"Banana", "Flour", "Eggs"},
                    new double[]{1, 1, 1},
                    new String[]{"pieces", "cup", "pieces"});

            addSampleRecipe(db, "Simple Porridge",
                    "1. Heat milk. 2. Stir in oats. 3. Cook until thick.",
                    new String[]{"Oats", "Milk"},
                    new double[]{1, 1},
                    new String[]{"cup", "cup"});

            addSampleRecipe(db, "Garlic Butter Pasta",
                    "1. Boil pasta. 2. Melt butter with garlic. 3. Mix together.",
                    new String[]{"Pasta", "Butter", "Garlic"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "tablespoon", "teaspoon"});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private void addSampleRecipe(SQLiteDatabase db, String name,
                                 String instructions, String[] ingredients,
                                 double[] quantities, String[] units) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert("recipes", null, recipeValues);

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredients.length; i++) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredients[i]);
            ingredientValues.put("required_quantity", quantities[i]);
            ingredientValues.put("unit", units[i]);

            db.insert("recipe_ingredients", null, ingredientValues);
        }
    }

    // Return only recipes whose ingredients and quantities are available
    public Cursor getAvailableRecipes() {
        SQLiteDatabase db = getReadableDatabase();

        String query =
                "SELECT r.id, r.name, r.instructions " +
                        "FROM recipes r " +
                        "WHERE EXISTS (" +
                        " SELECT 1 FROM recipe_ingredients ri " +
                        " WHERE ri.recipe_id = r.id" +
                        ") " +
                        "AND NOT EXISTS (" +
                        " SELECT 1 FROM recipe_ingredients ri " +
                        " WHERE ri.recipe_id = r.id " +
                        " AND NOT EXISTS (" +
                        "  SELECT 1 FROM ingredients i " +
                        "  WHERE LOWER(TRIM(i.name)) = LOWER(TRIM(ri.ingredient_name)) " +
                        "  AND LOWER(TRIM(i.unit)) = LOWER(TRIM(ri.unit)) " +
                        "  AND i.quantity >= ri.required_quantity" +
                        " )" +
                        ") " +
                        "ORDER BY r.name ASC";

        return db.rawQuery(query, null);
    }

    // Get ingredients required for one recipe
    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ? " +
                        "ORDER BY ingredient_name ASC",
                new String[]{String.valueOf(recipeId)}
        );
    }
}
