
package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class IngredientAdapter
        extends RecyclerView.Adapter<IngredientAdapter.IngredientViewHolder> {

    public interface IngredientListener {
        void onEdit(Ingredient ingredient);
        void onDelete(Ingredient ingredient);
    }

    private final List<Ingredient> ingredients;
    private final IngredientListener listener;

    public IngredientAdapter(
            List<Ingredient> ingredients,
            IngredientListener listener) {
        this.ingredients = ingredients;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ingredient, parent, false);

        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull IngredientViewHolder holder, int position) {

        Ingredient ingredient = ingredients.get(position);

        holder.name.setText(ingredient.getName());

        holder.quantity.setText(String.format(
                Locale.getDefault(),
                "Quantity: %s %s",
                String.valueOf(ingredient.getQuantity()),
                ingredient.getUnit()
        ));

        holder.editButton.setOnClickListener(
                v -> listener.onEdit(ingredient));

        holder.deleteButton.setOnClickListener(
                v -> listener.onDelete(ingredient));
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }

    static class IngredientViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView quantity;
        Button editButton;
        Button deleteButton;

        IngredientViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txtIngredientName);
            quantity = itemView.findViewById(R.id.txtIngredientQuantity);
            editButton = itemView.findViewById(R.id.btnEditIngredient);
            deleteButton = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
