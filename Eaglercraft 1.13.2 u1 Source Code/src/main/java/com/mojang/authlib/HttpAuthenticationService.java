package com.mojang.authlib;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public abstract class HttpAuthenticationService extends BaseAuthenticationService {
    private static final Logger LOGGER = LogManager.getLogger();

    @Nullable
    private final Proxy proxy;

    protected HttpAuthenticationService(final Proxy proxy) {
        this.proxy = proxy;
    }

    /**
     * Gets the proxy to be used with every HTTP(S) request.
     *
     * @return Proxy to be used.
     */
    public Proxy getProxy() {
        return proxy;
    }

    protected HttpURLConnection createUrlConnection(final URL url) throws IOException {
        Validate.notNull(url);
        LOGGER.debug("Opening connection to " + url);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        return connection;
    }

    /**
     * Performs a POST request to the specified URL and returns the result.
     * <p />
     * The POST data will be encoded in UTF-8 as the specified contentType. The response will be parsed as UTF-8.
     * If the server returns an error but still provides a body, the body will be returned as normal.
     * If the server returns an error without any body, a relevant {@link java.io.IOException} will be thrown.
     *
     * @param url URL to submit the POST request to
     * @param post POST data in the correct format to be submitted
     * @param contentType Content type of the POST data
     * @return Raw text response from the server
     * @throws IOException The request was not successful
     */
    public String performPostRequest(final URL url, final String post, final String contentType) throws IOException {
        Validate.notNull(url);
        Validate.notNull(post);
        Validate.notNull(contentType);
        final HttpURLConnection connection = createUrlConnection(url);
        final byte[] postAsBytes = post.getBytes(Charsets.UTF_8);

        connection.setRequestProperty("Content-Type", contentType + "; charset=utf-8");
        connection.setRequestProperty("Content-Length", "" + postAsBytes.length);
        connection.setDoOutput(true);

        LOGGER.debug("Writing POST data to " + url + ": " + post);

        OutputStream outputStream = null;
        try {
            outputStream = connection.getOutputStream();
            IOUtils.write(postAsBytes, outputStream);
        } finally {
            IOUtils.closeQuietly(outputStream);
        }

        LOGGER.debug("Reading data from " + url);

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            final String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } catch (final IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                LOGGER.debug("Reading error page from " + url);
                final String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
                LOGGER.debug("Response: " + result);
                return result;
            } else {
                LOGGER.debug("Request failed", e);
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public String performGetRequest(final URL url) throws IOException {
        return performGetRequest(url, null);
    }

    /**
     * Performs a GET request to the specified URL and returns the result.
     * <p />
     * The response will be parsed as UTF-8.
     * If the server returns an error but still provides a body, the body will be returned as normal.
     * If the server returns an error without any body, a relevant {@link java.io.IOException} will be thrown.
     *
     * @param url URL to submit the GET request to
     * @param authentication The authentication to provide, if any
     * @return Raw text response from the server
     * @throws IOException The request was not successful
     */
    public String performGetRequest(final URL url, @Nullable final String authentication) throws IOException {
        Validate.notNull(url);
        final HttpURLConnection connection = createUrlConnection(url);

        if (authentication != null) {
            connection.setRequestProperty("Authorization", authentication);
        }

        LOGGER.debug("Reading data from " + url);

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
            final String result = IOUtils.toString(inputStream, Charsets.UTF_8);
            LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
            LOGGER.debug("Response: " + result);
            return result;
        } catch (final IOException e) {
            IOUtils.closeQuietly(inputStream);
            inputStream = connection.getErrorStream();

            if (inputStream != null) {
                LOGGER.debug("Reading error page from " + url);
                final String result = IOUtils.toString(inputStream, Charsets.UTF_8);
                LOGGER.debug("Successful read, server response was " + connection.getResponseCode());
                LOGGER.debug("Response: " + result);
                return result;
            } else {
                LOGGER.debug("Request failed", e);
                throw e;
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    /**
     * Creates a {@link URL} with the specified string, throwing an {@link java.lang.Error} if the URL was malformed.
     * <p />
     * This is just a wrapper to allow URLs to be created in constants, where you know the URL is valid.
     *
     * @param url URL to construct
     * @return URL constructed
     */
    public static URL constantURL(final String url) {
        try {
            return new URL(url);
        } catch (final MalformedURLException ex) {
            throw new Error("Couldn't create constant for " + url, ex);
        }
    }

    /**
     * Turns the specified Map into an encoded & escaped query
     *
     * @param query Map to convert into a text based query
     * @return Resulting query.
     */
    public static String buildQuery(final Map<String, Object> query) {
        if (query == null) {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        for (final Map.Entry<String, Object> entry : query.entrySet()) {
            if (builder.length() > 0) {
                builder.append('&');
            }

            try {
                builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            } catch (final UnsupportedEncodingException e) {
                LOGGER.error("Unexpected exception building query", e);
            }

            if (entry.getValue() != null) {
                builder.append('=');
                try {
                    builder.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (final UnsupportedEncodingException e) {
                    LOGGER.error("Unexpected exception building query", e);
                }
            }
        }

        return builder.toString();
    }

    /**
     * Concatenates the given {@link java.net.URL} and query.
     *
     * @param url URL to base off
     * @param query Query to append to URL
     * @return URL constructed
     */
    public static URL concatenateURL(final URL url, final String query) {
        try {
            if (url.getQuery() != null && url.getQuery().length() > 0) {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "&" + query);
            } else {
                return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + query);
            }
        } catch (final MalformedURLException ex) {
            throw new IllegalArgumentException("Could not concatenate given URL with GET arguments!", ex);
        }
    }
}
