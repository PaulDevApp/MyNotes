package com.appsforlife.mynotes.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appsforlife.mynotes.R;
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

import static com.appsforlife.mynotes.Support.startViewAnimation;

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

    public static void setPreviewLink(Context context, String url, ImageView ivPreview,
                                      TextView tvPreviewTitle, TextView tvPreviewDescription) {
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = getJSOUPContent(url)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            if (result != null) {
                                Elements metaTags = result.getElementsByTag("meta");
                                for (Element element : metaTags) {
                                    if (element.attr("property").equals("og:image")) {
                                        Glide.with(context).load(element.attr("content"))
                                                .into(ivPreview);
                                        startViewAnimation(ivPreview, context, R.anim.appearance);
                                        ivPreview.setVisibility(View.VISIBLE);
                                    } else if (element.attr("name").equals("title")) {
                                        tvPreviewTitle.setText(element.attr("content"));
                                        startViewAnimation(tvPreviewTitle, context, R.anim.appearance);
                                        tvPreviewTitle.setVisibility(View.VISIBLE);
                                    } else if (element.attr("name").equals("description")) {
                                        if (tvPreviewTitle.getVisibility() == View.GONE) {
                                            tvPreviewDescription.setMaxLines(3);
                                        }
                                        tvPreviewDescription.setText(element.attr("content"));
                                        startViewAnimation(tvPreviewDescription, context, R.anim.appearance);
                                        tvPreviewDescription.setVisibility(View.VISIBLE);
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
