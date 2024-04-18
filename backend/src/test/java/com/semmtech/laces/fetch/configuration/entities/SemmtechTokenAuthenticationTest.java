package com.semmtech.laces.fetch.configuration.entities;

import org.junit.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class SemmtechTokenAuthenticationTest {

    @Test
    public void authenticationTokenGenerated() {
        var authenticationMethod =
                new SemmtechTokenAuthenticationEntity(
                        "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALBvPXci68G6RXwEWd4JpUEE5mmdg8OuDDovLOUuRHqQvo82bgXGLnjax3I496Xd5/Fqd4O5YFsRh8QGXFREN4aWtfcang2hW5+ANs33ervlApZcS2PQHvu95wMJn6sNBjvZz4O0fGeM3yQJC9o9FIsXgsR1tTGx6w4qwPowJoSBAgMBAAECgYA3iIv8N82lCJBeXLA8pySQto4fqCiVKu9GURr8d/et7GlOgn9W6e4utA4a8bthRdt/rVc46txdTcNB/A6Lp30iJTETy+w1NTzvNm3rKijd1KWv0ATGUjJxdgglF1okdezPUGtlu7UGfJSuQC7/rbUAEv3rMACdBfDNeW3PDw/FwQJBAOzz2IIbFCrO70rF+OZ+I6/AppjPaBD0qwhxEYZ8v39Ol3kHAMHu9ndeTWYOCxdcOsPaHk/oLraCJy+GmCL/HNUCQQC+ngSe9wWOv3c6fXiVbeBEs7Vp+UxtMrV5hhZzm7McUij0YfnACHmm6bH9nvJI24l2x7KIvQOx6wTv3n2w1e79AkBBm5rdG2ZQHBABoiMynsZ+yVbXhMYJu9UHd1ck57GWLEqghiHdkK7JimDf2w+THkRfKiuucFlOy2bSL/A78GspAkEArckXYW7JDFoXc6YNF/9y5nAJR7LYMP54YXEUaxZa98kYOaZRsviHINwTQK13K2GsNDTg2rWo9r7UY3Svl1eUaQJBAK7vq+7Xc87SUxv5AqBx0UhcV7QM6y1ZMe9sSqyXpsuLC5S/AHDcs/0KD6gS2AAGzQ5HKrf7lCh9BbzAD7GUpX8=",
                        "123456");

        var headers = authenticationMethod.headers("configuration");

        String authorization = headers.get(HttpHeaders.AUTHORIZATION).get(0);
        assertThat(authorization, containsString("semmtech-app-token"));
        assertThat(authorization, containsString(" appId=\"123456\""));
        assertThat(authorization, containsString("signature=\""));
    }

}
