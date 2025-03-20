package com.example.carparkfinder.util;

import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses the CSV file and loads carpark data.
 */
public class CSVReaderUtil {
    private static final String TAG = "CSVReaderUtil";
    private static final String CSV_FILE_NAME = "HDBCarparkInformation.csv"; // Ensure this file exists in assets

    public static Map<String, String[]> loadCarparkData(Context context) {
        Map<String, String[]> carparkData = new HashMap<>();

        try {
            InputStream inputStream = context.getAssets().open(CSV_FILE_NAME);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                // Skip header row
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Split CSV line
                String[] columns = line.split(",");

                // Log the number of columns found in each row
                Log.d(TAG, "Processing row with " + columns.length + " columns");

                // Ensure there are at least 12 columns (to avoid IndexOutOfBoundsException)
                if (columns.length < 12) {
                    Log.e(TAG, "Skipping Invalid Row (Not Enough Columns): " + line);
                    continue;
                }

                // **Correct Mapping of Columns Based on Your Provided Indexes**
                String carparkNumber = columns[0].trim(); //  Car Park Number
                String address = columns[1].trim(); //  Address (Corrected)
                String xCoord = columns[2].trim(); // X Coordinate (Not Used)
                String yCoord = columns[3].trim(); // Y Coordinate (Not Used)
                String carparkType = columns[4].trim(); //  Car Park Type
                String parkingSystem = columns[5].trim(); //  Type of Parking System
                String shortTermParking = columns[6].trim(); //  Short-Term Parking
                String freeParking = columns[7].trim(); //  Free Parking
                String nightParking = columns[8].trim(); //  Night Parking
                String decks = columns[9].trim(); //  Car Park Decks
                String gantryHeight = columns[10].trim(); //  Gantry Height
                String basement = columns[11].trim(); //  Basement Parking

                // Store Data in HashMap with **corrected order**
                carparkData.put(carparkNumber, new String[]{
                        address,           //  Address (Fixed)
                        carparkType,       //  Car Park Type (Fixed)
                        parkingSystem,     //  Type of Parking System (Fixed)
                        shortTermParking,  //  Short-Term Parking (Fixed)
                        freeParking,       //  Free Parking (Fixed)
                        nightParking,      //  Night Parking (Fixed)
                        decks,             //  Car Park Decks (Fixed)
                        gantryHeight,      //  Gantry Height (Fixed)
                        basement           //  Basement Parking (Fixed)
                });

                Log.d(TAG, "Loaded Carpark: " + carparkNumber + " | Address: " + address);
            }
            reader.close();
        } catch (Exception e) {
            Log.e(TAG, "Error reading CSV file", e);
        }

        return carparkData;
    }
}
