package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PantryActivity extends AppCompatActivity {

    Button btnAddIngredient;
    LinearLayout ingredientContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        ingredientContainer = findViewById(R.id.ingredientContainer);

        databaseHelper = new DatabaseHelper(this);

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(PantryActivity.this,
                    AddIngredientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayIngredients();
    }

    private void displayIngredients() {
        ingredientContainer.removeAllViews();

        Cursor cursor = databaseHelper.getReadableDatabase()
                .rawQuery("SELECT * FROM ingredients", null);

        if (cursor.getCount() == 0) {
            TextView emptyMessage = new TextView(this);
            emptyMessage.setText("Your pantry is empty. Add your first ingredient!");
            emptyMessage.setTextSize(16);
            emptyMessage.setPadding(8, 16, 8, 16);
            ingredientContainer.addView(emptyMessage);
        } else {
            while (cursor.moveToNext()) {
                StringBuilder details = new StringBuilder();

                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (i > 0) {
                        details.append(" | ");
                    }

                    details.append(cursor.getColumnName(i))
                            .append(": ")
                            .append(cursor.getString(i));
                }

                TextView ingredient = new TextView(this);
                ingredient.setText(details.toString());
                ingredient.setTextSize(16);
                ingredient.setPadding(12, 16, 12, 16);

                ingredientContainer.addView(ingredient);
            }
        }

        cursor.close();
    }
}