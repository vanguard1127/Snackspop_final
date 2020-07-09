package com.snackspop.snackspopnew.Utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.maps.model.LatLng;
import com.snackspop.snackspopnew.Model.NameValuePairs;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.TELEPHONY_SERVICE;

/**
 * Created by suraj on 16/3/17.
 */

public class AppUtils {

    public static boolean isDebug = true;


    private static final String EMPTY = "";
    private static final String NULL = "null";

    //assync
    public static final String PROGRESS_DEFAULT_MESSAGE = "Please Wait...";
    private static final String USER_AGENT = "Mozilla/5.0";
    // Asynctask Different Scenarios
    public static final int Async_SUCCESS = 0x1;
    public static final int Async_FAILED_SERVER_UNREACHABLE = 0x11;
    public static final int Async_FAILED_WITH_DESC = 0x12;

    public static final String BASE_SITE_URL = "http://13.58.163.46:3030";//"http://13.59.125.89:3000";
    //public static final String BASE_SITE_URL = "http://192.168.1.102:3030";//"http://13.59.125.89:3000";
    public static final String BASE_URL = BASE_SITE_URL + "/snackspop/api/";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String FOLDER_NAME = "snacks Pop";


    public static class PREFS {
        public static final String USER_TOKEN = "token";
        public static final String USER_ID = "_id";
        public static final String USER_Lat = "user_lat";
        public static final String USER_Lng = "user_lng";
        public static final String USER_OBJECT_STRING = "user_obj";
        public static final String IS_LOGED_IN = "is_loged_in";
        public static final String FIRST_TIME = "not_first_time";
        public static final String IS_SOCIAL = "is_social";
        public static final String RECENT_LOC = "recent_loc";
    }

    public static class AFTER_BASE_URL {
        public static final String LOGIN = "users/signin";
        public static final String SIGIN = "users/signup";
        public static final String SOCIAL_SIGNIN = "users/social_signin";
        public static final String SEND_VERIFICATION = "users/forgotpass";
        public static final String VERIFY = "users/verify";
        public static final String UPDATEPASS = "users/updatepass";
        public static final String UPDATE = "users/update";

        public static final String CREATE_NEW_ITEM = "items/add_new_item";
        public static final String UPDATE_ITEM = "items/update_item";
        public static final String GET_ITEM_LIST = "items/get_items/";
        public static final String GET_MY_ITEM_LIST = "items/get_my_items/";
        public static final String GET_ITEM_LIST_WITH_CHAT = "chats/get_items_with_chat/";
        public static final String GET_CHAT_LIST_WITH_ITEM = "chats/get_chats_with_item/";
        public static final String UPLOAD_FILE = "files/upload/";
        public static final String GET_ITEM = "items/get_item/";
        public static final String DEL_ITEM = "items/del_item/";
        public static final String GET_CHAT_MESSAGE_LIST = "chats/get_messages/";
        public static final String IMAGE_URL = "files/";

        public static final String GET_ORDERS_LIST = "seller/orders/";
        public static final String GET_ORDERS_DETAIL = "orders/detail/";
        public static final String CANCEL_ORDER = "seller/orders/";
        public static final String GET_TIMIMGS = "sellers/timings/";
        public static final String UPDATE_TIMIMGS = "sellers/timings/";
    }

    public static class ASYNC_CLASSNAME {
        public static final String LOGIN = "Login";
        public static final String SIGNUP = "signin";
        public static final String SOCIALSIGNIN = "social_signin";
        public static final String UPLOAD_FILE = "upload_file";
        public static final String SEND_VERIFICATION = "send_verification";
        public static final String VERIFY = "verify";
        public static final String UPDATE = "update";


        public static final String GET_ITEM_LIST = "get_items";
        public static final String GET_MY_ITEM_LIST = "get_my_items";
        public static final String CREATE_NEW_ITEM = "create_items";
        public static final String REMOVE_ITEM = "remove_item";
        public static final String GET_CHAT_MESSAGES = "get_chat_messages";
        public static final String GET_ITEM_LIST_WITH_CHAT = "get_items_chat";
        public static final String GET_CHAT_LIST_WITH_ITEM = "get_chats_item";

