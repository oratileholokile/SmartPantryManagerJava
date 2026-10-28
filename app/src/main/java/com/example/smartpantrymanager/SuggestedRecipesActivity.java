
package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SuggestedRecipesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested_recipes);

        TextView txtRecipeMessage = findViewById(R.id.txtRecipeMessage);

        txtRecipeMessage.setText(
                "Your matching recipes will appear here."
        );
    }
}
