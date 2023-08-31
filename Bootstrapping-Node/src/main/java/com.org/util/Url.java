package com.org.util;

public class Url {

    static public String getUrl(String hostname, int port, String endPoint) {
        return "http://localhost:" + port + "/" + hostname + "/" + endPoint;
    }
}
