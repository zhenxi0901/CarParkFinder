package com.example.carparkfinder.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.example.carparkfinder.model.CarParkEntity;
import java.util.List;

/*
insertFavorite() → Saves a car park as a favorite
getAllFavorites() → Retrieves all saved car parks
deleteFavorite() → Removes a saved car park
*/

@Dao
public interface CarParkDao {
    @Insert
    void insertFavorite(CarParkEntity carPark);

    @Query("SELECT * FROM favorite_carparks")
    List<CarParkEntity> getAllFavorites();

    @Query("DELETE FROM favorite_carparks WHERE carParkId = :carParkId")
    void deleteFavorite(String carParkId);
}

