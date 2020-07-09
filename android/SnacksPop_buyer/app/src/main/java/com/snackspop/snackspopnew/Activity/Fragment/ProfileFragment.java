package com.snackspop.snackspopnew.Activity.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.snackspop.snackspopnew.Activity.ChangeDetailsActivity;
import com.snackspop.snackspopnew.Activity.HomeActivity;
import com.snackspop.snackspopnew.Activity.ImageCropperActivity;
import com.snackspop.snackspopnew.Activity.NewPasswordActivity;
import com.snackspop.snackspopnew.Async.AsyncForAll;
import com.snackspop.snackspopnew.Model.NameValuePairs;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private static final int ACTIVITY_IMAGE_CROPPER = 10;
    private static final int ACTIVITY_UPDATE_DETAILS = 11;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    TextView et_first_name;
    TextView et_last_name;
    TextView et_phone_number;
    TextView et_password;
    TextView et_email;
    ImageView iv_profile;
    Context mContext;

    private View mProgressView;
    AsyncForAll mAsyncForAll;
    String nameValuePairsJsonString;
    JSONObject mFinalJsonObj;
    JSONArray imageJsoArray;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_profile, container, false);
        mContext = getContext();
        iniUi(rootView);
        return rootView;
    }

    private void iniUi(View rootView) {

        et_first_name = (TextView) rootView.findViewById(R.id.et_first_name);
        et_last_name = (TextView) rootView.findViewById(R.id.et_last_name);
        mProgressView = rootView.findViewById(R.id.progress_bar);

        et_phone_number = (TextView) rootView.findViewById(R.id.et_phone_number);
        et_password = (TextView) rootView.findViewById(R.id.et_password);

        et_email = (TextView) rootView.findViewById(R.id.et_email);
        iv_profile = (ImageView) rootView.findViewById(R.id.iv_profile);

        try {

            JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));
            et_email.setText(profileObj.optString("email"));
            et_first_name.setText(profileObj.optString("first_name"));
            et_last_name.setText(profileObj.optString("last_name"));
            et_phone_number.setText(profileObj.optString("phone_number"));

            if( AppUtils.getStringPreferences(AppUtils.PREFS.IS_SOCIAL, mContext).equals("1") ){
                et_email.setEnabled(false);
                et_password.setEnabled(false);
            }
            //if (profileObj.has("photo") && profileObj.getJSONArray("photo").length() > 0) {
            if (profileObj.has("photo")) {
                String photoString =profileObj.getString("photo");
                String url = "";
                if (photoString!= null && photoString.compareTo("null") != 0)
                    url =  AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + profileObj.optString("photo");

                //imageLoader.displayImage(AppUtils.BASE_URL + profileObj.getJSONArray("photo").getJSONObject(0).getString("path"),
                if (!TextUtils.isEmpty(url))
                {
                    Glide.with(mContext).load(url).placeholder(R.drawable.ic_emptyuser).into(iv_profile);
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        iv_profile.setOnClickListener(this);
        et_first_name.setOnClickListener(this);
        et_last_name.setOnClickListener(this);
        et_phone_number.setOnClickListener(this);
        et_password.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.et_first_name:
                startActivityForResult(new Intent(mContext, ChangeDetailsActivity.class).putExtra(AppUtils.EXTRA.CHANGE_PARAMTER, "first_name"), ACTIVITY_UPDATE_DETAILS);
                break;
            case R.id.et_last_name:
                startActivityForResult(new Intent(mContext, ChangeDetailsActivity.class).putExtra(AppUtils.EXTRA.CHANGE_PARAMTER, "last_name"), ACTIVITY_UPDATE_DETAILS);
                break;
            case R.id.et_phone_number:
                startActivityForResult(new Intent(mContext, ChangeDetailsActivity.class).putExtra(AppUtils.EXTRA.CHANGE_PARAMTER, "phone_number"), ACTIVITY_UPDATE_DETAILS);
                break;
            case R.id.et_password:
                startActivity(new Intent(mContext, NewPasswordActivity.class).putExtra(AppUtils.EXTRA.TOKEN, AppUtils.getUserId(mContext)));
                break;
            case R.id.iv_profile:
                startActivityForResult(new Intent(mContext, ImageCropperActivity.class), ACTIVITY_IMAGE_CROPPER);
                break;
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that the result was from the autocomplete widget.
        if (requestCode == ACTIVITY_IMAGE_CROPPER) {
            if (resultCode == RESULT_OK) {
                Bitmap bmp = ImageCropperActivity.image;
                iv_profile.setImageBitmap(bmp);
                showProgress(true);
                StartAsyncUpload(bmp);
            }
        } else if (requestCode == ACTIVITY_UPDATE_DETAILS) {

            try {

                JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));
                et_email.setText(profileObj.optString("email"));
                et_first_name.setText(profileObj.optString("first_name"));
                et_last_name.setText(profileObj.optString("last_name"));
                et_phone_number.setText(profileObj.optString("phone_number"));
                mContext.sendBroadcast(new Intent(AppUtils.BROADCAST.BR_UPDATE_USER_DATA));


            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void StartAsyncUpload(Bitmap bmp) {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }


        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            if (bmp == null)
                return;
            ArrayList<NameValuePairs> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new NameValuePairs("imageupload", bmp, "profile"));

            mAsyncForAll = new AsyncForAll(mContext, nameValuePairs, AppUtils.ASYNC_CLASSNAME.UPLOAD_FILE, getHeaderNameValuePair(),
                    false, true, true) {

                @Override
                protected Integer doInBackground(Void... params) {
                    urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPLOAD_FILE;
                    return super.doInBackground(params);
                }

                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);

                    mFinalJsonObj = finalResult;

                    // finalResult.get("")
                    try {
                        onCustomPostExecuteForTokens(result, MESSAGE, MethodName);
                    } catch (Exception e) {
                        e.printStackTrace();
                        onCustomPostExecuteForTokens(AppUtils.Async_FAILED_SERVER_UNREACHABLE, MESSAGE, MethodName);
                    }

                }

            };

            mAsyncForAll.execute();
        } else {
            Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }


    private void onUploadFileSuccess() {
        try {
            imageJsoArray = new JSONArray();
            int id = mFinalJsonObj.getInt("id");

            JSONObject jObj = new JSONObject();
            jObj.put("photo" , id);

            imageJsoArray.put(jObj);

            showProgress(true);
            StartAsyncUpdateProfilePic();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void StartAsyncUpdateProfilePic() {

        // Close Asynctask of it is already running
        if (mAsyncForAll != null && mAsyncForAll.getStatus() == AsyncTask.Status.PENDING) {
            mAsyncForAll.cancel(true);
            mAsyncForAll = null;
        }

        // Call Asynctaks now
        if (AppUtils.haveNetworkConnection(mContext)) {
            nameValuePairsJsonString = getNameValuePair();
            if (!TextUtils.isEmpty(nameValuePairsJsonString)) {
                mAsyncForAll = new AsyncForAll(mContext, nameValuePairsJsonString, AppUtils.ASYNC_CLASSNAME.UPDATE,
                        false, true, false) {

                    @Override
                    protected Integer doInBackground(Void... params) {
                        isPut = true;
                        urlString = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.UPDATE ;
                        accessToken = AppUtils.getAccessToken(mContext);
                        return super.doInBackground(params);
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        super.onPostExecute(result);

                        mFinalJsonObj = finalResult;

                        // finalResult.get("")
                        onCustomPostExecuteForTokens(result, MESSAGE, MethodName);
                    }
                };
                mAsyncForAll.execute();
            } else {
                Snackbar.make(mProgressView, getResources().getString(
                        R.string.message_server_down), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } else {
            showProgress(false);
            Snackbar.make(mProgressView, getResources().getString(R.string.message_no_internet), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void onCustomPostExecuteForTokens(int result, String Message, String ClassName) {
        showProgress(false);
        switch (result) {
            case AppUtils.Async_SUCCESS:
                if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPDATE))
                    onUpdateSuccess();
                else if (ClassName.equals(AppUtils.ASYNC_CLASSNAME.UPLOAD_FILE))
                    onUploadFileSuccess();
                break;
            case AppUtils.Async_FAILED_WITH_DESC:
                onFailedDesc(Message);
                break;
            case AppUtils.Async_FAILED_SERVER_UNREACHABLE:
                onServerUnreachable();
                break;
            default:
                break;
        }

    }


    protected void onServerUnreachable() {

        Snackbar.make(mProgressView, getResources().getString(
                R.string.message_server_down), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onFailedDesc(String Message) {
        Snackbar.make(mProgressView, Message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    protected void onUpdateSuccess() {

        Snackbar.make(mProgressView, "Profile Picture updated successfully", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        try {
            AppUtils.setPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext, mFinalJsonObj.getJSONObject("user").toString());
            JSONObject profileObj = new JSONObject(AppUtils.getStringPreferences(AppUtils.PREFS.USER_OBJECT_STRING, mContext));
            //if (profileObj.has("photo") && profileObj.getJSONArray("photo").length() > 0) {
            if (profileObj.has("photo")) {
                String url = AppUtils.BASE_URL + AppUtils.AFTER_BASE_URL.IMAGE_URL + profileObj.optString("photo");
                //imageLoader.displayImage(AppUtils.BASE_URL + profileObj.getJSONArray("photo").getJSONObject(0).getString("path"),
                Glide.with(mContext).load(url).placeholder(R.drawable.ic_emptyuser).into(iv_profile);
                final ImageView imgView = ((HomeActivity)mContext).getProfilePhotoView();
                if (imgView != null)
                {
                    Glide.with(mContext).load(url).placeholder(R.drawable.ic_emptyuser).into(imgView);
                }

            }

            if (profileObj.has("first_name") && profileObj.has("last_name"))
            {
                TextView userNameView = ((HomeActivity)mContext).getUserNameTextView();

                String userName = AppUtils.getJSONStringValue(profileObj,"first_name") + " " + AppUtils.getJSONStringValue(profileObj,"last_name");

                userNameView.setText(userName);
            }
            mContext.sendBroadcast(new Intent(AppUtils.BROADCAST.BR_UPDATE_USER_DATA));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getNameValuePair() {
        try {
            //JSONObject jObj = new JSONObject();
            //jObj.put("photo", imageJsoArray);
            return imageJsoArray.get(0).toString();
        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }

    private List<NameValuePairs> getHeaderNameValuePair() {
        // et_firstname, et_email, et_password, et_confirm_password
        try {
            List<NameValuePairs> list = new ArrayList<NameValuePairs>();
            list.add(new NameValuePairs("x-access-token", AppUtils.getAccessToken(mContext)));
            return list;

        } catch (Exception e) {

            e.printStackTrace();

            return null;
        }
    }
}
