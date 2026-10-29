
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SuggestedRecipesActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout recipeContainer;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        databaseHelper = new DatabaseHelper(this);
        message = findViewById(R.id.txtRecipeMessage);

        LinearLayout parent = (LinearLayout) message.getParent();

        Button btnBack = new Button(this);
        btnBack.setText("Back");
        btnBack.setOnClickListener(v -> finish());

        LinearLayout.LayoutParams backParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        parent.addView(btnBack, parent.indexOfChild(message), backParams);

        Button btnAllRecipes = new Button(this);
        btnAllRecipes.setText("All Recipes");

        Button btnAvailableRecipes = new Button(this);
        btnAvailableRecipes.setText("Find Recipes I Can Make");

        int messageIndex = parent.indexOfChild(message);

        parent.addView(btnAllRecipes, messageIndex + 1);
        parent.addView(btnAvailableRecipes, messageIndex + 2);

        recipeContainer = new LinearLayout(this);
        recipeContainer.setOrientation(LinearLayout.VERTICAL);
        recipeContainer.setPadding(0, 12, 0, 12);

        parent.addView(
                recipeContainer,
                messageIndex + 3,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

        btnAllRecipes.setOnClickListener(v -> displayRecipes(false));

        btnAvailableRecipes.setOnClickListener(
                v -> displayRecipes(true));

        databaseHelper.insertSampleRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRecipes(false);
    }

    private void displayRecipes(boolean availableOnly) {
        recipeContainer.removeAllViews();

        Cursor cursor;

        if (availableOnly) {
            cursor = databaseHelper.getAvailableRecipes();
        } else {
            cursor = databaseHelper.getAllRecipes();
        }

        try {
            if (cursor.getCount() == 0) {
                if (availableOnly) {
                    message.setText(
                            "No recipes can be made with your current pantry ingredients. Add more ingredients or quantities.");
                } else {
                    message.setText("No recipes found.");
                }
                return;
            }

            if (availableOnly) {
                message.setText("Recipes You Can Make:");
            } else {
                message.setText("All Recipes:");
            }

            while (cursor.moveToNext()) {
                final int recipeId = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                final String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                final String instructions = cursor.getString(
                        cursor.getColumnIndexOrThrow("instructions"));

                TextView recipe = new TextView(this);

                recipe.setText(
                        name + "\n\n" + instructions +
                                "\n\nTap to view details");

                recipe.setTextSize(16);
                recipe.setTextColor(0xFF333333);
                recipe.setBackgroundColor(0xFFFFE8F2);
                recipe.setPadding(20, 20, 20, 20);

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);

                params.setMargins(0, 0, 0, 16);
                recipe.setLayoutParams(params);

                recipe.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            SuggestedRecipesActivity.this,
                            RecipeDetailActivity.class);

                    intent.putExtra("recipeId", recipeId);
                    intent.putExtra("recipeName", name);
                    intent.putExtra("instructions", instructions);

                    startActivity(intent);
                });

                recipeContainer.addView(recipe);
            }

        } finally {
            cursor.close();
        }
    }
}
