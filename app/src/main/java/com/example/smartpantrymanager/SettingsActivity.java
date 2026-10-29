
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        databaseHelper = new DatabaseHelper(this);

        Button backButton = findViewById(R.id.btnSettingsBack);
        Button clearButton = findViewById(R.id.btnClearPantry);

        backButton.setOnClickListener(v -> finish());

        clearButton.setOnClickListener(v ->
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Clear Pantry")
                        .setMessage(
                                "Are you sure you want to delete all pantry ingredients?"
                        )
                        .setPositiveButton("Clear", (dialog, which) -> {
                            int deleted = databaseHelper
                                    .getWritableDatabase()
                                    .delete("ingredients", null, null);

                            Toast.makeText(
                                    SettingsActivity.this,
                                    deleted + " ingredient(s) deleted",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
        );
    }
}
