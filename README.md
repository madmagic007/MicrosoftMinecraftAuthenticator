# Microsoft Minecraft Authenticator

A hacky but extremely simple way to log in to minecraft through microsoft fully automatically

---

<p align="center">
   <a href="https://discordapp.com/users/401795293797941290/">
       <img src="https://img.shields.io/badge/Discord-%232C2F33.svg?logo=discord" alt="Discord">
   </a>
</p>

This project gets the Minecraft access token from the associated Microsoft accounts email and password, fully automatically without any popup or OAuth2 app requirement.
This access token can be used for various of purposes, but mainly for creating (afk/console) clients to connect to server.

---

## Usage

```java
//Provide your Microsoft email/password here
AuthCredentials creds = new AuthCredentials("email", "password");
creds.doAuth(); //Does the various authorization steps

System.out.prinln(creds.mcAccessToken); //This is the almighty access token
```

---

## Token lifeTimes

This process goes through various of tokens, each having their own lifetime. Refer to <a href="https://wiki.vg/Microsoft_Authentication_Scheme" target="_blank">the wiki</a> for details of the duration of each token. Your implementation must handle refreshing of the various tokens. The `MicrosoftAuth` class has multiple public functions that can be used to refresh the required tokens.