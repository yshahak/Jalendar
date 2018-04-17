package com.thedroidboy.jalendar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.google.android.gms.location.places.Place;

import net.sourceforge.zmanim.util.GeoLocation;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by B.E.L on 06/10/2016.
 */

public class LocationHelper {

    public static GeoLocation getGeoLocationFromPlace(Context context, Place place, boolean hebrew){
        double lat = place.getLatLng().latitude;
        double lng = place.getLatLng().longitude;
        String location =  getCityName(context, hebrew, lat, lng);
        if (location == null) {
            location = place.getName().toString();
        }
        return new GeoLocation(location, lat, lng, TimeZone.getDefault());
    }

    @SuppressLint("CommitPrefEdits")
    public static void saveLocation(SharedPreferences prefs, GeoLocation geoLocation) {
        SharedPreferences.Editor editor = prefs.edit();
        Utils.putDouble(editor, Constants.KEY_LATITUDE, geoLocation.getLatitude());
        Utils.putDouble(editor, Constants.KEY_LONGITUDE, geoLocation.getLongitude());
        editor
                .putString(Constants.KEY_LOCATION, geoLocation.getLocationName())
                .apply();
    }

    public static void saveLocation(Context context, SharedPreferences prefs, Place place) {
        SharedPreferences.Editor editor = prefs.edit();
        Utils.putDouble(editor, Constants.KEY_LATITUDE, place.getLatLng().latitude);
        Utils.putDouble(editor, Constants.KEY_LONGITUDE, place.getLatLng().longitude);
        editor
                .putString(Constants.KEY_LOCATION, place.getName().toString())
                .apply();
    }

    public static void updateLocationLanguage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        double lat = Utils.getDouble(prefs, Constants.KEY_LATITUDE, 31.768318999999998);
        double lng = Utils.getDouble(prefs, Constants.KEY_LONGITUDE, 35.21371);
        boolean hebrew = Utils.isLocaleHebrew(prefs);
        String location = getCityName(context, hebrew, lat, lng);
        editor
                .putString(Constants.KEY_LOCATION, location)
                .apply();
    }

    public static String getLocation(SharedPreferences prefs){
        return prefs.getString(Constants.KEY_LOCATION, Utils.isLocaleHebrew(prefs) ? "ירושלים" : "Jerusalem");
    }

    public static GeoLocation getChosenGeoLocation(SharedPreferences prefs){
        double lat = Utils.getDouble(prefs, Constants.KEY_LATITUDE, 31.768318999999998);
        double lng = Utils.getDouble(prefs, Constants.KEY_LONGITUDE, 35.21371);
        String location =  getLocation(prefs);
        return new GeoLocation(location, lat, lng, TimeZone.getDefault());
    }

    public static String getCityName(Context context, boolean isHebrew, double lat, double lng) {
        String cityName = null;
        Geocoder geocoder = new Geocoder(context, isHebrew ? new Locale("iw","IL") : Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (addresses != null && addresses.size() > 0){
            Address address = addresses.get(0);
            cityName = address.getLocality();
            if (cityName == null && address.getMaxAddressLineIndex() > 0){
                cityName = address.getAddressLine(address.getMaxAddressLineIndex() - 1);
            }
        }
        return cityName;
    }


    @SuppressWarnings("MissingPermission")
    private static Location getBestLocation(Context context){
//        Intent resultIntent = new Intent(context, BootReceiver.class);
//        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(context, 0, resultIntent, 0);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
//        locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, resultPendingIntent);

        List<String> providers = locationManager.getProviders(false);
        Location bestLocation = null;

        for (String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location == null) {
                continue;
            }
            if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = location;
            }
        }
        return bestLocation;
    }

/*
    public static GeoLocation getGeoLocationDefault(boolean hebrew){
        double lat = 31.768318999999998;
        double lng = 35.21371;
        String location =  hebrew ? "ירושלים" : "Jerusalem";
        return new GeoLocation(location, lat, lng, TimeZone.getDefault());
    }*/

}
