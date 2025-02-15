package com.suribada.rxjavabook.chap3;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding4.view.RxView;
import com.suribada.rxjavabook.R;

import java.util.Random;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Created by lia on 2018-03-21.
 */
public class ContractErrorActivity extends Activity {

    private TextView title;
    private Button button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_and_button);
        title = findViewById(R.id.title);
        button = findViewById(R.id.button);
        RxView.clicks(button)
                .flatMap(ignored -> getBestSeller()
                        .subscribeOn(Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bookTitle -> title.setText(bookTitle),
                        e -> Toast.makeText(this, "문제 발생", Toast.LENGTH_LONG).show()
                );
    }

    private Observable<String> getBestSeller() {
        Random random = new Random();
        int val = random.nextInt(5);
        return Observable.fromCallable(() -> "title " + 10 / val);
    }

}