        public static final String GET_ORDERS_LIST = "get_orders_list";
        public static final String GET_ORDERS_DETAIL = "get_orders_detail";
        public static final String CANCEL_ORDER = "cancel_order";
        public static final String GET_TIMIMGS = "seller/timings/";
        public static final String UPDATE_TIMIMGS = "seller/timings/";

        public static final String UPDATE_LOCATION = "user/update_location";
    }

    public static class EXTRA {

        public static final String EMAIL = "email";
        public static final String TOKEN = "token";
        public static final String CHANGE_PARAMTER = "change_parameter";
        public static final String MY_ITEM = "my_item";
        public static final String OTHER_ITEM = "other_item";
        public static final String IS_FROM_NOTIFICATION = "is_from_notification";
        public static final String NOTIFICATION_TYPE = "notification_type";
        public static final String NOTIFICATION_TITLE = "notification_title";
        public static final String NOTIFICATION_MESSAGE = "notification_message";
        public static final String NOTIFICATION_ORDER_ID = "notification_order_id";


        public static final String LOC_LAT = "loc_lat";
        public static final String LOC_LNG = "loc_lng";
        public static final String FILTER_IS_CLEARED = "filter_is_cleared";
        public static final String MIN = "min";
        public static final String MAX = "max";
        public static final String ORDER_OBJECT = "order_object";
        public static final String SHIPPING_ADDRESS = "shipping_address";
        public static final String MIN_VALUE_SET = "min_set";
        public static final String MAX_VALUE_SET = "max_set";
        public static final String INCLUDE_CLOSED = "include_closed";
        public static final String CART_ITEM_LIST = "item_cart_list";
        public static final String CART_COUNTER = "cart_counter";
        public static final String MY_ITEM_FOR_CART = "my_item_for_cart";

        public static final String chatting_user_id = "userid";
        public static final String chatting_user_image = "userimage";
        public static final String chatting_user_name = "username";
        public static final String chatting_item_id = "itemid";
        public static final String chatting_activity_flag = "chatting_flag";
    }

    public static boolean chatting_flag = false;
    public static int     chatting_userid = -1;
    public static int     chatting_itemid = -1;
    public static String  chatting_username = "";
    public static String  chatting_userimageurl="";

    public static class BROADCAST {
        public static final String BR_CLOSE_ACTIVITY_LOGIN = "com.snackspop.snackspopnew.close_activity";
        public static final String BR_UPDATE_MY_ITEMS = "com.snackspop.snackspopnew.update_my_items";
        public static final String BR_UPDATE_USER_DATA = "com.snackspop.snackspopnew.update_user_data";
        public static final String BR_UPDATE_ORDER_DATA = "com.snackspop.snackspopnew.update_order";
        public static final String BR_UPDATE_LOCATION = "com.snackspop.snackspopnew.locationUpdate";
        public static final String BR_CLOSE_LOCATION_SERVICE = "com.snackspop.snackspopnew.CLOSE_locationUpdate";

        public static final String BR_UPDATE_CART = "com.sanckspop.snackspopnew.update_cart";
        public static final String BR_UPDATE_CART_List = "com.sanckspop.snackspopnew.update_cart_list";
        public static final String BR_LOGIN = "com.sanckspop.snackspopnew.login";
        public static final String BR_ORDER_CREATED = "com.sanckspop.snackspopnew.order_created";

        public static final String BR_NEW_MESSAGE_ARRIVED = "com.sanckspop.snackspopnew.order_created";

    }

    public static String parseStatus(String string) {

        return toCamelCase(string);
    }

