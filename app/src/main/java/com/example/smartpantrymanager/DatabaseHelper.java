package com.example.smartpantrymanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "SmartPantry.db";
    private static final int DATABASE_VERSION = 3;

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
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion) {

        if (oldVersion < 3) {
            seedRecipes(db);
        }
    }

    // --------------------------------------------------
    // INGREDIENT CRUD
    // --------------------------------------------------

    public boolean addIngredient(
            String name,
            double quantity,
            String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        return db.insert("ingredients", null, values) != -1;
    }

    public Cursor getAllIngredients() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, name, quantity, unit " +
                        "FROM ingredients ORDER BY name ASC",
                null
        );
    }

    public boolean updateIngredient(
            int id,
            String name,
            double quantity,
            String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("quantity", quantity);
        values.put("unit", unit.trim());

        int rowsUpdated = db.update(
                "ingredients",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        return rowsUpdated > 0;
    }

    public int deleteIngredient(int id) {

        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                "ingredients",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // --------------------------------------------------
    // RECIPE CRUD
    // --------------------------------------------------

    public boolean addRecipe(
            String name,
            String instructions) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", name.trim());
        values.put("instructions", instructions.trim());

        return db.insert("recipes", null, values) != -1;
    }

    public boolean addRecipeIngredient(
            int recipeId,
            String ingredientName,
            double requiredQuantity,
            String unit) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("recipe_id", recipeId);
        values.put("ingredient_name", ingredientName.trim());
        values.put("required_quantity", requiredQuantity);
        values.put("unit", unit.trim());

        return db.insert(
                "recipe_ingredients",
                null,
                values
        ) != -1;
    }

    public void insertSampleRecipes() {

        SQLiteDatabase db = getWritableDatabase();
        seedRecipes(db);
    }

    public Cursor getAllRecipes() {

        SQLiteDatabase db = getWritableDatabase();
        seedRecipes(db);

        return db.rawQuery(
                "SELECT id, name, instructions " +
                        "FROM recipes ORDER BY name ASC",
                null
        );
    }

    public Cursor getRecipeIngredients(int recipeId) {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT ingredient_name, required_quantity, unit " +
                        "FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );
    }

    // --------------------------------------------------
    // AVAILABLE RECIPE MATCHING
    // --------------------------------------------------

    public Cursor getAvailableRecipes() {

        SQLiteDatabase db = getWritableDatabase();
        seedRecipes(db);

        MatrixCursor availableRecipes = new MatrixCursor(
                new String[]{"id", "name", "instructions"}
        );

        // Add pantry quantities together for matching ingredients.
        Map<String, Double> pantryQuantities = new HashMap<>();

        Cursor pantryCursor = db.rawQuery(
                "SELECT name, quantity, unit FROM ingredients",
                null
        );

        try {
            while (pantryCursor.moveToNext()) {

                String name = normalizeIngredientName(
                        pantryCursor.getString(
                                pantryCursor.getColumnIndexOrThrow("name")
                        )
                );

                String unit = normalizeUnit(
                        pantryCursor.getString(
                                pantryCursor.getColumnIndexOrThrow("unit")
                        )
                );

                double quantity = pantryCursor.getDouble(
                        pantryCursor.getColumnIndexOrThrow("quantity")
                );

                String key = name + "|" + unit;

                double currentQuantity = pantryQuantities.containsKey(key)
                        ? pantryQuantities.get(key)
                        : 0.0;

                pantryQuantities.put(key, currentQuantity + quantity);
            }
        } finally {
            pantryCursor.close();
        }

        Cursor recipesCursor = db.rawQuery(
                "SELECT id, name, instructions FROM recipes ORDER BY name ASC",
                null
        );

        try {
            while (recipesCursor.moveToNext()) {

                int recipeId = recipesCursor.getInt(
                        recipesCursor.getColumnIndexOrThrow("id")
                );

                String recipeName = recipesCursor.getString(
                        recipesCursor.getColumnIndexOrThrow("name")
                );

                String instructions = recipesCursor.getString(
                        recipesCursor.getColumnIndexOrThrow("instructions")
                );

                boolean canMakeRecipe = true;
                boolean hasRequirements = false;

                Cursor requirementsCursor = db.rawQuery(
                        "SELECT ingredient_name, required_quantity, unit " +
                                "FROM recipe_ingredients WHERE recipe_id = ?",
                        new String[]{String.valueOf(recipeId)}
                );

                try {
                    while (requirementsCursor.moveToNext()) {

                        hasRequirements = true;

                        String ingredientName = normalizeIngredientName(
                                requirementsCursor.getString(
                                        requirementsCursor.getColumnIndexOrThrow(
                                                "ingredient_name"
                                        )
                                )
                        );

                        String unit = normalizeUnit(
                                requirementsCursor.getString(
                                        requirementsCursor.getColumnIndexOrThrow(
                                                "unit"
                                        )
                                )
                        );

                        double requiredQuantity =
                                requirementsCursor.getDouble(
                                        requirementsCursor.getColumnIndexOrThrow(
                                                "required_quantity"
                                        )
                                );

                        String key = ingredientName + "|" + unit;

                        double availableQuantity =
                                pantryQuantities.containsKey(key)
                                        ? pantryQuantities.get(key)
                                        : 0.0;

                        if (availableQuantity < requiredQuantity) {
                            canMakeRecipe = false;
                            break;
                        }
                    }
                } finally {
                    requirementsCursor.close();
                }

                // Only show recipes with requirements that are all available.
                if (hasRequirements && canMakeRecipe) {
                    availableRecipes.addRow(new Object[]{
                            recipeId,
                            recipeName,
                            instructions
                    });
                }
            }
        } finally {
            recipesCursor.close();
        }

        return availableRecipes;
    }

    // --------------------------------------------------
    // INGREDIENT NAME NORMALISATION
    // --------------------------------------------------

    private String normalizeIngredientName(String name) {

        if (name == null) {
            return "";
        }

        String normalized = name
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");

        if (normalized.endsWith("ies") && normalized.length() > 3) {
            normalized = normalized.substring(
                    0, normalized.length() - 3
            ) + "y";

        } else if (normalized.endsWith("oes")
                && normalized.length() > 3) {

            normalized = normalized.substring(
                    0, normalized.length() - 2
            );

        } else if ((normalized.endsWith("ches")
                || normalized.endsWith("shes")
                || normalized.endsWith("xes")
                || normalized.endsWith("zes")
                || normalized.endsWith("sses"))
                && normalized.length() > 4) {

            normalized = normalized.substring(
                    0, normalized.length() - 2
            );

        } else if (normalized.endsWith("s")
                && !normalized.endsWith("ss")
                && normalized.length() > 2) {

            normalized = normalized.substring(
                    0, normalized.length() - 1
            );
        }

        return normalized;
    }

    // --------------------------------------------------
    // UNIT NORMALISATION
    // --------------------------------------------------

    private String normalizeUnit(String unit) {

        if (unit == null) {
            return "";
        }

        String normalized = unit
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", "");

        switch (normalized) {
            case "pieces":
            case "piece":
            case "pcs":
            case "pc":
                return "piece";

            case "slices":
            case "slice":
                return "slice";

            case "tablespoons":
            case "tablespoon":
            case "tbsp":
            case "tbsps":
                return "tablespoon";

            case "teaspoons":
            case "teaspoon":
            case "tsp":
            case "tsps":
                return "teaspoon";

            case "cups":
            case "cup":
                return "cup";

            case "grams":
            case "gram":
            case "g":
                return "g";

            case "kilograms":
            case "kilogram":
            case "kg":
            case "kgs":
                return "kg";

            case "millilitres":
            case "milliliters":
            case "millilitre":
            case "milliliter":
            case "ml":
                return "ml";

            case "litres":
            case "liters":
            case "litre":
            case "liter":
            case "l":
                return "l";

            default:
                return normalized;
        }
    }

    // --------------------------------------------------
    // SAMPLE RECIPES
    // --------------------------------------------------

    private void seedRecipes(SQLiteDatabase db) {

        addSampleRecipe(
                db,
                "Scrambled Eggs",
                "Beat the eggs. Melt butter in a pan. Add the eggs and stir gently until cooked.",
                new String[]{"Eggs", "Butter"},
                new double[]{2, 1},
                new String[]{"pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Egg Fried Rice",
                "Heat oil in a pan. Add cooked rice and beaten eggs. Stir-fry until the eggs are cooked.",
                new String[]{"Rice", "Eggs", "Oil"},
                new double[]{1, 2, 1},
                new String[]{"cup", "pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Pancakes",
                "Mix flour, milk and eggs into a smooth batter. Cook small portions in a lightly greased pan.",
                new String[]{"Flour", "Milk", "Eggs"},
                new double[]{1, 1, 1},
                new String[]{"cup", "cup", "pieces"}
        );

        addSampleRecipe(
                db,
                "Cheese Sandwich",
                "Place cheese between two slices of bread. Serve as is or toast until golden.",
                new String[]{"Bread", "Cheese"},
                new double[]{2, 2},
                new String[]{"slices", "slices"}
        );

        addSampleRecipe(
                db,
                "Tomato Pasta",
                "Cook the pasta. Heat oil in a pan, add chopped tomatoes and cook until soft. Mix with the pasta.",
                new String[]{"Pasta", "Tomato", "Oil"},
                new double[]{1, 2, 1},
                new String[]{"cup", "pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Banana Smoothie",
                "Blend the banana and milk until smooth. Serve immediately.",
                new String[]{"Banana", "Milk"},
                new double[]{1, 1},
                new String[]{"pieces", "cup"}
        );

        addSampleRecipe(
                db,
                "Omelette",
                "Beat the eggs and pour into a pan. Add cheese and cook until the eggs are set.",
                new String[]{"Eggs", "Cheese"},
                new double[]{2, 1},
                new String[]{"pieces", "slices"}
        );

        addSampleRecipe(
                db,
                "Garlic Rice",
                "Cook the rice. Heat oil in a pan, add chopped garlic, then stir in the rice.",
                new String[]{"Rice", "Garlic", "Oil"},
                new double[]{1, 1, 1},
                new String[]{"cup", "teaspoon", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "French Toast",
                "Beat the egg with milk. Dip the bread into the mixture and fry until golden on both sides.",
                new String[]{"Bread", "Eggs", "Milk"},
                new double[]{2, 1, 2},
                new String[]{"slices", "pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Tomato Salad",
                "Chop the tomatoes and mix with oil. Season as desired.",
                new String[]{"Tomato", "Oil"},
                new double[]{2, 1},
                new String[]{"pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Butter Toast",
                "Toast the bread and spread butter over the warm slices.",
                new String[]{"Bread", "Butter"},
                new double[]{2, 1},
                new String[]{"slices", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Cheese Omelette",
                "Beat the eggs. Melt butter in a pan, add the eggs and cheese, and cook until set.",
                new String[]{"Eggs", "Cheese", "Butter"},
                new double[]{2, 1, 1},
                new String[]{"pieces", "slices", "teaspoon"}
        );

        addSampleRecipe(
                db,
                "Banana Pancakes",
                "Mash the banana and mix with flour and egg. Cook small pancakes in a lightly greased pan.",
                new String[]{"Banana", "Flour", "Eggs"},
                new double[]{1, 1, 1},
                new String[]{"pieces", "cup", "pieces"}
        );

        addSampleRecipe(
                db,
                "Simple Porridge",
                "Combine oats and milk in a pot. Cook over low heat, stirring until thick.",
                new String[]{"Oats", "Milk"},
                new double[]{1, 1},
                new String[]{"cup", "cup"}
        );

        addSampleRecipe(
                db,
                "Garlic Butter Pasta",
                "Cook the pasta. Melt butter in a pan, add garlic, and mix with the drained pasta.",
                new String[]{"Pasta", "Butter", "Garlic"},
                new double[]{1, 1, 1},
                new String[]{"cup", "tablespoon", "teaspoon"}
        );

        addSampleRecipe(
                db,
                "Chicken Fried Rice",
                "Cook the rice and chicken. Heat oil in a pan, add the chicken and rice, and stir-fry.",
                new String[]{"Rice", "Chicken", "Oil"},
                new double[]{1, 1, 1},
                new String[]{"cup", "pieces", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Peanut Butter Toast",
                "Toast the bread and spread peanut butter evenly over the slices.",
                new String[]{"Bread", "Peanut Butter"},
                new double[]{2, 2},
                new String[]{"slices", "tablespoon"}
        );

        addSampleRecipe(
                db,
                "Apple Oatmeal",
                "Cook the oats with milk. Chop the apple and add it to the oatmeal.",
                new String[]{"Oats", "Milk", "Apple"},
                new double[]{1, 1, 1},
                new String[]{"cup", "cup", "pieces"}
        );

        addSampleRecipe(
                db,
                "Cheesy Tomato Pasta",
                "Cook the pasta. Cook chopped tomatoes in a pan, add cheese, then mix with the pasta.",
                new String[]{"Pasta", "Tomato", "Cheese"},
                new double[]{1, 2, 2},
                new String[]{"cup", "pieces", "slices"}
        );

        addSampleRecipe(
                db,
                "Banana Oat Smoothie",
                "Blend banana, oats and milk until smooth. Serve immediately.",
                new String[]{"Banana", "Oats", "Milk"},
                new double[]{1, 0.5, 1},
                new String[]{"pieces", "cup", "cup"}
        );
    }

    private void addSampleRecipe(
            SQLiteDatabase db,
            String name,
            String instructions,
            String[] ingredientNames,
            double[] quantities,
            String[] units) {

        int recipeId = -1;

        Cursor cursor = db.rawQuery(
                "SELECT id FROM recipes WHERE name = ? LIMIT 1",
                new String[]{name}
        );

        try {
            if (cursor.moveToFirst()) {
                recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );
            }
        } finally {
            cursor.close();
        }

        if (recipeId == -1) {

            ContentValues recipeValues = new ContentValues();
            recipeValues.put("name", name);
            recipeValues.put("instructions", instructions);

            long insertedId = db.insert(
                    "recipes",
                    null,
                    recipeValues
            );

            if (insertedId == -1) {
                return;
            }

            recipeId = (int) insertedId;

        } else {

            ContentValues recipeValues = new ContentValues();
            recipeValues.put("instructions", instructions);

            db.update(
                    "recipes",
                    recipeValues,
                    "id = ?",
                    new String[]{String.valueOf(recipeId)}
            );
        }

        Cursor ingredientCursor = db.rawQuery(
                "SELECT COUNT(*) FROM recipe_ingredients WHERE recipe_id = ?",
                new String[]{String.valueOf(recipeId)}
        );

        boolean alreadyHasIngredients;

        try {
            ingredientCursor.moveToFirst();
            alreadyHasIngredients = ingredientCursor.getInt(0) > 0;
        } finally {
            ingredientCursor.close();
        }

        // Do not duplicate recipe requirements every time recipes are loaded.
        if (alreadyHasIngredients) {
            return;
        }

        for (int i = 0; i < ingredientNames.length; i++) {

            ContentValues ingredientValues = new ContentValues();
            ingredientValues.put("recipe_id", recipeId);
            ingredientValues.put("ingredient_name", ingredientNames[i]);
            ingredientValues.put("required_quantity", quantities[i]);
            ingredientValues.put("unit", units[i]);

            db.insert(
                    "recipe_ingredients",
                    null,
                    ingredientValues
            );
        }
    }
}
