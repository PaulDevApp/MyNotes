package com.appsforlife.mynotes.util;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.appsforlife.mynotes.constants.Constants.*;

public class LinkPreviewUtil {

    private static CompositeDisposable compositeDisposable;

    private static Observable<Document> getJSOUPContent(String url) {
        return Observable.fromCallable(() -> {
            try {
                return Jsoup.connect(url).timeout(0).get();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setPreviewLink(Context context, String url, ImageView ivSiteImage,
                                      TextView tvSiteName, TextView tvSiteDescription) {
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = getJSOUPContent(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            if (result != null) {
                                Elements metaTags = result.getElementsByTag(META);
                                for (Element element : metaTags) {
                                    switch (element.attr(PROPERTY)) {
                                        case META_SITE_IMAGE:
                                            Glide.with(context).load(element.attr(CONTENT))
                                                    .into(ivSiteImage);
                                            ivSiteImage.setVisibility(View.VISIBLE);
                                            break;
                                        case META_SITE_TITLE:
                                            tvSiteName.setText(element.attr(CONTENT));
                                            if (!TextUtils.isEmpty(tvSiteName.getText())) {
                                                tvSiteName.setVisibility(View.VISIBLE);
                                                tvSiteName.setVisibility(View.VISIBLE);
                                            }
                                            break;
                                        case META_SITE_DESCRIPTION:
                                            tvSiteDescription.setText(element.attr(CONTENT));
                                            if (!TextUtils.isEmpty(tvSiteDescription.getText())) {
                                                tvSiteDescription.setVisibility(View.VISIBLE);
                                            }
                                            break;
                                    }
                                }
                            }
                        },
                        Throwable::printStackTrace);
        compositeDisposable.add(disposable);
    }

    public static void dispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
