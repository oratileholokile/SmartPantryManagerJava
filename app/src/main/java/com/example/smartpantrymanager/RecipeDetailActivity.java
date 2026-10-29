
package com.example.smartpantrymanager;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        ScrollView scrollView = new ScrollView(this);

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(24, 24, 24, 24);
        content.setBackgroundColor(0xFFFFF5F9);

        scrollView.addView(content);
        setContentView(scrollView);

        Button backButton = new Button(this);
        backButton.setText("Back");

        backButton.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        content.addView(backButton);

        String recipeName =
                getIntent().getStringExtra("recipeName");

        String instructions =
                getIntent().getStringExtra("instructions");

        int recipeId =
                getIntent().getIntExtra("recipeId", -1);

        addText(
                recipeName == null ? "Recipe Details" : recipeName,
                24,
                0xFFB83270
        );

        addText("Ingredients Required", 20, 0xFF333333);

        loadIngredients(recipeId);

        addText("Instructions", 20, 0xFFB83270);

        addText(
                instructions == null || instructions.trim().isEmpty()
                        ? "No instructions available."
                        : instructions,
                16,
                0xFF333333
        );
    }

    private void loadIngredients(int recipeId) {

        if (recipeId == -1) {
            addText("Recipe could not be identified.",
                    16, 0xFF333333);
            return;
        }

        Cursor cursor =
                databaseHelper.getRecipeIngredients(recipeId);

        if (cursor == null) {
            addText("Could not load ingredients.",
                    16, 0xFF333333);
            return;
        }

        try {
            if (cursor.getCount() == 0) {
                addText(
                        "No ingredients are linked to this recipe.",
                        16,
                        0xFF333333
                );
                return;
            }

            while (cursor.moveToNext()) {

                String ingredientName = cursor.getString(
                        cursor.getColumnIndexOrThrow("ingredient_name"));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("required_quantity"));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit"));

                addText(
                        "\u2022 " + ingredientName
                                + " - " + quantity + " " + unit,
                        16,
                        0xFF333333
                );
            }

        } finally {
            cursor.close();
        }
    }

    private void addText(String text, int size, int color) {

        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setPadding(0, 12, 0, 12);

        content.addView(view);
    }
}
