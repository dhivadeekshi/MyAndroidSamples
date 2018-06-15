package com.dhivakar.mysamples.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Dictionary;
import java.util.HashMap;

public class HTTPRequestHandler {

    public interface Callback {
        void OnRequestSuccess(String response);
        void OnRequestFailed(String response, int responseCode);
    }

    private static URL createRequestURL(String url) throws MalformedURLException {
        return new URL(url);
    }

    private static HttpURLConnection createConnection(URL url, String requestMethod) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection != null)
            connection.setRequestMethod(requestMethod);
        return connection;
    }

    private static void addRequestProperties(HashMap<String, String> properties, HttpURLConnection connection) {
        if (properties != null) {
            for (String key : properties.keySet()) {
                connection.addRequestProperty(key, properties.get(key));
                LogUtils.i("HTTPRequestHandler","Add Property :"+key+"-"+properties.get(key));
            }
        }
    }

    private static String appendParamsToUrl(HashMap<String, String> params, String url) {
        if (params != null) {
            StringBuilder newUrl = new StringBuilder();
            newUrl.append(url).append('?');
            for (String key : params.keySet()) {
                newUrl.append(key).append('=').append(params.get(key)).append('&');
                LogUtils.i("HTTPRequestHandler","Add Property :"+key+"-"+params.get(key));
            }
            return newUrl.toString();
        }
        return url;
    }

    private static void HandleResponse(HttpURLConnection connection, Callback OnRequestCompleted) throws IOException {

        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        StringBuilder responseError = new StringBuilder();
        String inputLine;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        }catch (IOException e) {
            try {

                BufferedReader error = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                while ((inputLine = error.readLine()) != null) {
                    responseError.append(inputLine);
                }
                error.close();
            } catch (Exception _e) {
                responseError.append("Not Available exception:").append(_e.getMessage());
            }
            if (responseError.length() <= 0)
                responseError.append(connection.getResponseMessage());
        }

        switch (responseCode) {
            case HttpURLConnection.HTTP_OK: // SUCCESS
                OnRequestCompleted.OnRequestSuccess(response.toString());
                break;
            default:
                OnRequestCompleted.OnRequestFailed(response.append("|").append(responseError).toString(), responseCode);
                break;
        }
    }

    public static void GetRequest(final String url,final HashMap<String , String> properties, final HashMap<String , String> params, final Callback onRequestCompleted) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpURLConnection connection = createConnection(createRequestURL(appendParamsToUrl(params, url)), "GET");

                    if (connection != null) {
                        addRequestProperties(properties, connection);
                        connection.connect();
                        HandleResponse(connection, onRequestCompleted);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onRequestCompleted.OnRequestFailed("Not Available", HttpURLConnection.HTTP_BAD_REQUEST);
                }

                return null;
            }
        };
    }

    public static void PostRequest(final String url, final HashMap<String, String> properties, final HashMap<String, String> params,final Callback onRequestCompleted) {

        LogUtils.i("HTTPRequestHandler", "PostRequest for URL : "+url);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    HttpURLConnection connection = createConnection(createRequestURL(appendParamsToUrl(params,url)), "POST");

                    if (connection != null) {
                        addRequestProperties(properties, connection);
                        connection.setDoOutput(true);
                        OutputStream os = connection.getOutputStream();
                        os.flush();
                        os.close();

                        connection.connect();
                        HandleResponse(connection, onRequestCompleted);
                    }
                } catch (IOException|SecurityException e) {
                    e.printStackTrace();
                    onRequestCompleted.OnRequestFailed("Not Available", HttpURLConnection.HTTP_BAD_REQUEST);
                }

                return null;
            }
        }.execute(null, null, null);
    }

    public static void OpenWebRequest()
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... voids) {

                return null;
            }
        }.execute(null, null, null);
    }
}
