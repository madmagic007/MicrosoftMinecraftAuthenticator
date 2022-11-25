package me.madmagic.mma;

import java.io.IOException;

public class UserAuth {

    public final String login;
    public final String password;
    public String accesToken;
    public String xblToken;
    public String userHash;
    public String xstsToken;
    public String mcAccessToken;

    public UserAuth(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public void doAuth() throws IOException {
        MicrosoftAuth.login(this);
        MicrosoftAuth.setXblToken(this);
        MicrosoftAuth.setXstsToken(this);
        MicrosoftAuth.authenticateMC(this);
    }
}
