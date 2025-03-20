package com.example.carparkfinder.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CarParkApiResponse {
    @SerializedName("items")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        @SerializedName("timestamp")
        private String timestamp;

        @SerializedName("carpark_data")
        private List<CarParkData> carparkData;

        public String getTimestamp() {
            return timestamp;
        }

        public List<CarParkData> getCarparkData() {
            return carparkData;
        }
    }

    public static class CarParkData {
        @SerializedName("carpark_number")
        private String carparkNumber;

        @SerializedName("update_datetime")
        private String updateDatetime;

        @SerializedName("carpark_info")
        private List<CarParkInfo> carparkInfo;

        public String getCarparkNumber() {
            return carparkNumber;
        }

        public String getUpdateDatetime() {  // Getter for updateDatetime
            return updateDatetime;
        }

        public List<CarParkInfo> getCarparkInfo() {
            return carparkInfo;
        }
    }

    public static class CarParkInfo {
        @SerializedName("total_lots")
        private String totalLots;

        @SerializedName("lot_type")
        private String lotType;

        @SerializedName("lots_available")
        private String lotsAvailable;

        public String getTotalLots() {
            return totalLots;
        }

        public String getLotType() {
            return lotType;
        }

        public String getLotsAvailable() {
            return lotsAvailable;
        }
    }
}
