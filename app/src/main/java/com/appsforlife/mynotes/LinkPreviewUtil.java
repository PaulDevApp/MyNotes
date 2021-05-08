package com.appsforlife.mynotes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;

public class LinkPreviewUtil {

    public static Observable<Document> getJSOUPContent(String url){
        return Observable.fromCallable(() -> {
            try {
                return Jsoup.connect(url).timeout(0).get();
            }catch (IOException e){
                throw  new RuntimeException(e);
            }
        });
    }
}
