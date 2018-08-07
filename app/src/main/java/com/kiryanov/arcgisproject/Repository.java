package com.kiryanov.arcgisproject;

import android.content.Context;
import android.graphics.Color;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.PointReducer;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.Polygon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
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

    public Observable<List<Polygon>> getDistricts(Context context, double tolerance) {
        return getGeoJson(context, URL_DISTRICTS, tolerance);
    }

    public Observable<List<Polygon>> getSettlement(Context context, double tolerance) {
        return getGeoJson(context, URL_SETTLEMENT, tolerance);
    }

    public Observable<List<Polygon>> getCustomPolygon(Context context, double tolerance) {
        String geoJson = "{ \"type\": \"FeatureCollection\", \"features\": [ { \"type\": \"Feature\", \"properties\": {}, \"geometry\": { \"type\": \"Polygon\", \"coordinates\": [ [ [ 39.8583984375, 48.84302835299516 ], [ 39.70458984374999, 48.58932584966975 ], [ 39.82543945312499, 48.55297816440071 ], [ 39.90234375, 48.4146186174932 ], [ 39.990234375, 48.29050321714062 ], [ 39.94628906249999, 48.11476663187632 ], [ 39.781494140625, 47.88688085106901 ], [ 38.97949218749999, 47.85740289465826 ], [ 38.86962890625, 47.77625204393233 ], [ 38.759765625, 47.68018294648414 ], [ 38.3642578125, 47.5913464767971 ], [ 38.243408203125, 47.4057852900587 ], [ 38.25439453125, 47.25686404408872 ], [ 38.21044921875, 47.15236927446393 ], [ 38.55102539062499, 46.9052455464292 ], [ 38.79272460937499, 46.830133640447386 ], [ 38.82568359375, 46.76996843356982 ], [ 38.91357421874999, 46.63435070293566 ], [ 39.122314453125, 46.63435070293566 ], [ 39.2431640625, 46.717268685073954 ], [ 39.298095703125, 46.79253827035982 ], [ 39.96826171875, 46.79253827035982 ], [ 40.10009765625, 46.694667307773116 ], [ 40.198974609375, 46.483264729155586 ], [ 40.440673828125, 46.34692761055676 ], [ 40.84716796875, 46.28622391806706 ], [ 40.91308593749999, 46.15700496290803 ], [ 41.187744140625, 46.05036097561633 ], [ 41.41845703125, 46.00459325574482 ], [ 42.69287109375, 46.18743678432541 ], [ 43.560791015625, 46.28622391806706 ], [ 43.912353515625, 46.49082901981415 ], [ 44.01123046875, 46.78501604269254 ], [ 44.219970703125, 47.1075227853425 ], [ 44.18701171875, 47.502358951968574 ], [ 43.0169677734375, 47.535746978239125 ], [ 42.45666503906249, 48.03034580796616 ], [ 42.32757568359375, 48.03218251603595 ], [ 42.2808837890625, 48.03034580796616 ], [ 42.21221923828125, 48.011975126709956 ], [ 42.1380615234375, 48.02116128565783 ], [ 42.01995849609375, 48.05972528178409 ], [ 42.044677734375, 48.098260411732674 ], [ 42.11883544921875, 48.13126755117026 ], [ 42.088623046875, 48.23930899024907 ], [ 42.17376708984375, 48.26308411537845 ], [ 42.12158203124999, 48.31242790407178 ], [ 42.176513671875, 48.472921272487824 ], [ 42.36328124999999, 48.494767515307295 ], [ 42.6708984375, 48.61112192003074 ], [ 42.7587890625, 48.69096039092549 ], [ 42.703857421875, 48.857487002645485 ], [ 42.637939453125, 48.93693495409401 ], [ 42.528076171875, 48.99463598353405 ], [ 42.418212890625, 49.001843917978526 ], [ 42.330322265625, 49.13859653703879 ], [ 42.1875, 49.14578361775004 ], [ 42.088623046875, 49.167338606291075 ], [ 42.0556640625, 49.27497287599639 ], [ 42.099609375, 49.44670029695474 ], [ 42.16552734375, 49.63917719651036 ], [ 42.12158203124999, 49.79544988802771 ], [ 41.912841796875, 49.908787000867136 ], [ 41.671142578125, 49.993615462541136 ], [ 41.539306640625, 50.10648772767332 ], [ 41.39648437499999, 50.17689812200107 ], [ 41.30859375, 50.05008477838256 ], [ 41.209716796875, 50.00773901463687 ], [ 41.165771484375, 49.96535590991311 ], [ 41.06689453125, 49.830896288288976 ], [ 40.97900390625, 49.76707407366792 ], [ 40.79223632812499, 49.69606181911566 ], [ 40.704345703125, 49.63206194128714 ], [ 40.572509765625, 49.57510247172322 ], [ 40.330810546875, 49.61070993807422 ], [ 40.166015625, 49.61782831211117 ], [ 40.111083984375, 49.57510247172322 ], [ 40.05615234375, 49.532339195028115 ], [ 40.078125, 49.43241258024849 ], [ 40.133056640625, 49.38237278700955 ], [ 40.18798828124999, 49.34659884833293 ], [ 40.198974609375, 49.28214015975995 ], [ 40.177001953125, 49.23194729854554 ], [ 39.935302734375, 49.13859653703879 ], [ 39.715576171875, 49.088257784724675 ], [ 39.803466796875, 48.94415123418794 ], [ 40.023193359375, 48.86471476180277 ], [ 39.8583984375, 48.84302835299516 ] ] ] } } ] }";

        return getGeoJsonFromString(context, geoJson, tolerance);
    }

    private Observable<List<Polygon>> getGeoJson(Context context, String url, double tolerance) {
        Observable<List<Polygon>> main = Observable.fromCallable(() -> getRequest(url))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> Toast.makeText(context, "GeoJson loading", Toast.LENGTH_SHORT).show())
                .observeOn(Schedulers.computation())
                .map(geoJson -> {
//                    FolderOverlay folderOverlay = new FolderOverlay();
                    List<Polygon> list = new ArrayList<>();

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

                        list.add(polygon);
                    }

                    return list;
                })
                .flatMap(Observable::fromIterable)
                .buffer(10);


        Observable<Long> interval = Observable.interval(500, TimeUnit.MILLISECONDS);

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

    private Observable<List<Polygon>> getGeoJsonFromString(Context context, String geoJsonStr, double tolerance) {
        Observable<List<Polygon>> main = Observable.fromCallable(() -> geoJsonStr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(s -> Toast.makeText(context, "GeoJson loading", Toast.LENGTH_SHORT).show())
                .observeOn(Schedulers.computation())
                .map(geoJson -> {
                    List<Polygon> list = new ArrayList<>();

                    JsonObject object = new JsonParser().parse(geoJson).getAsJsonObject();
                    JsonArray features = object.getAsJsonArray("features");

                    int count = 600;
                    double offsetX = 0;
                    double offsetY = 0;
                    double offsetChange = 1.0;

                    for (int i = 0; i < count; i++) {
                        for (JsonElement fe : features) {
                            JsonObject fo = fe.getAsJsonObject();

                            JsonArray coordinates = fo
                                    .getAsJsonObject("geometry")
                                    .getAsJsonArray("coordinates")
                                    .get(0).getAsJsonArray();

                            List<GeoPoint> geoPoints = new ArrayList<>();

                            for (JsonElement element : coordinates) {
                                JsonArray coord = element.getAsJsonArray();

                                JsonElement first = coord.get(0);
                                JsonElement second = coord.get(1);

                                if (first.isJsonPrimitive() && second.isJsonPrimitive()) {
                                    geoPoints.add(new GeoPoint(
                                            second.getAsDouble() + offsetY,
                                            first.getAsDouble() + offsetX
                                    ));
                                }
                            }

                            offsetX += offsetChange;
                            if (offsetX > 30) {
                                offsetX = 0;
                                offsetY += offsetChange;
                            }

                            Polygon polygon = new Polygon();
                            polygon.setFillColor(Color.GRAY);
                            polygon.setStrokeWidth(1f);
                            polygon.setPoints(geoPoints);

                            list.add(polygon);
                        }
                    }


                    return list;
                })
                .flatMap(Observable::fromIterable)
                .buffer(10);


        Observable<Long> interval = Observable.interval(500, TimeUnit.MILLISECONDS);

        return Observable.zip(main, interval, (overlay, aLong) -> overlay)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
