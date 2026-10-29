
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        databaseHelper = new DatabaseHelper(this);

        TextView txtRecipeName = findViewById(R.id.txtRecipeName);
        TextView txtRecipeIngredients = findViewById(R.id.txtRecipeIngredients);
        TextView txtRecipeInstructions = findViewById(R.id.txtRecipeInstructions);

        int recipeId = getIntent().getIntExtra("recipeId", -1);
        String recipeName = getIntent().getStringExtra("recipeName");
        String instructions = getIntent().getStringExtra("instructions");

        txtRecipeName.setText(recipeName == null ? "Recipe Details" : recipeName);
        txtRecipeInstructions.setText(
                instructions == null ? "No instructions available." : instructions);

        StringBuilder ingredientsText = new StringBuilder();

        if (recipeId != -1) {
            Cursor cursor = databaseHelper.getRecipeIngredients(recipeId);

            while (cursor.moveToNext()) {
                String name = cursor.getString(0);
                double quantity = cursor.getDouble(1);
                String unit = cursor.getString(2);

                ingredientsText.append("• ")
                        .append(name)
                        .append(" — ")
                        .append(quantity)
                        .append(" ")
                        .append(unit)
                        .append("\n");
            }

            cursor.close();
        }

        if (ingredientsText.length() == 0) {
            ingredientsText.append("No ingredients listed.");
        }

        txtRecipeIngredients.setText(ingredientsText.toString());
    }
}
