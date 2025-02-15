package com.suribada.rxjavabook.chap7;

import com.suribada.rxjavabook.SystemClock;
import com.suribada.rxjavabook.api.model.Weather;

import org.junit.Test;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AmbTest {

    @Test
    public void amb() {
        Observable<Weather> kmaObs = Observable.interval(100, TimeUnit.MILLISECONDS, Schedulers.io())
                .flatMap(ignored -> getWeatherFromKma())
                .doOnNext(next -> System.out.println("kmaObs next=" + next))
                .doOnDispose(() -> System.out.println("kmaObs disposed"));

        Observable<Weather> kweatherObs = Observable.interval(100, TimeUnit.MILLISECONDS, Schedulers.io())
                .flatMap(ignored -> getWeatherFromKweather())
                .doOnNext(next -> System.out.println("kweather next=" + next))
                .doOnDispose(() -> System.out.println("kweatherObs disposed"));

        Observable.ambArray(kmaObs, kweatherObs)
                .subscribe(System.out::println);
        SystemClock.sleep(5000);
    }

    @Test
    public void amb_bad() {
        Observable.interval(100, TimeUnit.MILLISECONDS, Schedulers.io())
                .flatMap(ignored -> Observable.ambArray(getWeatherFromKma().subscribeOn(Schedulers.io()),
                        getWeatherFromKweather().subscribeOn(Schedulers.io())))
                .subscribe(System.out::println);
        SystemClock.sleep(5000);
    }

    private Observable<Weather> getWeatherFromKma() {
        return Observable.create(emitter -> {
            ThreadLocalRandom.current().nextInt(100, 500);
            Weather weather = new Weather();
            weather.setCurrentTemperature(10.0f);
            emitter.onNext(weather);
        });
    }

    private Observable<Weather> getWeatherFromKweather() {
        return Observable.create(emitter -> {
            ThreadLocalRandom.current().nextInt(100, 500);
            Weather weather = new Weather();
            weather.setCurrentTemperature(20.0f);
            emitter.onNext(weather);
        });
    }
}
