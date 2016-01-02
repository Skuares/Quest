package com.skuares.studio.quest;

import com.shaded.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by salim on 1/2/2016.
 */
public class UserPlace {

    String placeName;
    String placeAddress;
    String placeId;
    double placeLatitude;
    double placeLongitude;


    public UserPlace(){}
    /*
    public UserPlace(String placeName, String placeAddress, String placeId,double placeLatitude, double placeLongitude){
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeId = placeId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;
    }
    */

    public UserPlace(@JsonProperty("placeName") String placeName,
                     @JsonProperty("placeAddress") String placeAddress,
                     @JsonProperty("placeId") String placeId,
                     @JsonProperty("placeLatitude") double placeLatitude,
                     @JsonProperty("placeLongitude") double placeLongitude){

        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.placeId = placeId;
        this.placeLatitude = placeLatitude;
        this.placeLongitude = placeLongitude;

    }



    // no setters
    // to change you need to change the whole object all at once


    public String getPlaceName() {
        return placeName;
    }

    public String getPlaceAddress() {
        return placeAddress;
    }

    public double getPlaceLatitude() {
        return placeLatitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public double getPlaceLongitude() {
        return placeLongitude;
    }
}

