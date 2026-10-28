package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddIngredientActivity extends AppCompatActivity {

    EditText editName, editQuantity;
    Spinner spinnerUnit;
    Button btnSave;
    DatabaseHelper databaseHelper;
    int ingredientId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredient);

        databaseHelper = new DatabaseHelper(this);

        editName = findViewById(R.id.editName);
        editQuantity = findViewById(R.id.editQuantity);
        spinnerUnit = findViewById(R.id.spinnerUnit);
        btnSave = findViewById(R.id.btnSave);

        String[] units = {"pieces", "g", "kg", "ml", "litres"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                units
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerUnit.setAdapter(adapter);

        ingredientId = getIntent().getIntExtra("ingredientId", -1);

        if (ingredientId != -1) {
            editName.setText(getIntent().getStringExtra("name"));
            editQuantity.setText(
                    String.valueOf(getIntent().getDoubleExtra("quantity", 1))
            );

            String savedUnit = getIntent().getStringExtra("unit");
            if (savedUnit != null) {
                int position = adapter.getPosition(savedUnit);
                if (position >= 0) {
                    spinnerUnit.setSelection(position);
                }
            }

            btnSave.setText("Update Ingredient");
        }

        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {
        String name = editName.getText().toString().trim();
        String quantityText = editQuantity.getText().toString().trim();

        if (name.isEmpty() || quantityText.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid quantity",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (quantity <= 0) {
            Toast.makeText(this, "Quantity must be greater than zero",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        String unit = spinnerUnit.getSelectedItem().toString();

        boolean success;

        if (ingredientId == -1) {
            success = databaseHelper.addIngredient(name, quantity, unit);
        } else {
            success = databaseHelper.updateIngredient(
                    ingredientId, name, quantity, unit
            );
        }

        if (success) {
            Toast.makeText(this, "Ingredient saved successfully",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save ingredient",
                    Toast.LENGTH_SHORT).show();
        }
    }
}