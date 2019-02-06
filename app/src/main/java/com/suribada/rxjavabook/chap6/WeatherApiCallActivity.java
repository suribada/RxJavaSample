package com.suribada.rxjavabook.chap6;

import android.app.Activity;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.suribada.rxjavabook.R;
import com.suribada.rxjavabook.api.BookSampleRepository;
import com.suribada.rxjavabook.api.model.Weather;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Noh.Jaechun on 2018-04-08.
 */
public class WeatherApiCallActivity extends Activity {

    private BookSampleRepository repository = new BookSampleRepository();

    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_buttons);
        title = findViewById(R.id.title);
        Stetho.initializeWithDefaults(this);
    }

    public void onClickButton1(View view) {
        Location location = getSampleLocation();
        repository.getRegion(location)
                .map(region -> region.areaCode)
                .flatMap(repository::getWeather)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(weather -> title.setText(weather.toString()));
    }

    public Single<Weather> getWeather(Location location) {
        return repository.getRegion(location)
                .map(region -> region.areaCode)
                .flatMap(repository::getWeather);
    }

    public void onClickButton2(View view) {
        Location location = getSampleLocation();
        repository.getRegion(location) // (1)
                .subscribeOn(Schedulers.io()) // (2)
                .map(region -> region.areaCode)
                .flatMap(areaCode -> repository.getWeather(areaCode) // (3)
                        .subscribeOn(Schedulers.io())) // (4)
                .observeOn(AndroidSchedulers.mainThread()) // (5)
                .subscribe(weather -> title.setText(weather.toString()),
                        e -> {
                            // 로그나 Toast
                        });
    }

    @NonNull
    private Location getSampleLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(37.5088652);
        location.setLongitude(127.0609603);
        return location;
    }

    public void onClickButton3(View view) {
        int regionCode = 111;
        Single.zip(repository.getWeather(regionCode),
                    repository.getWeatherDetail(regionCode),
                    Pair::new)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> title.setText(pair.first + ", " + pair.second));
    }

    public void onClickButton4(View view) {
        int regionCode = 111;
        Single.zip(repository.getWeather(regionCode).subscribeOn(Schedulers.io()),
                    repository.getWeatherDetail(regionCode).subscribeOn(Schedulers.io()),
                    Pair::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> title.setText(pair.first + ", " + pair.second));
    }

}