package com.example.carparkfinder

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.carparkfinder.api.RetrofitClient
import com.example.carparkfinder.model.CarParkApiResponse
import com.example.carparkfinder.ui.theme.CarParkFinderTheme
import com.example.carparkfinder.util.CSVReaderUtil
import com.example.carparkfinder.view.CarParkDetailsActivity
import com.example.carparkfinder.view.FavoritesActivity
import com.example.carparkfinder.view.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if user is logged in
        val prefs: SharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        if (!prefs.getBoolean("isLoggedIn", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContent {
            CarParkFinderTheme {
                CarParkListScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarParkListScreen() {
    val context = LocalContext.current
    val carParks = remember { mutableStateListOf<CarParkApiResponse.CarParkData>() }
    val isLoading = remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val carparkMap = remember { CSVReaderUtil.loadCarparkData(context) }

    // Fetch car park data from API
    LaunchedEffect(Unit) {
        val apiService = RetrofitClient.getClient()
        apiService.getCarParkAvailability().enqueue(object : Callback<CarParkApiResponse> {
            override fun onResponse(call: Call<CarParkApiResponse>, response: Response<CarParkApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val apiResponse = response.body()
                    if (!apiResponse!!.items.isEmpty()) {
                        val latestItem = apiResponse.items[0]
                        carParks.clear()
                        carParks.addAll(latestItem.carparkData)
                        isLoading.value = false
                    }
                }
            }

            override fun onFailure(call: Call<CarParkApiResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch car park data", t)
                isLoading.value = false
            }
        })
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Car Park Finder") },
                    actions = {
                        Button(
                            onClick = {
                                val intent = Intent(context, FavoritesActivity::class.java)
                                context.startActivity(intent)
                            },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("View Favorites")
                        }
                        Button(
                            onClick = { logout(context) },
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Text("Logout")
                        }
                    }
                )

                // Search Bar
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    placeholder = { Text("Search by Address") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { /* Optional: Handle search action */ })
                )
            }
        }
    ) { innerPadding ->
        if (isLoading.value) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                items(carParks.filter { carPark ->
                    val carparkDetails = carparkMap?.get(carPark.carparkNumber)
                    val address = carparkDetails?.get(0) ?: ""
                    address.lowercase().contains(searchQuery.lowercase())
                }) { carPark ->
                    val carparkDetails = carparkMap?.get(carPark.carparkNumber)

                    if (carparkDetails == null || carparkDetails.size < 9) {
                        Log.e("MainActivity", "Skipping Missing CSV Data for Car Park ID: ${carPark.carparkNumber}")
                        return@items
                    }

                    val address = carparkDetails[0]
                    val carparkType = carparkDetails[1]
                    val parkingSystem = carparkDetails[2]
                    val shortTermParking = carparkDetails[3]
                    val freeParking = carparkDetails[4]
                    val nightParking = carparkDetails[5]
                    val decks = carparkDetails[6]
                    val gantryHeight = carparkDetails[7]
                    val basement = carparkDetails[8]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable {
                                val intent = Intent(context, CarParkDetailsActivity::class.java).apply {
                                    putExtra("CAR_PARK_ID", carPark.carparkNumber)
                                    putExtra("CAR_PARK_ADDRESS", address)

                                    putExtra("CAR_PARK_TYPE", carparkType)
                                    putExtra("PARKING_SYSTEM", parkingSystem)
                                    putExtra("SHORT_TERM_PARKING", shortTermParking)
                                    putExtra("FREE_PARKING", freeParking)
                                    putExtra("NIGHT_PARKING", nightParking)
                                    putExtra("CAR_PARK_DECKS", decks)
                                    putExtra("GANTRY_HEIGHT", gantryHeight)
                                    putExtra("BASEMENT", basement)
                                    putExtra("TOTAL_LOTS", carPark.carparkInfo[0].totalLots)
                                    putExtra("LOT_TYPE", carPark.carparkInfo[0].lotType)
                                    putExtra("AVAILABLE_LOTS", carPark.carparkInfo[0].lotsAvailable)
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Carpark: ${carPark.carparkNumber}", style = MaterialTheme.typography.titleLarge)
                            Text(text = "Address: $address", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}

fun logout(context: Context) {
    val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("isLoggedIn", false).apply()
    context.startActivity(Intent(context, LoginActivity::class.java))
}
