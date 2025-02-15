package com.suribada.rxjavabook.chap4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;
import com.jakewharton.rxbinding4.view.RxView;
import com.suribada.rxjavabook.R;
import com.suribada.rxjavabook.chap1.ObserverActivity;

import io.reactivex.rxjava3.core.Observable;

/**
 * Created by Noh.Jaechun on 2018. 3. 29..
 */
public class TwoButtonClickActivity extends Activity {

    private Button button1, button2, button3, button4, button5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_button_click);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        Observable.mergeArray(
                RxView.clicks(button1).take(1), // (1)
                RxView.clicks(button2).take(1),
                RxView.clicks(button3).take(1),
                RxView.clicks(button4).take(1),
                RxView.clicks(button5).take(1))
            .take(2) // (2)
            .ignoreElements() // (3)
            .subscribe(() -> {
                startActivity(new Intent(this, ObserverActivity.class));
                finish();
            });
    }
}
