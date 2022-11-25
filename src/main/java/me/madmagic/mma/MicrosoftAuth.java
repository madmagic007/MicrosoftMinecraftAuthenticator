package me.madmagic.mma;


import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MicrosoftAuth {

    private static final String loginUrl = "https://login.live.com/oauth20_authorize.srf?client_id=000000004C12AE6F&redirect_uri=https://login.live.com/oauth20_desktop.srf&scope=service::user.auth.xboxlive.com::MBI_SSL&display=touch&response_type=token&locale=en";
    private static final String xblTokenUrl = "https://user.auth.xboxlive.com/user/authenticate";
    private static final String xstsTokenurl = "https://xsts.auth.xboxlive.com/xsts/authorize";
    private static final String mcAuthUrl = "https://api.minecraftservices.com/authentication/login_with_xbox";

    public static void login(AuthCredentials user) throws IOException {
        Document doc = Jsoup.parse(RequestHelper.string(loginUrl));
        String ppft = doc.toString().split(",sFTTag:'<input type=\"hidden\" name=\"PPFT\"")[1].split("\"/>'")[0].split("value=\"")[1];
        String urlpost = doc.toString().split(",urlPost:'")[1].split("',")[0];//possibly this url is wrong

        RequestBody b1 = new FormBody.Builder()
                .add("login", user.login)
                .add("loginfmt", user.login)
                .add("passwd", user.password)
                .add("PPFT", ppft).build();

        String hash = RequestHelper.request(urlpost, b1).request().url().toString().split("#")[1];
        Map<String, String> queries = new HashMap<>();

        for (String pair : hash.split("&")) {
            int idx = pair.indexOf("=");
            queries.put(decode(pair.substring(0, idx)), decode(pair.substring(idx +1)));
        }

        user.accesToken = queries.get("access_token");
        RequestHelper.clearCookies();
    }

    public static void setXblToken(AuthCredentials user) throws IOException {
        JSONObject post = new JSONObject()
                .put("Properties", new JSONObject()
                        .put("AuthMethod", "RPS")
                        .put("SiteName", "user.auth.xboxlive.com")
                        .put("RpsTicket", user.accesToken)
                )
                .put("RelyingParty", "http://auth.xboxlive.com")
                .put("TokenType", "JWT");

        JSONObject resp = RequestHelper.postJSON(xblTokenUrl, post);
        user.xblToken = resp.getString("Token");
        user.userHash = resp.getJSONObject("DisplayClaims").getJSONArray("xui").getJSONObject(0).getString("uhs");
    }

    public static void setXstsToken(AuthCredentials user) throws IOException {
        JSONObject post = new JSONObject()
                .put("Properties", new JSONObject()
                        .put("SandboxId", "RETAIL")
                        .put("UserTokens", new JSONArray().put(user.xblToken))
                )
                .put("RelyingParty", "rp://api.minecraftservices.com/")
                .put("TokenType", "JWT");

        JSONObject resp = RequestHelper.postJSON(xstsTokenurl, post);
        user.xstsToken = resp.getString("Token");
    }

    public static void authenticateMC(AuthCredentials user) throws IOException {
        JSONObject post = new JSONObject()
                .put("identityToken", String.format("XBL3.0 x=%s;%s", user.userHash, user.xstsToken));

        JSONObject resp = RequestHelper.postJSON(mcAuthUrl, post);
        user.mcAccessToken = resp.getString("access_token");
    }

    private static String decode(String text) {
        return URLDecoder.decode(text, StandardCharsets.UTF_8);
    }
}
