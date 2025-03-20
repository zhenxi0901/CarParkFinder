package com.example.carparkfinder.view;

import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.carparkfinder.R;
import com.example.carparkfinder.api.CarParkApiService;
import com.example.carparkfinder.api.RetrofitClient;
import com.example.carparkfinder.model.CarParkApiResponse;
import com.example.carparkfinder.model.CarParkEntity;
import com.example.carparkfinder.util.SVY21Converter;
import com.example.carparkfinder.viewmodel.FavoritesViewModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarParkDetailsActivity extends AppCompatActivity {
    private FavoritesViewModel favoritesViewModel;
    private TextView carParkNameText, carParkAddressText, shortTermParkingText, parkingSystemText;
    private TextView freeParkingText, nightParkingText, carparkTypeText, decksText, gantryHeightText, basementText;
    private TextView totalLotsText, lotTypeText, availableLotsText, carparkNumText, lastUpdatedText;
    private Button favoriteButton, directionsButton, makePaymentButton;

    private static final String TAG = "CarParkDetailsActivity";
    private Map<String, String[]> carParkInfoMap = new HashMap<>();
    private double carParkLatitude = 0.0;
    private double carParkLongitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpark_details);

        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);

        // Initialize UI Components
        carParkNameText = findViewById(R.id.carpark_name);
        carParkAddressText = findViewById(R.id.carpark_address);
        shortTermParkingText = findViewById(R.id.short_term_parking);
        parkingSystemText = findViewById(R.id.parking_type);
        freeParkingText = findViewById(R.id.free_parking);
        nightParkingText = findViewById(R.id.night_parking);
        carparkTypeText = findViewById(R.id.carpark_type);
        decksText = findViewById(R.id.carpark_decks);
        gantryHeightText = findViewById(R.id.gantry_height);
        basementText = findViewById(R.id.carpark_basement);
        totalLotsText = findViewById(R.id.totalLots);
        lotTypeText = findViewById(R.id.lotType);
        availableLotsText = findViewById(R.id.availableLots);
        carparkNumText = findViewById(R.id.carparkNum);
        lastUpdatedText = findViewById(R.id.lastUpdated);
        favoriteButton = findViewById(R.id.favorite_button);
        directionsButton = findViewById(R.id.SearchButton); // Get Directions Button
        makePaymentButton = findViewById(R.id.btnMakePayment); // Make Payment Button

        makePaymentButton.setOnClickListener(v -> {
            Intent intent = new Intent(CarParkDetailsActivity.this, PaymentActivity.class);
            startActivity(intent);
        });

        // Retrieve Intent Data
        String carParkId = getIntent().getStringExtra("CAR_PARK_ID");

        if (carParkId == null) {
            Toast.makeText(this, "Car Park ID is missing!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Load CSV Data
        loadCsvData();

        // Display CSV Data
        if (carParkInfoMap.containsKey(carParkId)) {
            String[] details = carParkInfoMap.get(carParkId);
            Log.d(TAG, "Displaying CSV Data for " + carParkId);

            carParkNameText.setText("Carpark: " + carParkId);
            carParkAddressText.setText("Address: " + details[0]);
            carparkTypeText.setText("Car Park Type: " + details[1]);
            parkingSystemText.setText("Parking System: " + details[2]);
            shortTermParkingText.setText("Short Term Parking: " + details[3]);
            freeParkingText.setText("Free Parking: " + details[4]);
            nightParkingText.setText("Night Parking: " + details[5]);
            decksText.setText("Decks: " + details[6]);
            gantryHeightText.setText("Gantry Height: " + details[7]);
            basementText.setText("Basement Parking: " + details[8]);

            // Convert SVY21 to WGS84
            double svyX = Double.parseDouble(details[9]);
            double svyY = Double.parseDouble(details[10]);
            double[] latLon = SVY21Converter.svy21ToWgs84(svyX, svyY);
            carParkLatitude = latLon[0];
            carParkLongitude = latLon[1];
        } else {
            carParkNameText.setText("Car Park Not Found");
        }

        // Fetch Real-Time Car Park Data
        fetchCarParkAvailability(carParkId);

        // Save to Favorites
        CarParkEntity carPark = new CarParkEntity(carParkId, carParkNameText.getText().toString(), carParkAddressText.getText().toString());
        favoriteButton.setOnClickListener(v -> {
            favoritesViewModel.addFavorite(carPark);
            Toast.makeText(this, "Car Park Saved as Favorite!", Toast.LENGTH_SHORT).show();
        });

        // Open Google Maps when "Get Directions" is Clicked
        directionsButton.setOnClickListener(v -> openGoogleMaps());


    }

    /**
     * Open Google Maps for navigation
     */
    private void openGoogleMaps() {
        if (carParkLatitude != 0.0 && carParkLongitude != 0.0) {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + carParkLatitude + "," + carParkLongitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Location coordinates not available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Load Car Park Data from CSV
     */
    private void loadCsvData() {
        AssetManager assetManager = getAssets();
        try (InputStream inputStream = assetManager.open("HDBCarparkInformation.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String line;
            reader.readLine(); // Skip header

            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");

                if (columns.length >= 12) {
                    String carParkId = columns[0].trim();
                    String address = columns[1].trim();
                    String xCoord = columns[2].trim(); // SVY21 X
                    String yCoord = columns[3].trim(); // SVY21 Y
                    String carparkType = columns[4].trim();
                    String parkingSystem = columns[5].trim();
                    String shortTermParking = columns[6].trim();
                    String freeParking = columns[7].trim();
                    String nightParking = columns[8].trim();
                    String decks = columns[9].trim();
                    String gantryHeight = columns[10].trim();
                    String basement = columns[11].trim();

                    carParkInfoMap.put(carParkId, new String[]{
                            address, carparkType, parkingSystem, shortTermParking,
                            freeParking, nightParking, decks, gantryHeight, basement,
                            xCoord, yCoord
                    });

                    Log.d(TAG, "Loaded CSV Data: " + carParkId + " -> " + address);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error reading CSV file", e);
        }
    }

    private void fetchCarParkAvailability(String carParkId) {
        CarParkApiService apiService = RetrofitClient.getClient();
        Call<CarParkApiResponse> call = apiService.getCarParkAvailability();

        call.enqueue(new Callback<CarParkApiResponse>() {
            @Override
            public void onResponse(Call<CarParkApiResponse> call, Response<CarParkApiResponse> response) {
                Log.d(TAG, " API Response Code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    CarParkApiResponse apiResponse = response.body();
                    Log.d(TAG, "API Response: " + response.body().toString());

                    if (!apiResponse.getItems().isEmpty()) {
                        CarParkApiResponse.Item latestItem = apiResponse.getItems().get(0);
                        boolean found = false;

                        for (CarParkApiResponse.CarParkData carpark : latestItem.getCarparkData()) {
                            Log.d(TAG, " Checking Carpark ID: " + carpark.getCarparkNumber());
                            if (carpark.getCarparkNumber().equalsIgnoreCase(carParkId)) {
                                CarParkApiResponse.CarParkInfo info = carpark.getCarparkInfo().get(0);

                                totalLotsText.setText("Total Lots: " + info.getTotalLots());
                                lotTypeText.setText("Lot Type: " + info.getLotType());
                                availableLotsText.setText("Available Lots: " + info.getLotsAvailable());
                                carparkNumText.setText("Carpark Number: " + carpark.getCarparkNumber());
                                lastUpdatedText.setText("Last Updated: " + latestItem.getTimestamp());

                                found = true;
                                break;
                            }
                        }

                        if (!found) {
                            Log.w(TAG, "️ Car Park ID not found in API response.");
                            Toast.makeText(CarParkDetailsActivity.this, "Car Park ID not found!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, " API response is empty.");
                    }
                } else {
                    Log.e(TAG, " API call failed. HTTP Code: " + response.code());
                    try {
                        Log.e(TAG, " Error Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        Log.e(TAG, " Error reading response body", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<CarParkApiResponse> call, Throwable t) {
                Log.e(TAG, " Network Error fetching car park availability", t);
            }
        });
    }
}

