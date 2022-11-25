package me.madmagic.mma;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RequestHelper {

    private static final String usag = "Mozilla/5.0 (XboxReplay; XboxLiveAuth/3.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36";
    private static OkHttpClient c = new OkHttpClient.Builder()
            .cookieJar(new MyCookieJar())
            .build();

    public static String string(String url) throws IOException {
        return request(url, null).body().string();
    }

    public static JSONObject postJSON(String url, JSONObject post) throws IOException {
        return new JSONObject(request(url, RequestBody.create(post.toString(), MediaType.parse("application/json"))).body().string());
    }

    public static Response request(String url, RequestBody rb) throws IOException {
        Request.Builder r = new Request.Builder()
                .header("User-Agent", usag)
                .url(url);

        if (rb != null) r.post(rb);
        return c.newCall(r.build()).execute();
    }

    public static void clearCookies() {
        ((MyCookieJar) c.cookieJar()).list = new ArrayList<>();
    }

    public static class MyCookieJar implements CookieJar {

        public List<Cookie> list = new ArrayList<>();

        @Override
        public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
            this.list = list;
        }

        @NotNull
        @Override
        public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
            return list;
        }
    }
}