    private static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = AppUtils.EMPTY;
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part) + " ";
        }
        return camelCaseString.trim();
    }


    public static String displayCurrencyInfoForLocale(Locale locale, Context mContext) {
        try {

            TelephonyManager teleMgr = (TelephonyManager) mContext.getSystemService(TELEPHONY_SERVICE);
            String countryCode = teleMgr.getSimCountryIso();
//
            Locale loc = new Locale("", countryCode.toUpperCase());
            Currency currency = Currency.getInstance(loc);
            return currency.getSymbol();
        }catch(Exception e){
            System.out.println("Locale: " + locale.getDisplayName());
            Currency currency = Currency.getInstance(locale);
            System.out.println("Currency Code: " + currency.getCurrencyCode());
            System.out.println("Symbol: " + currency.getSymbol());
            System.out.println("Default Fraction Digits: " + currency.getDefaultFractionDigits());
            System.out.println();
            return currency.getSymbol();
        }


    }


    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }


    public static void setPreferences(String prefrenceName, Context context, String preferenceValue) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(prefrenceName, preferenceValue).apply();
    }

    public static void setPreferences(String prefrenceName, Context context, boolean preferenceValue) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(prefrenceName, preferenceValue).apply();
    }

    public static String getStringPreferences(String preferenceName, Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(preferenceName, "");
    }

    public static String getUserId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFS.USER_ID, "");
    }

    public static String getAccessToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREFS.USER_TOKEN, "");
    }

    public static LatLng getUserLocation(Context context) {
        return new LatLng((double) PreferenceManager.getDefaultSharedPreferences(context).getFloat(PREFS.USER_Lat, 0),
                (double) PreferenceManager.getDefaultSharedPreferences(context).getFloat(PREFS.USER_Lng, 0));
    }

    public static boolean isGPSEnabled(Context context) {

        //            if (showEnableGPSDialog)
        return isProviderAvailable(context);
    }

    //    new code added on 05-04-2016
    public static boolean haveNetworkConnection(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null) {
            NetworkInfo nInfo = connectivity.getActiveNetworkInfo();

            //do your thing
            return nInfo != null && nInfo.getState() == NetworkInfo.State.CONNECTED;
        }
        else
            return false;
    }

    public static boolean isProviderAvailable(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean networkEnabled = lm
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return networkEnabled || gpsEnabled;

    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    public static String[] DateFormat_DD_MMM_YYYY(String date) {

        String newstring = "";
        String newstringTime = "";
        try {
//            2017-05-13 07:47:07.640Z
            Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    .parse(date);
            newstring = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(date1);
            newstringTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date1);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new String[]{newstring, newstringTime};

    }

    public static String DateFormat_HH_MM(String date) {

        String newstringTime = "";
        try {
//            2017-05-13 07:47:07.640Z
            Date date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    .parse(date);
            newstringTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(date1);

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newstringTime;

    }


    public static String putWithOutMultipartWithAccessToken(String url, String nameValuePairs, String accessToken) {


        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, nameValuePairs);
            Request request;
            if (TextUtils.isEmpty(nameValuePairs)) {
                String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
//                con.setRequestProperty("Authorization", "Basic " + encoded);
                request = new Request.Builder()
                        .url(url)
                        .put(null)
                        .addHeader("Authorization", "Basic " + encoded)
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("x-access-token", accessToken)
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .put(body)
                        .addHeader("authorization", "Basic cm9vdDpyb290")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .addHeader("x-access-token", accessToken)
                        .build();
            }

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

// --Commented out by Inspection START (8/8/17 12:15 PM):
//    public static String postWithOutMultipart(String url, String nameValuePairs) {
//
//
//        try {
//
//            OkHttpClient client = new OkHttpClient();
//
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody body = RequestBody.create(mediaType, nameValuePairs);
//            Request request = null;
//            if (TextUtils.isEmpty(nameValuePairs)) {
//                String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
////                con.setRequestProperty("Authorization", "Basic " + encoded);
//                request = new Request.Builder()
//                        .url(url)
//                        .post(null)
//                        .addHeader("Authorization", "Basic " + encoded)
//                        .addHeader("content-type", "application/json")
//                        .addHeader("cache-control", "no-cache")
//                        .build();
//            } else {
//                request = new Request.Builder()
//                        .url(url)
//                        .post(body)
//                        .addHeader("authorization", "Basic cm9vdDpyb290")
//                        .addHeader("content-type", "application/json")
//                        .addHeader("cache-control", "no-cache")
//                        .build();
//            }
//
//            Response response = client.newCall(request).execute();
//            return response.body().string();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//
//    }
// --Commented out by Inspection STOP (8/8/17 12:15 PM)

    public static String postWithOutMultipartWithAccessToken(String url, String nameValuePairs, String accessToken) {


        try {

            OkHttpClient client = new OkHttpClient();

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, nameValuePairs);
            Request request;
            if (TextUtils.isEmpty(nameValuePairs)) {
                String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
//                con.setRequestProperty("Authorization", "Basic " + encoded);
                request = new Request.Builder()
                        .url(url)
                        .post(null)
                        .addHeader("Authorization", "Basic " + encoded)
                        .addHeader("content-type", "application/json")
                        .addHeader("x-access-token", accessToken)
                        .addHeader("cache-control", "no-cache")
                        .build();
            } else {
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .addHeader("authorization", "Basic cm9vdDpyb290")
                        .addHeader("content-type", "application/json")
                        .addHeader("x-access-token", accessToken)
                        .addHeader("cache-control", "no-cache")
                        .build();
            }

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String postWithHeaderNew(String url,
                                           List<NameValuePairs> nameValuePairs,
                                           List<NameValuePairs> headerNameValuePairs) {

        try {

            MultipartUtility multipartUtility = new MultipartUtility(url, headerNameValuePairs);

            for (int i = 0; i < nameValuePairs.size(); i++) {
                LogCat.i("Name Value -->", nameValuePairs.get(i).getName() + "--->" + nameValuePairs.get(i).getValue());
                if (nameValuePairs.get(i).isFile()) {
                    if (nameValuePairs.get(i).getBmp() == null)
                        multipartUtility.addFilePart(
                                nameValuePairs.get(i).getName(),
                                nameValuePairs.get(i).getValue());
                    else {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        nameValuePairs.get(i).getBmp().compress(Bitmap.CompressFormat.JPEG,
                                50, stream);
                        byte[] byteArray = stream.toByteArray();
                        multipartUtility.addFilePart(
                                nameValuePairs.get(i).getName(),
                                nameValuePairs.get(i).getValue() + ".jpg",
                                byteArray);
                    }
                } else
                    multipartUtility.addFormPart(nameValuePairs.get(i).getName(), nameValuePairs.get(i).getValue());
            }
            multipartUtility.finishMultipart();
            String response = multipartUtility.getResponse();
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String getWithHeader(String url) {


        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", USER_AGENT);

            String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
            con.setRequestProperty("Authorization", "Basic " + encoded);
            con.setRequestProperty("Content-Type", "application/json");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            System.out.println(response.toString());
            return response.toString();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            return null;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static String deleteWithOutMultipartWithAccessToken(String url, String accessToken) {


        try {

            OkHttpClient client = new OkHttpClient();
//            RequestBody body = RequestBody.create(mediaType, nameValuePairs);
            String encoded = Base64.encodeToString(("root" + ":" + "root").getBytes(), Base64.NO_WRAP);
//                con.setRequestProperty("Authorization", "Basic " + encoded);
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .addHeader("Authorization", "Basic " + encoded)
                    .addHeader("content-type", "application/json")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("x-access-token", accessToken)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / 160f);
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                FOLDER_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("SaeeqTaxi", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Get the file path from the given Uri.
     *
     * @param context The context of the calling activity.
     * @param uri     The Uri whose file path is returned.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getFilePathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {

                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /*public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {MediaStore.Images.Media.DATA };
        try {

            cursor = context.getContentResolver()
                    .query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {

                final int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(column_index);
            }
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void HideKeyBoard(Activity mActivity) {
        InputMethodManager inputManager = (InputMethodManager) mActivity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View view = mActivity.getCurrentFocus();
        if (view == null)
            return;
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }




    ///JSON functions
    public static String getJSONStringValue(JSONObject obj, String param){
        if( obj.has(param) && !obj.isNull(param) ){
            return obj.optString(param);
        }else{
            return "";
        }
    }

}
