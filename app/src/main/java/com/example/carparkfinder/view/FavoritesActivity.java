package com.example.carparkfinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.carparkfinder.R;
import com.example.carparkfinder.model.CarParkEntity;
import com.example.carparkfinder.viewmodel.FavoritesViewModel;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private FavoritesViewModel favoritesViewModel;
    private ListView listView;
    private FavoritesAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        listView = findViewById(R.id.favorites_list);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Load favorites into list
        favoritesViewModel.getFavorites().observe(this, favorites -> {
            adapter = new FavoritesAdapter(this, favorites, favoritesViewModel);
            listView.setAdapter(adapter);
        });

        // Clicking a favorite opens CarParkDetailsActivity
        listView.setOnItemClickListener((parent, view, position, id) -> {
            CarParkEntity selectedCarPark = adapter.getItem(position);
            if (selectedCarPark != null) {
                Intent intent = new Intent(FavoritesActivity.this, CarParkDetailsActivity.class);
                intent.putExtra("CAR_PARK_ID", selectedCarPark.getCarParkId());
                intent.putExtra("CAR_PARK_NAME", selectedCarPark.getName());
                intent.putExtra("CAR_PARK_LOCATION", selectedCarPark.getLocation());
                intent.putExtra("CAR_PARK_PRICE", "$2.50 per hour"); // Static for now
                startActivity(intent);
            } else {
                Toast.makeText(FavoritesActivity.this, "Car park data missing!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}