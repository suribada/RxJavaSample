package com.suribada.rxjavabook.chap7;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.jakewharton.rxbinding4.widget.RxCompoundButton;
import com.suribada.rxjavabook.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;

/**
 * from Learnig RxJava
 *
 * Created by lia on 2018-01-04.
 */
public class StopWatchActivity extends Activity {

    TextView time;

    ToggleButton start;

    Button init;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stop_watch);
        time = findViewById(R.id.time);
        start = findViewById(R.id.start);
        init = findViewById(R.id.init);
        time.setText(String.valueOf(0));
        RxCompoundButton.checkedChanges(start).switchMap(checked -> {
                if (checked) {
                    return Observable.interval(100, TimeUnit.MILLISECONDS);
                } else {
                    //return Observable.empty();
                    return Observable.just(0);
                }
            }).observeOn(AndroidSchedulers.mainThread())
            .subscribe(value -> {
                time.setText(String.valueOf(value));
            });
    }

}
