package com.suribada.rxjavabook.chap8;

import androidx.core.util.Pair;

import com.suribada.rxjavabook.SystemClock;
import com.suribada.rxjavabook.api.BookSampleRepository;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ZipTest {

    @Test
    public void zip() {
        Observable.zip(Observable.interval(0, 5, TimeUnit.SECONDS) // (1)
                .doOnDispose(() -> System.out.println("disposed")), // (2)
                Observable.just("A", "B", "C"), // (3)
                (x, y) -> (x + y)) // (4)
                .subscribe(System.out::println, System.err::println, () -> System.out.println("onComplete"));
        SystemClock.sleep(30000);
    }

    @Test
    public void zip2() {
        Observable.zip(Observable.interval(0, 5, TimeUnit.SECONDS) // (1)
                        .doOnDispose(() -> System.out.println("disposed")), // (2)
                Observable.interval(6, TimeUnit.SECONDS).take(3), // (3)
                (x, y) -> "first=" + x + ", second=" + y) // (4)
                .subscribe(System.out::println, System.err::println, () -> System.out.println("onComplete"));
        SystemClock.sleep(30000);
    }

    /**
     * onComplete 이벤트를 보내지 않는 경우
     */
    @Test
    public void zip_abnormal() {
        Observable.zip(Observable.interval(0, 5, TimeUnit.SECONDS)
                .doOnDispose(() -> System.out.println("disposed")),
                Observable.<String>create(emitter -> { // (1) 끝
                    emitter.onNext("A");
                    emitter.onNext("B");
                    emitter.onNext("C");
                }), // (1) 끝
                (x, y) -> (x + y))
                .subscribe(System.out::println, System.err::println, () -> System.out.println("onComplete"));
        SystemClock.sleep(30000);
    }

    /**
     * 여기서 disposed가 출력 안 되는 이유는 메인 스레드에서 1~4까지 이미 소비했기 때문이다.
     */
    @Test
    public void zip3() {
        Observable.zip(Observable.range(1, 4)
                        .doOnNext(System.out::println)
                        .doOnDispose(() -> System.out.println("disposed"))
                , Observable.just("A", "B", "C"), (x, y) -> (x + y))
                .subscribe(System.out::println, System.err::println, () -> System.out.println("onComplete"));
    }

    @Test
    public void zip4() {
        Observable.zip(Observable.range(1, 3)
                        .doOnNext(System.out::println)
                        .doOnDispose(() -> System.out.println("disposed"))
                , Observable.just("A", "B", "C", "D").doOnDispose(() -> System.out.println("disposed2") ),
                (x, y) -> (x + y))
                .subscribe(System.out::println, System.err::println, () -> System.out.println("onComplete"));
    }

    /**
     * 두 Observable 간에 시간차가 있음
     */
    @Test
    public void zip_interval_problem() {
        Observable.zip(Observable.interval(10, TimeUnit.SECONDS).map(x -> x * 10000), // (1)
                Observable.interval(11, TimeUnit.SECONDS), // (2)
                (x, y) -> x + y) // (3)
                .subscribe(System.out::println);
                //.subscribe(v -> System.out.println(System.currentTimeMillis() + ":" + v));
        SystemClock.sleep(250000);
    }

    @Test
    public void zip_much_differnce() {
        Observable.zip(Observable.interval(1, TimeUnit.MINUTES),
                Observable.interval(1, TimeUnit.NANOSECONDS), (x, y) -> x + y)
                .subscribe(System.out::println);
        SystemClock.sleep(1000 * 60 * 10);
    }

    @Test
    public void zipWithIterable() {
        Observable.just(1, 2, 3, 4).zipWith(Arrays.asList("A", "B", "C"), (x, y) -> x + y)
                .subscribe(System.out::println);
    }

    @Test
    public void zipWithIterable2() {
        Observable.just(1, 2, 3, 4).zipWith(Observable.fromIterable(Arrays.asList("A", "B", "C")), (x, y) -> x + y)
                .subscribe(System.out::println);
    }

    @Test
    public void zip_pi() {
        Observable.zip(Observable.interval(0, 5, TimeUnit.SECONDS), // (1)
                Observable.just("3.", "1", "4", "1", "5", "9", "2").scan((x, y) -> x + y), // (2)
                (interval, pi) -> pi) // (3)
                .subscribe(System.out::println);
        SystemClock.sleep(200000);
    }

    /**
     * delay 연산자와 헷갈릴 수 있음. 동작 다르다.
     */
    @Test
    public void zip_pi_not_equivalent() {
        Observable.just("3.", "1", "4", "1", "5", "9", "2").scan((x, y) -> x + y)
                .delay(v -> {
                    if (v.length() < 2) {
                        return Observable.empty();
                    }
                    return Observable.timer(5, TimeUnit.SECONDS);
                })
                .subscribe(System.out::println);
            SystemClock.sleep(200000);
    }

    @Test
    public void zip_each() {
        BookSampleRepository repository = new BookSampleRepository();
        int regionCode = 111;
        Observable.zip(repository.getWeatherObservable(regionCode).subscribeOn(Schedulers.io()), // (1)
                repository.getWeatherDetailObservable(regionCode).subscribeOn(Schedulers.io()), // (2)
                Pair::new) // (3)
                .subscribe(pair -> System.out.println(pair.first + ", " + pair.second),
                        System.err::println);
        SystemClock.sleep(5000);
    }
}
