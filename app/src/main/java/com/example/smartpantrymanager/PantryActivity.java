package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class PantryActivity extends AppCompatActivity {

    Button btnAddIngredient;
    Button btnBack;
    LinearLayout ingredientContainer;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        btnBack = findViewById(R.id.btnBack);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        ingredientContainer = findViewById(R.id.ingredientContainer);

        databaseHelper = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class
            );
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

        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            if (cursor.getCount() == 0) {
                TextView emptyMessage = new TextView(this);
                emptyMessage.setText(
                        "Your pantry is empty. Add your first ingredient!"
                );
                emptyMessage.setTextSize(16);
                emptyMessage.setTextColor(0xFF9C4778);
                emptyMessage.setPadding(8, 16, 8, 16);
                ingredientContainer.addView(emptyMessage);
            }

            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id")
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name")
                );

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity")
                );

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit")
                );

                LinearLayout item = new LinearLayout(this);
                item.setOrientation(LinearLayout.VERTICAL);
                item.setPadding(12, 12, 12, 12);
                item.setBackgroundColor(0xFFFFE8F2);

                LinearLayout.LayoutParams itemParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                itemParams.setMargins(0, 0, 0, 12);
                item.setLayoutParams(itemParams);

                TextView details = new TextView(this);
                details.setText(
                        name + "\nQuantity: " + quantity + " " + unit
                );
                details.setTextSize(16);
                details.setTextColor(0xFF333333);
                item.addView(details);

                LinearLayout buttons = new LinearLayout(this);
                buttons.setOrientation(LinearLayout.HORIZONTAL);

                Button editButton = new Button(this);
                editButton.setText("Edit");

                editButton.setOnClickListener(v -> {
                    Intent intent = new Intent(
                            PantryActivity.this,
                            AddIngredientActivity.class
                    );

                    intent.putExtra("ingredientId", id);
                    intent.putExtra("name", name);
                    intent.putExtra("quantity", quantity);
                    intent.putExtra("unit", unit);

                    startActivity(intent);
                });

                Button deleteButton = new Button(this);
                deleteButton.setText("Delete");

                deleteButton.setOnClickListener(v ->
                        new AlertDialog.Builder(PantryActivity.this)
                                .setTitle("Delete Ingredient")
                                .setMessage(
                                        "Delete " + name +
                                                " from your pantry?"
                                )
                                .setPositiveButton(
                                        "Delete",
                                        (dialog, which) -> {
                                            boolean deleted =
                                                    databaseHelper
                                                            .deleteIngredient(id);

                                            if (deleted) {
                                                Toast.makeText(
                                                        PantryActivity.this,
                                                        "Ingredient deleted",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                                displayIngredients();
                                            } else {
                                                Toast.makeText(
                                                        PantryActivity.this,
                                                        "Could not delete ingredient",
                                                        Toast.LENGTH_SHORT
                                                ).show();
                                            }
                                        }
                                )
                                .setNegativeButton("Cancel", null)
                                .show()
                );

                buttons.addView(editButton);
                buttons.addView(deleteButton);
                item.addView(buttons);
                ingredientContainer.addView(item);

                View divider = new View(this);
                divider.setBackgroundColor(0xFFDDDDDD);

                LinearLayout.LayoutParams dividerParams =
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                1
                        );

                divider.setLayoutParams(dividerParams);
                ingredientContainer.addView(divider);
            }
        } finally {
            cursor.close();
        }
    }
}
