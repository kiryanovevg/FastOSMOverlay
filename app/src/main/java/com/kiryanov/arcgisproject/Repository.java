package com.kiryanov.arcgisproject;

import android.content.Context;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Evgeniy on 13.08.18.
 */

public class Repository {

    public static final double LAT = 47.2;
    public static final double LNG = 39.7;

    private static Repository instance;

    private Repository() {}

    public static Repository getInstance() {
        if (instance == null)
            instance = new Repository();

        return instance;
    }

    @FunctionalInterface
    interface AddMarker {
        void addMarker(double lat, double lng);
    }

    private static final String URL_DISTRICTS = "https://gisro.donland.ru/api/vector_layers/1/records/?polygonbox=POLYGON((45.4833984375%2051.364921488259526,%2045.4833984375%2044.6061127451739,%2035.496826171875%2044.6061127451739,%2035.496826171875%2051.364921488259526,45.4833984375%2051.364921488259526))";

    private double markerOffset = 0;

    public void addPoints(int count, AddMarker addMarker, Runnable setButtonText) {
        Random random = new Random();
        int accuracy = 1000000;
        double density = 0.5;

        for (int i = 0; i < count; i++) {
            double dispersionX = getDispersion(random.nextInt(), accuracy, density);
            double dispersionY = getDispersion(random.nextInt(), accuracy, density);

            double x = LAT + dispersionX + markerOffset;
            double y = LNG + dispersionY + markerOffset;

            addMarker.addMarker(x, y);
        }

        if (markerOffset > 80) markerOffset = -80;
        markerOffset += density;

        setButtonText.run();
    }

    //X, Y
    private double getDispersion(int random, int accuracy, double density) {
        return (((double) random * random) / accuracy) % density;
    }

    public Observable<List<IGeoPoint>> getPointsFromGeoJson(Context context) {

        Observable<List<IGeoPoint>> main = Observable.fromCallable(() -> getRequest(URL_DISTRICTS))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> Toast.makeText(context, "GeoJson loading", Toast.LENGTH_SHORT).show())
                .observeOn(Schedulers.computation())
                .map(geoJson -> {
                    JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
                    JsonArray features = object.getAsJsonArray("features");

                    List<IGeoPoint> geoPoints = new ArrayList<>();
                    for (JsonElement fe : features) {
                        JsonObject fo = fe.getAsJsonObject();

                        JsonArray coordinates = fo
                                .getAsJsonObject("geometry")
                                .getAsJsonArray("coordinates")
                                .get(0).getAsJsonArray();

                        for (JsonElement element : coordinates) {
                            JsonArray coord = element.getAsJsonArray();

                            JsonElement first = coord.get(0);
                            JsonElement second = coord.get(1);

                            if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
                                geoPoints.add(new GeoPoint(
                                        second.getAsDouble(),
                                        first.getAsDouble()
                                ));
                            }
                        }
                    }

                    return geoPoints;
                })
                .flatMap(Observable::fromIterable)
                .buffer(10000).take(2);


        Observable<Long> interval = Observable.interval(0, TimeUnit.MILLISECONDS);
//        Observable<Long> interval = Observable.interval(500, TimeUnit.MILLISECONDS);

        return Observable.zip(main, interval, (overlay, aLong) -> overlay)
                .observeOn(AndroidSchedulers.mainThread());
    }

    private String getRequest(String url) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                new URL(url).openConnection().getInputStream()
        ));

        String input;
        while ((input = reader.readLine()) != null) {
            builder.append(input);
        }

        reader.close();

        return builder.toString();
    }
}
