package com.snackspop.snackspopnew.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.fenchtose.nocropper.CropperView;
import com.snackspop.snackspopnew.BuildConfig;
import com.snackspop.snackspopnew.R;
import com.snackspop.snackspopnew.Utils.AppUtils;
import com.snackspop.snackspopnew.Utils.LogCat;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageCropperActivity extends AppCompatActivity {


    private static final int REQUEST_GALLERY = 21;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int permissionRequestIdCamera = 1;
    public static final int permissionRequestIdGallery = 2;
    private static final String TAG = "ImageCropperActivity";

    @BindView(R.id.imageview)
    CropperView mImageView;

    private Bitmap mBitmap;
    private boolean isSnappedToCenter = false;

    private Uri fileUri;

    public static Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_croper);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color

            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimary));
        }
        ButterKnife.bind(this);
    }


    @OnClick(R.id.image_button)
    public void onImageButtonClicked() {
        startGalleryIntent();
    }

    @OnClick(R.id.camera_button)
    public void onCameraButtonClicked() {

        startCameraIntent();
    }

    @OnClick(R.id.crop_button)
    public void onImageCropClicked() {
        cropImage();
    }

    @OnClick(R.id.rotate_button)
    public void onImageRotateClicked() {
        rotateImage();
    }

    @OnClick(R.id.snap_button)
    public void onImageSnapClicked() {
        snapImage();
    }


    @OnClick(R.id.cancel_button)
    public void onCancelClicked() {
        finish();
    }

    private void loadNewImage(String filePath) {
        mBitmap = BitmapFactory.decodeFile(filePath);
        Log.i(TAG, "bitmap: " + mBitmap.getWidth() + " " + mBitmap.getHeight());

        int maxP = Math.max(mBitmap.getWidth(), mBitmap.getHeight());
        float scale1280 = (float) maxP / 1280;

        if (mImageView.getWidth() != 0) {
            mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
        } else {

            ViewTreeObserver vto = mImageView.getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    mImageView.setMaxZoom(mImageView.getWidth() * 2 / 1280f);
                    return true;
                }
            });

        }

        mBitmap = Bitmap.createScaledBitmap(mBitmap, (int) (mBitmap.getWidth() / scale1280),
                (int) (mBitmap.getHeight() / scale1280), true);
        mImageView.setImageBitmap(mBitmap);
    }


    private void startCameraIntent() {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            if ((ActivityCompat.checkSelfPermission(ImageCropperActivity.this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(ImageCropperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(ImageCropperActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    File photoFile = AppUtils
                            .getOutputMediaFile(AppUtils.MEDIA_TYPE_IMAGE);
                    if( photoFile != null ) {
                        fileUri = FileProvider.getUriForFile(ImageCropperActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                }else {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // create a file to save image
                    fileUri = AppUtils
                            .getOutputMediaFileUri(AppUtils.MEDIA_TYPE_IMAGE);

                    // set the image file name
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                    // start the image Capture Intent
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            } else {
                String[] permissions = new String[]{
                        Manifest.permission.CAMERA, "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(permissions, permissionRequestIdCamera);
            }
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // create a file to save image
            fileUri = AppUtils
                    .getOutputMediaFileUri(AppUtils.MEDIA_TYPE_IMAGE);

            // set the image file name
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image Capture Intent
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case permissionRequestIdCamera: {

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] ==
                            PackageManager.PERMISSION_GRANTED) {
                        LogCat.e("permission granted ", i + "");
                        if (i == (permissions.length - 1)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                File photoFile = AppUtils
                                        .getOutputMediaFile(AppUtils.MEDIA_TYPE_IMAGE);
                                if( photoFile != null ) {
                                    fileUri = FileProvider.getUriForFile(ImageCropperActivity.this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                                    startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                                }
                            }else {
                                // create new Intent
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                                // create a file to save image
                                fileUri = AppUtils
                                        .getOutputMediaFileUri(AppUtils.MEDIA_TYPE_IMAGE);

                                // set the image file name
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                                // start the image Capture Intent
                                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                            }
                        }

                    } else {

                        Toast.makeText(ImageCropperActivity.this, getResources().getString(
                                R.string.message_camera_permision), Toast.LENGTH_LONG).show();
                        LogCat.e("permission not granted ", i + "");
                        break;
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

                break;
            }
            case permissionRequestIdGallery: {

                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] ==
                            PackageManager.PERMISSION_GRANTED) {
                        LogCat.e("permission granted ", i + "");
                        if (i == (permissions.length - 1)) {
                            // create new Intent
                            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(intent, REQUEST_GALLERY);
                        }

                    } else {

                        Toast.makeText(ImageCropperActivity.this, getResources().getString(
                                R.string.message_gallery_permission), Toast.LENGTH_LONG).show();
                        LogCat.e("permission not granted ", i + "");
                        break;
                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

                break;
            }
        }
    }

    private void startGalleryIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // only for gingerbread and newer versions
            if ((ActivityCompat.checkSelfPermission(ImageCropperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) &&
                    (ActivityCompat.checkSelfPermission(ImageCropperActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)) {

                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY);

            } else {

                String[] permissions = new String[]{
                        "android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.READ_EXTERNAL_STORAGE"
                };
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    requestPermissions(permissions, permissionRequestIdGallery);


            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_GALLERY);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent resultIntent) {
        super.onActivityResult(requestCode, responseCode, resultIntent);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (responseCode == RESULT_OK) {

                String absPath = AppUtils.getFilePathFromUri(this, fileUri);
                loadNewImage(absPath);

            }

        } else if (responseCode == RESULT_OK) {
            String absPath = AppUtils.getFilePathFromUri(this, resultIntent.getData());
            loadNewImage(absPath);
        }
    }

    private void cropImage() {

        Bitmap bitmap = mImageView.getCroppedBitmap();

        Intent in = new Intent();
//        in.putExtra(AppUtils.Extra.IMAGE_BITMAP, bitmap);
        image = bitmap;
        setResult(Activity.RESULT_OK, in);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void rotateImage() {
        if (mBitmap == null) {
            Log.e(TAG, "bitmap is not loaded yet");
            return;
        }

        mBitmap = AppUtils.rotateBitmap(mBitmap, 90);
        mImageView.setImageBitmap(mBitmap);
    }

    private void snapImage() {
        if (isSnappedToCenter) {
            mImageView.cropToCenter();
        } else {
            mImageView.fitToCenter();
        }

        isSnappedToCenter = !isSnappedToCenter;
    }
}
