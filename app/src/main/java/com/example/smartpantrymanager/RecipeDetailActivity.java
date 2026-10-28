package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        TextView txtRecipeName = findViewById(R.id.txtRecipeName);
        TextView txtIngredients = findViewById(R.id.txtRecipeIngredients);
        TextView txtInstructions = findViewById(R.id.txtRecipeInstructions);

        String name = getIntent().getStringExtra("recipeName");
        String ingredients = getIntent().getStringExtra("ingredients");
        String instructions = getIntent().getStringExtra("instructions");

        txtRecipeName.setText(name == null ? "Recipe" : name);
        txtIngredients.setText(
                ingredients == null ? "No ingredients listed." : ingredients
        );
        txtInstructions.setText(
                instructions == null ? "No instructions available." : instructions
        );
    }
}
