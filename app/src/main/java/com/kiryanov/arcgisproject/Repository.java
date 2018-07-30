package com.kiryanov.arcgisproject;

import android.graphics.Color;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Evgeniy on 30.07.18.
 */

public class Repository {

    private static Repository instance;

    private Repository() {}

    public static Repository getInstance() {
        if (instance == null)
            instance = new Repository();

        return instance;
    }

    private static final String URL_DISTRICTS = "https://gisro.donland.ru/api/vector_layers/1/records/?polygonbox=POLYGON((45.4833984375%2051.364921488259526,%2045.4833984375%2044.6061127451739,%2035.496826171875%2044.6061127451739,%2035.496826171875%2051.364921488259526,45.4833984375%2051.364921488259526))";
    private static final String URL_SETTLEMENT = "http://192.168.202.136:7999/api/vector_layers/179/records/?polygonbox=POLYGON((45.4833984375%2051.364921488259526,%2045.4833984375%2044.6061127451739,%2035.496826171875%2044.6061127451739,%2035.496826171875%2051.364921488259526,45.4833984375%2051.364921488259526))";

    public Observable<Overlay> getDistricts(Toast toast){
        return Observable.fromCallable(() -> getRequest(URL_DISTRICTS))
                .subscribeOn(Schedulers.io())
                .doOnNext(s -> toast.show())
                .observeOn(Schedulers.computation())
                .map(geoJson -> {
                    FolderOverlay folderOverlay = new FolderOverlay();

                    JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
                    JsonArray features = object.getAsJsonArray("features");

                    for (JsonElement fe : features) {
                        JsonObject fo = fe.getAsJsonObject();

                        JsonArray coordinates = fo
                                .getAsJsonObject("geometry")
                                .getAsJsonArray("coordinates")
                                .get(0).getAsJsonArray();

                        List<GeoPoint> geoPoints = new ArrayList<>();

                        geoPoints.clear();
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

                        Polygon polygon = new Polygon();
                        polygon.setFillColor(Color.GRAY);
                        polygon.setStrokeWidth(1f);
                        polygon.setPoints(geoPoints);

                        folderOverlay.add(polygon);
                    }

                    return folderOverlay;
                })
                .flatMap(folderOverlay -> Observable.fromIterable(folderOverlay.getItems()))
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
