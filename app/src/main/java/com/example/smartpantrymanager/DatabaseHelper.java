
package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE recipes (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "name TEXT NOT NULL, " +
                        "instructions TEXT NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE recipe_ingredients (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "recipe_id INTEGER NOT NULL, " +
                        "ingredient_name TEXT NOT NULL, " +
                        "required_quantity REAL NOT NULL, " +
                        "unit TEXT NOT NULL)"
        );

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            seedRecipes(db);
        }
    }

    // INGREDIENT FUNCTIONS

    public boolean addIngredient(String name, double quantity, String unit) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        return db.insert("ingredients", null, values) != -1;
    }

    public Cursor getAllIngredients() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM ingredients ORDER BY name ASC",
                null
        );
    }

    public boolean updateIngredient(
            int id,
            String name,
            double quantity,
            String unit
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        return db.update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    public boolean deleteIngredient(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.delete(
                "ingredients",
                "id = ?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }

    // RECIPE INSERTION FUNCTIONS

    public long addRecipe(String name, String instructions) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("instructions", instructions);

        return db.insert("recipes", null, values);
    }

    public long addRecipeIngredient(
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit
    ) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName);
        values.put("required_quantity", requiredQuantity);
        values.put("unit", unit);

        return db.insert("recipe_ingredients", null, values);
    }

    // CAN ALSO BE CALLED MANUALLY TO ADD ANY MISSING SAMPLE RECIPES

    public void insertSampleRecipes() {
        SQLiteDatabase db = this.getWritableDatabase();
        seedRecipes(db);
    }

    private void seedRecipes(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            // 1
            addSampleRecipe(db,
                    "Scrambled Eggs",
                    "1. Crack the eggs into a bowl. 2. Beat them. " +
                            "3. Melt the butter in a pan. 4. Cook the eggs while stirring.",
                    new String[]{"Eggs", "Butter"},
                    new double[]{2, 1},
                    new String[]{"pieces", "tablespoon"});

            // 2
            addSampleRecipe(db,
                    "Egg Fried Rice",
                    "1. Cook the rice. 2. Beat the eggs. " +
                            "3. Heat the oil. 4. Add the eggs and rice and fry together.",
                    new String[]{"Rice", "Eggs", "Oil"},
                    new double[]{1, 2, 1},
                    new String[]{"cup", "pieces", "tablespoon"});

            // 3
            addSampleRecipe(db,
                    "Pancakes",
                    "1. Mix the flour, milk and eggs. 2. Stir until smooth. " +
                            "3. Pour small amounts into a hot pan. 4. Cook both sides.",
                    new String[]{"Flour", "Milk", "Eggs"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "cup", "pieces"});

            // 4
            addSampleRecipe(db,
                    "Cheese Sandwich",
                    "1. Place cheese between two slices of bread. " +
                            "2. Serve as a sandwich.",
                    new String[]{"Bread", "Cheese"},
                    new double[]{2, 2},
                    new String[]{"slices", "slices"});

            // 5
            addSampleRecipe(db,
                    "Tomato Pasta",
                    "1. Boil the pasta until tender. 2. Chop the tomatoes. " +
                            "3. Heat the oil and cook the tomatoes. 4. Mix with the pasta.",
                    new String[]{"Pasta", "Tomato", "Oil"},
                    new double[]{1, 2, 1},
                    new String[]{"cup", "pieces", "tablespoon"});

            // 6
            addSampleRecipe(db,
                    "Banana Smoothie",
                    "1. Peel the banana. 2. Add the banana and milk to a blender. " +
                            "3. Blend until smooth.",
                    new String[]{"Banana", "Milk"},
                    new double[]{1, 1},
                    new String[]{"pieces", "cup"});

            // 7
            addSampleRecipe(db,
                    "Omelette",
                    "1. Beat the eggs. 2. Pour them into a heated pan. " +
                            "3. Add the cheese. 4. Cook until the eggs are set.",
                    new String[]{"Eggs", "Cheese"},
                    new double[]{2, 1},
                    new String[]{"pieces", "slices"});

            // 8
            addSampleRecipe(db,
                    "Garlic Rice",
                    "1. Cook the rice. 2. Heat the oil in a pan. " +
                            "3. Add the garlic. 4. Mix in the rice and fry briefly.",
                    new String[]{"Rice", "Garlic", "Oil"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "teaspoon", "tablespoon"});

            // 9
            addSampleRecipe(db,
                    "French Toast",
                    "1. Beat the egg with the milk. 2. Dip the bread into the mixture. " +
                            "3. Cook both sides in a heated pan until golden.",
                    new String[]{"Bread", "Eggs", "Milk"},
                    new double[]{2, 1, 2},
                    new String[]{"slices", "pieces", "tablespoon"});

            // 10
            addSampleRecipe(db,
                    "Tomato Salad",
                    "1. Wash and chop the tomatoes. 2. Add the oil. " +
                            "3. Mix gently and serve.",
                    new String[]{"Tomato", "Oil"},
                    new double[]{2, 1},
                    new String[]{"pieces", "tablespoon"});

            // 11
            addSampleRecipe(db,
                    "Butter Toast",
                    "1. Toast the bread. 2. Spread butter over the toast. " +
                            "3. Serve while warm.",
                    new String[]{"Bread", "Butter"},
                    new double[]{2, 1},
                    new String[]{"slices", "tablespoon"});

            // 12
            addSampleRecipe(db,
                    "Cheese Omelette",
                    "1. Beat the eggs. 2. Melt the butter in a pan. " +
                            "3. Add the eggs and cheese. 4. Cook until set.",
                    new String[]{"Eggs", "Cheese", "Butter"},
                    new double[]{2, 1, 1},
                    new String[]{"pieces", "slices", "teaspoon"});

            // 13
            addSampleRecipe(db,
                    "Banana Pancakes",
                    "1. Mash the banana. 2. Mix with flour and egg. " +
                            "3. Cook small pancakes in a heated pan on both sides.",
                    new String[]{"Banana", "Flour", "Eggs"},
                    new double[]{1, 1, 1},
                    new String[]{"pieces", "cup", "pieces"});

            // 14
            addSampleRecipe(db,
                    "Simple Porridge",
                    "1. Add the oats and milk to a pot. 2. Heat gently while stirring. " +
                            "3. Cook until the oats soften.",
                    new String[]{"Oats", "Milk"},
                    new double[]{1, 1},
                    new String[]{"cup", "cup"});

            // 15
            addSampleRecipe(db,
                    "Garlic Butter Pasta",
                    "1. Boil the pasta. 2. Melt the butter in a pan. " +
                            "3. Add the garlic. 4. Mix in the cooked pasta.",
                    new String[]{"Pasta", "Butter", "Garlic"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "tablespoon", "teaspoon"});

            // 16
            addSampleRecipe(db,
                    "Chicken Fried Rice",
                    "1. Cook the rice. 2. Cook the chicken thoroughly. " +
                            "3. Heat the oil in a pan. 4. Add the chicken and rice and fry together.",
                    new String[]{"Rice", "Chicken", "Oil"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "pieces", "tablespoon"});

            // 17
            addSampleRecipe(db,
                    "Peanut Butter Toast",
                    "1. Toast the bread. 2. Spread peanut butter over the slices. " +
                            "3. Serve.",
                    new String[]{"Bread", "Peanut Butter"},
                    new double[]{2, 2},
                    new String[]{"slices", "tablespoon"});

            // 18
            addSampleRecipe(db,
                    "Apple Oatmeal",
                    "1. Cook the oats in milk. 2. Chop the apple. " +
                            "3. Add the apple and serve.",
                    new String[]{"Oats", "Milk", "Apple"},
                    new double[]{1, 1, 1},
                    new String[]{"cup", "cup", "pieces"});

            // 19
            addSampleRecipe(db,
                    "Cheesy Tomato Pasta",
                    "1. Boil the pasta until tender. 2. Cook the chopped tomatoes. " +
                            "3. Mix the tomatoes with the pasta. 4. Add the cheese.",
                    new String[]{"Pasta", "Tomato", "Cheese"},
                    new double[]{1, 2, 2},
                    new String[]{"cup", "pieces", "slices"});

            // 20
            addSampleRecipe(db,
                    "Banana Oat Smoothie",
                    "1. Peel the banana. 2. Add the banana, oats and milk to a blender. " +
                            "3. Blend until smooth.",
                    new String[]{"Banana", "Oats", "Milk"},
                    new double[]{1, 0.5, 1},
                    new String[]{"pieces", "cup", "cup"});

            db.setTransactionSuccessful();

        } finally {
            db.endTransaction();
        }
    }

    private void addSampleRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units
    ) {
        // Avoid inserting a recipe that already exists.
        Cursor cursor = db.rawQuery(
                "SELECT id FROM recipes WHERE name = ?",
                new String[]{name}
        );

        boolean exists;

        try {
            exists = cursor.moveToFirst();
        } finally {
            cursor.close();
        }

        if (exists) {
            return;
        }

        ContentValues recipeValues = new ContentValues();
        recipeValues.put("name", name);
        recipeValues.put("instructions", instructions);

        long recipeId = db.insert("recipes", null, recipeValues);

        if (recipeId == -1) {
            return;
        }

        for (int i = 0; i < ingredientNames.length; i++) {
            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredientNames[i]);
            ingredientValues.put("required_quantity", quantities[i]);
            ingredientValues.put("unit", units[i]);

            db.insert("recipe_ingredients", null, ingredientValues);
        }
    }

    // FIND RECIPES THAT CAN BE MADE WITH THE PANTRY

    public Cursor getAvailableRecipes() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT r.id, r.name, r.instructions " +
                        "FROM recipes r " +
                        "WHERE EXISTS (" +
                        "SELECT 1 FROM recipe_ingredients ri " +
                        "WHERE ri.recipe_id = r.id) " +
                        "AND NOT EXISTS (" +
                        "SELECT 1 FROM recipe_ingredients ri " +
                        "WHERE ri.recipe_id = r.id " +
                        "AND NOT EXISTS (" +
                        "SELECT 1 FROM ingredients i " +
                        "WHERE LOWER(TRIM(i.name)) = LOWER(TRIM(ri.ingredient_name)) " +
                        "AND LOWER(TRIM(i.unit)) = LOWER(TRIM(ri.unit)) " +
                        "AND i.quantity >= ri.required_quantity" +
                        ")" +
                        ") " +
                        "ORDER BY r.name ASC";

        return db.rawQuery(query, null);
    }

    // GET INGREDIENTS REQUIRED FOR A RECIPE

    public Cursor getRecipeIngredients(int recipeId) {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }
}
