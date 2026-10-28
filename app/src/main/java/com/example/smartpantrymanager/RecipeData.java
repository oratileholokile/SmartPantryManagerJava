
package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class RecipeData {

    public static class Ingredient {
        public String name;
        public double quantity;
        public String unit;

        public Ingredient(String name, double quantity, String unit) {
            this.name = name;
            this.quantity = quantity;
            this.unit = unit;
        }
    }

    public static class Recipe {
        public String name;
        public String instructions;
        public List<Ingredient> ingredients;

        public Recipe(String name, String instructions,
                      Ingredient... ingredients) {
            this.name = name;
            this.instructions = instructions;
            this.ingredients = Arrays.asList(ingredients);
        }
    }

    private static Ingredient i(String name, double quantity, String unit) {
        return new Ingredient(name, quantity, unit);
    }

    public static List<Recipe> getRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        recipes.add(new Recipe("Scrambled Eggs",
                "1. Beat the eggs in a bowl.\n2. Heat a pan.\n3. Cook the eggs while stirring until set.",
                i("eggs", 2, "pieces")));

        recipes.add(new Recipe("Banana Smoothie",
                "1. Peel the banana.\n2. Blend the banana with milk until smooth.",
                i("banana", 1, "pieces"), i("milk", 200, "ml")));

        recipes.add(new Recipe("Cheese Omelette",
                "1. Beat the eggs.\n2. Cook them in a pan.\n3. Add cheese and fold the omelette.",
                i("eggs", 2, "pieces"), i("cheese", 30, "g")));

        recipes.add(new Recipe("Tomato Pasta",
                "1. Boil the pasta.\n2. Chop and cook the tomatoes.\n3. Mix the pasta with the tomatoes.",
                i("pasta", 100, "g"), i("tomato", 2, "pieces")));

        recipes.add(new Recipe("Peanut Butter Sandwich",
                "1. Spread peanut butter on the bread.\n2. Put the slices together.",
                i("bread", 2, "pieces"), i("peanut butter", 20, "g")));

        recipes.add(new Recipe("Milk Porridge",
                "1. Heat the milk.\n2. Add oats and stir until soft.",
                i("milk", 250, "ml"), i("oats", 50, "g")));

        recipes.add(new Recipe("Tomato Egg Stir",
                "1. Chop the tomatoes.\n2. Cook them in a pan.\n3. Add beaten eggs and cook through.",
                i("tomato", 2, "pieces"), i("eggs", 2, "pieces")));

        recipes.add(new Recipe("Cheese Toast",
                "1. Put cheese on the bread.\n2. Toast until the bread is crisp and the cheese melts.",
                i("bread", 2, "pieces"), i("cheese", 30, "g")));

        recipes.add(new Recipe("Banana Oat Bowl",
                "1. Slice the banana.\n2. Mix with oats and milk.",
                i("banana", 1, "pieces"), i("oats", 40, "g"),
                i("milk", 100, "ml")));

        recipes.add(new Recipe("Garlic Tomato Pasta",
                "1. Boil the pasta.\n2. Cook chopped tomatoes and garlic.\n3. Mix together.",
                i("pasta", 100, "g"), i("tomato", 2, "pieces"),
                i("garlic", 1, "pieces")));

        recipes.add(new Recipe("Cheesy Eggs",
                "1. Beat the eggs.\n2. Cook in a pan.\n3. Add cheese and stir until melted.",
                i("eggs", 2, "pieces"), i("cheese", 20, "g")));

        recipes.add(new Recipe("Peanut Banana Toast",
                "1. Toast the bread.\n2. Spread peanut butter on it.\n3. Add sliced banana.",
                i("bread", 2, "pieces"), i("peanut butter", 20, "g"),
                i("banana", 1, "pieces")));

        recipes.add(new Recipe("Tomato Cheese Toast",
                "1. Place sliced tomato and cheese on the bread.\n2. Toast until warm.",
                i("bread", 2, "pieces"), i("tomato", 1, "pieces"),
                i("cheese", 25, "g")));

        recipes.add(new Recipe("Simple Garlic Eggs",
                "1. Beat the eggs.\n2. Cook the garlic briefly in a pan.\n3. Add eggs and cook through.",
                i("eggs", 2, "pieces"), i("garlic", 1, "pieces")));

        recipes.add(new Recipe("Creamy Banana Oats",
                "1. Heat the milk.\n2. Add oats and cook until soft.\n3. Slice the banana on top.",
                i("milk", 200, "ml"), i("oats", 50, "g"),
                i("banana", 1, "pieces")));

        return recipes;
    }

    public static String normalize(String name) {
        String value = name.trim().toLowerCase(Locale.ROOT);

        if (value.equals("eggs")) return "egg";
        if (value.equals("tomatoes")) return "tomato";
        if (value.equals("potatoes")) return "potato";
        if (value.equals("bananas")) return "banana";
        if (value.equals("loaves")) return "loaf";

        if (value.endsWith("s") && value.length() > 3) {
            return value.substring(0, value.length() - 1);
        }

        return value;
    }
}
