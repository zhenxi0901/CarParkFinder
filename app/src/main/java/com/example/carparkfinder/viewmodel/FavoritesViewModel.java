package com.example.carparkfinder.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.carparkfinder.database.AppDatabase;
import com.example.carparkfinder.database.CarParkDao;
import com.example.carparkfinder.model.CarParkEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesViewModel extends AndroidViewModel {
    private final CarParkDao dao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final MutableLiveData<List<CarParkEntity>> favoriteCarParks = new MutableLiveData<>();

    public FavoritesViewModel(Application application) {
        super(application);
        dao = AppDatabase.getDatabase(application).carParkDao();
        loadFavorites();
    }

    private void loadFavorites() {
        executorService.execute(() -> favoriteCarParks.postValue(dao.getAllFavorites()));
    }

    public void addFavorite(CarParkEntity carPark) {
        executorService.execute(() -> {
            dao.insertFavorite(carPark);
            loadFavorites(); //Update after insert
        });
    }

    public void removeFavorite(String carParkId) {
        executorService.execute(() -> {
            dao.deleteFavorite(carParkId);
            loadFavorites(); //Update after delete
        });
    }

    public LiveData<List<CarParkEntity>> getFavorites() {
        return favoriteCarParks;
    }
}