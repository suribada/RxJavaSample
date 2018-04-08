package com.suribada.rxjavabook.chap6;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.facebook.stetho.Stetho;
import com.suribada.rxjavabook.R;
import com.suribada.rxjavabook.api.BookSampleRepository;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by lia on 2018-04-09.
 */
public class BookApiCallActivity extends Activity {

    private BookSampleRepository repository = new BookSampleRepository();

    private TextView title;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.four_buttons);
        title = (TextView) findViewById(R.id.title);
        Stetho.initializeWithDefaults(this);
    }

    public void onClickButton1(View view) {
        Single.merge(repository.getBestSeller(),
                    repository.getRecommendBooks(),
                    repository.getCategoryBooks(7))
                .subscribeOn(Schedulers.io())
                .collect(ArrayList::new, ArrayList::addAll)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(books -> title.setText(books.toString()));
    }

    public void onClickButton2(View view) {
        Single.merge(repository.getBestSeller().subscribeOn(Schedulers.io()),
                    repository.getRecommendBooks().subscribeOn(Schedulers.io()),
                    repository.getCategoryBooks(7).subscribeOn(Schedulers.io()))
                .collect(ArrayList::new, ArrayList::addAll)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(books -> title.setText(books.toString()));
    }

    public void onClickButton3(View view) {
        repository.getBookCategories().toObservable()
                .flatMapIterable(categories -> categories)
                .flatMapSingle(category -> repository.getCategoryBooks(category.categoryId))
                .subscribeOn(Schedulers.io())
                .collect(ArrayList::new, ArrayList::addAll)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(books -> title.setText(books.toString()));
    }

    public void onClickButton4(View view) {
        repository.getBookCategories().toObservable()
                .subscribeOn(Schedulers.io())
                .flatMapIterable(categories -> categories)
                .flatMapSingle(category ->
                        repository.getCategoryBooks(category.categoryId).subscribeOn(Schedulers.io()))
                .collect(ArrayList::new, ArrayList::addAll)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(books -> title.setText(books.toString()));
    }

}
