package com.appsforlife.mynotes.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

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

    public static void setPreviewLink(Context context, TextView tvUrl, ImageView ivPreview,
                                      TextView tvPreviewTitle, TextView tvPreviewDescription,
                                      TextView tvPreviewUrl, FrameLayout linkPreviewDetail, boolean isClick) {
        compositeDisposable = new CompositeDisposable();
        Disposable disposable = getJSOUPContent(tvUrl.getText().toString())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            if (result != null) {
                                Elements metaTags = result.getElementsByTag("meta");
                                for (Element element : metaTags) {
                                    if (element.attr("property").equals("og:image")) {
                                        Glide.with(context).load(element.attr("content"))
                                                .into(ivPreview);
                                        ivPreview.setVisibility(View.VISIBLE);
                                    } else if (element.attr("name").equals("title")) {
                                        tvPreviewTitle.setText(element.attr("content"));
                                        tvPreviewTitle.setVisibility(View.VISIBLE);
                                    } else if (element.attr("name").equals("description")) {
                                        tvPreviewDescription.setText(element.attr("content"));
                                        tvPreviewDescription.setVisibility(View.VISIBLE);
                                        if (tvPreviewDescription.getVisibility() == View.GONE) {
                                            tvPreviewDescription.setMaxLines(3);
                                        }
                                    } else if (element.attr("property").equals("og:url")) {
                                        tvPreviewUrl.setText(element.attr("content"));
                                        tvPreviewUrl.setVisibility(View.VISIBLE);
                                    }
                                    startViewAnimation(linkPreviewDetail, context, R.anim.appearance);
                                    linkPreviewDetail.setVisibility(View.VISIBLE);

                                    if (isClick) {
                                        linkPreviewDetail.setOnClickListener(v -> {
                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                            intent.setData(Uri.parse(tvUrl.getText().toString()));
                                            context.startActivity(intent);
                                        });
                                    }
                                }
                                if (tvPreviewDescription.getVisibility() == View.GONE
                                        && tvPreviewTitle.getVisibility() == View.GONE
                                        && tvPreviewUrl.getVisibility() == View.GONE) {
                                    linkPreviewDetail.setVisibility(View.GONE);
                                }
                            } else {
                                linkPreviewDetail.setVisibility(View.GONE);
                            }
                        },
                        throwable -> linkPreviewDetail.setVisibility(View.GONE));
        compositeDisposable.add(disposable);
    }

    public static void dispose() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
    }
}
