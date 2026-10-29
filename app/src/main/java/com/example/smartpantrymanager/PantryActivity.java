
package com.example.smartpantrymanager;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PantryActivity extends AppCompatActivity {

    private Button btnAddIngredient;
    private Button btnBack;
    private RecyclerView recyclerIngredients;
    private TextView txtPantryEmpty;
    private DatabaseHelper databaseHelper;
    private IngredientAdapter ingredientAdapter;
    private final List<Ingredient> ingredientList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);

        btnBack = findViewById(R.id.btnBack);
        btnAddIngredient = findViewById(R.id.btnAddIngredient);
        recyclerIngredients = findViewById(R.id.recyclerIngredients);
        txtPantryEmpty = findViewById(R.id.txtPantryEmpty);

        databaseHelper = new DatabaseHelper(this);

        recyclerIngredients.setLayoutManager(
                new LinearLayoutManager(this));

        ingredientAdapter = new IngredientAdapter(
                ingredientList,
                new IngredientAdapter.IngredientListener() {

                    @Override
                    public void onEdit(Ingredient ingredient) {
                        Intent intent = new Intent(
                                PantryActivity.this,
                                AddIngredientActivity.class);

                        intent.putExtra("ingredientId", ingredient.getId());
                        intent.putExtra("name", ingredient.getName());
                        intent.putExtra("quantity", ingredient.getQuantity());
                        intent.putExtra("unit", ingredient.getUnit());

                        startActivity(intent);
                    }

                    @Override
                    public void onDelete(Ingredient ingredient) {
                        confirmDelete(ingredient);
                    }
                });

        recyclerIngredients.setAdapter(ingredientAdapter);

        btnBack.setOnClickListener(v -> finish());

        btnAddIngredient.setOnClickListener(v -> {
            Intent intent = new Intent(
                    PantryActivity.this,
                    AddIngredientActivity.class);

            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadIngredients();
    }

    private void loadIngredients() {
        ingredientList.clear();

        Cursor cursor = databaseHelper.getAllIngredients();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow("id"));

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow("name"));

                double quantity = cursor.getDouble(
                        cursor.getColumnIndexOrThrow("quantity"));

                String unit = cursor.getString(
                        cursor.getColumnIndexOrThrow("unit"));

                ingredientList.add(
                        new Ingredient(id, name, quantity, unit));
            }
        } finally {
            cursor.close();
        }

        ingredientAdapter.notifyDataSetChanged();

        boolean isEmpty = ingredientList.isEmpty();
        txtPantryEmpty.setVisibility(
                isEmpty ? View.VISIBLE : View.GONE);

        recyclerIngredients.setVisibility(
                isEmpty ? View.GONE : View.VISIBLE);
    }

    private void confirmDelete(Ingredient ingredient) {
        new AlertDialog.Builder(PantryActivity.this)
                .setTitle("Delete Ingredient")
                .setMessage(
                        "Delete " + ingredient.getName()
                                + " from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    int deleted = databaseHelper.deleteIngredient(
                            ingredient.getId()
                    );

                    if (deleted > 0)  {
                        Toast.makeText(
                                PantryActivity.this,
                                "Ingredient deleted",
                                Toast.LENGTH_SHORT).show();

                        loadIngredients();
                    } else {
                        Toast.makeText(
                                PantryActivity.this,
                                "Could not delete ingredient",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
