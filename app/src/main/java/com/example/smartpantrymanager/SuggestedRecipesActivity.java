
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ViewGroup;
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

        recipeContainer = new LinearLayout(this);
        recipeContainer.setOrientation(LinearLayout.VERTICAL);
        recipeContainer.setPadding(0, 12, 0, 12);

        if (message.getParent() instanceof LinearLayout) {
            LinearLayout parent = (LinearLayout) message.getParent();
            int index = parent.indexOfChild(message);
            parent.addView(recipeContainer, index + 1,
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        databaseHelper.insertSampleRecipes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayRecipes();
    }

    private void displayRecipes() {
        recipeContainer.removeAllViews();

        Cursor cursor = databaseHelper.getAvailableRecipes();

        if (cursor.getCount() == 0) {
            message.setText("No recipes available yet. Add the required ingredients and quantities to your pantry.");
        } else {
            message.setText("Recipes you can make:");
        }

        while (cursor.moveToNext()) {
            int recipeId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String instructions = cursor.getString(cursor.getColumnIndexOrThrow("instructions"));

            TextView recipe = new TextView(this);
            recipe.setText(name + "\n" + instructions + "\nTap to view details");
            recipe.setTextSize(16);
            recipe.setTextColor(0xFF333333);
            recipe.setBackgroundColor(0xFFFFE8F2);
            recipe.setPadding(20, 20, 20, 20);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
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

        cursor.close();
    }
}
