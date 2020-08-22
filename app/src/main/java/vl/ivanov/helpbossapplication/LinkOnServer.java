package vl.ivanov.helpbossapplication;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LinkOnServer {

    /**
     * Метод для открытия http-соединения
     * @return urlConnect - url соединение
     * @throws MalformedURLException
     */
    public HttpsURLConnection openHttpConnect() throws MalformedURLException {
        URL httpBinEndPoint = new URL("https://helpboss.ru/index.php");
        HttpsURLConnection urlConnect = null;
        try {
            urlConnect = (HttpsURLConnection) httpBinEndPoint.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlConnect;
    }
}
