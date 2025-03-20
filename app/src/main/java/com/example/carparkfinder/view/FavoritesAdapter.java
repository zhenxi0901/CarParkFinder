package com.example.carparkfinder.view;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import com.example.carparkfinder.R;
import com.example.carparkfinder.model.CarParkEntity;
import com.example.carparkfinder.viewmodel.FavoritesViewModel;
import java.util.List;

public class FavoritesAdapter extends ArrayAdapter<CarParkEntity> {
    private FavoritesViewModel favoritesViewModel;

    public FavoritesAdapter(Context context, List<CarParkEntity> favorites, FavoritesViewModel viewModel) {
        super(context, 0, favorites);
        this.favoritesViewModel = viewModel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_favorite, parent, false);
        }

        CarParkEntity carPark = getItem(position);
        TextView name = convertView.findViewById(R.id.fav_carpark_name);
        TextView location = convertView.findViewById(R.id.fav_carpark_location);
        TextView deleteButton = convertView.findViewById(R.id.delete_button); //Added delete button

        if (carPark != null) {
            name.setText(carPark.getName());
            location.setText(carPark.getLocation());

            // Clicking a car park opens details
            convertView.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), CarParkDetailsActivity.class);
                intent.putExtra("CAR_PARK_ID", carPark.getCarParkId());
                intent.putExtra("CAR_PARK_NAME", carPark.getName());
                intent.putExtra("CAR_PARK_LOCATION", carPark.getLocation());
                getContext().startActivity(intent);
            });

            // Deleting a favorite
            deleteButton.setOnClickListener(v -> {
                favoritesViewModel.removeFavorite(carPark.getCarParkId());
                Toast.makeText(getContext(), "Favorite Removed!", Toast.LENGTH_SHORT).show();
            });
        }

        return convertView;
    }
}