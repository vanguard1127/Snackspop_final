package com.snackspop.snackspopnew.Utils;


import android.util.Base64;

import com.snackspop.snackspopnew.Model.NameValuePairs;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


/**
 * Created by suraj on 16/9/15.
 */
public class MultipartUtility {
    private String url;
    private HttpURLConnection con;
    private OutputStream os;

    private String delimiter = "--";
    private String boundary = "SwA" + Long.toString(System.currentTimeMillis()) + "SwA";

    /**
     * This constructor initializes a new HTTP POST request with url parameter
     *
     * @param requestURL
     * @throws IOException
     */
//    public MultipartUtility(String requestURL, String charset)
    public MultipartUtility(String requestURL, List<NameValuePairs> headerNameValuePairs)
            throws IOException {
        url = requestURL;
        con = (HttpURLConnection) (new URL(url)).openConnection();
        con.setRequestMethod("POST");
        con.setDoInput(true);
        con.setDoOutput(true);

        con.setRequestProperty("Connection", "Keep-Alive");
        String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
        con.setRequestProperty("Authorization", "Basic " + encoded);
        con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        for (int i = 0; i < headerNameValuePairs.size(); i++) {
            con.addRequestProperty(headerNameValuePairs.get(i).getName(), headerNameValuePairs.get(i).getValue());
        }
        con.connect();
        os = con.getOutputStream();

    }

//    public MultipartUtility(String requestURL, List<NameValuePairs> headerNameValuePairs, boolean isPost)
//            throws IOException {
//        url = requestURL;
//        con = (HttpURLConnection) (new URL(url)).openConnection();
//        if (isPost) {
//            con.setRequestMethod("POST");
//            con.setDoInput(true);
//            con.setDoOutput(true);
//            con.setRequestProperty("Connection", "Keep-Alive");
//
//        }
//        else
//            con.setRequestMethod("GET");
//
//
//
//        String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
//        con.setRequestProperty("Authorization", "Basic " + encoded);
//
//        con.connect();
//        os = con.getOutputStream();
//
//    }

    /**
     * Method add for String
     * cm9vdDpyb290
     *
     * @param paramName
     * @param value
     * @throws Exception
     */
    public void addFormPart(String paramName, String value) throws Exception {
        //used
        writeParamData(paramName, value);
    }


    private void writeParamData(String paramName, String value) throws Exception {


        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write("Content-Type: text/plain\r\n".getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"\r\n").getBytes());
        os.write(("\r\n" + value + "\r\n").getBytes());


    }


    /**
     * Method add for File part
     *
     * @param paramName
     * @param fileName
     * @param data
     * @throws Exception
     */
    public void addFilePart(String paramName, String fileName, byte[] data) throws Exception {
        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"driver_" + fileName + "\"\r\n").getBytes());
//        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Type: "
                + URLConnection.guessContentTypeFromName((fileName)) + "\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());
        os.write(data);
        os.write("\r\n".getBytes());
    }

    /**
     * Method add for File part
     *
     * @param paramName
     * @param file
     * @throws Exception
     */
    public void addFilePart(String paramName, String file) throws Exception {
        File uploadFile = new File(file);
        os.write((delimiter + boundary + "\r\n").getBytes());
        os.write(("Content-Disposition: form-data; name=\"" + paramName + "\"; filename=\"" + uploadFile.getName() + "\"\r\n").getBytes());
//        os.write(("Content-Type: application/octet-stream\r\n").getBytes());
        os.write(("Content-Type: "
                + URLConnection.guessContentTypeFromName(uploadFile.getName()) + "\r\n").getBytes());
        os.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
        os.write("\r\n".getBytes());


        FileInputStream inputStream = new FileInputStream(uploadFile);
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
//        os.write(data);
        os.write("\r\n".getBytes());
    }


    public void finishMultipart() throws Exception {
        os.write((delimiter + boundary + delimiter + "\r\n").getBytes());
    }


    public String getResponse() throws Exception {


        //----------old------------------
//        InputStream is = con.getInputStream();
//        byte[] b1 = new byte[10240*50];
//        StringBuffer buffer = new StringBuffer();
//        while (is.read(b1) != -1)
//            buffer.append(new String(b1));

        //------------------------------------

        // change for oom----------

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        //--------------------

        con.disconnect();
//        return buffer.toString();
        return response.toString();
    }
}