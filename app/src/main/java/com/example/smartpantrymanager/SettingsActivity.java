
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);

        TextView title = new TextView(this);
        title.setText("Smart Pantry Manager Settings");
        title.setTextSize(22);
        layout.addView(title);

        TextView description = new TextView(this);
        description.setText(
                "Manage your pantry app.\n\n" +
                        "Your ingredients and recipes are stored locally " +
                        "on your device using SQLite.");
        description.setTextSize(16);
        description.setPadding(0, 24, 0, 24);
        layout.addView(description);

        Button clearButton = new Button(this);
        clearButton.setText("Clear Pantry");

        clearButton.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Clear Pantry")
                        .setMessage(
                                "Are you sure you want to delete all pantry ingredients?")
                        .setPositiveButton("Clear", (dialog, which) -> {
                            int deleted = databaseHelper
                                    .getWritableDatabase()
                                    .delete("ingredients", null, null);

                            Toast.makeText(
                                    this,
                                    deleted + " ingredient(s) deleted",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        layout.addView(clearButton);
        setContentView(layout);
    }
}
