package com.example.carson_ho.webview_demo;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "PhotoActivity";
    /**
     * 申请权限
     */
    public static final int NICK_REQUEST_CAMERA_CODE = 105;
    /**
     * 启动相机
     */
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 300;

    private static final int REQUEST_CODE_CROP_IMAGE = 0x776;
    /**
     * 照片选择路径
     */
    private String picFileFullName;
    private boolean isCrop;
    private File outCropFile;

    private ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        iv_image = findViewById(R.id.iv_image);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                getPic();
                break;
            case R.id.btn_2:
                isCrop = true;
                getPic();
                break;
            case R.id.btn_3:
                startActivity(new Intent(this, WebviewActivity.class));
                break;
            case R.id.btn_4:

                break;
            case R.id.btn_5:

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: // 启动相机拍照获取 照片
                if (resultCode == -1) {
                    //发送图片
                    if (TextUtils.isEmpty(picFileFullName)) {
                        Toast.makeText(this, "相机故障，请重试", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Log.i(TAG, "图片的本地路径是：" + picFileFullName);
                    if (isCrop) {
                        cropImage();
                    } else {
                        Bitmap loacalBitmap = getLoacalBitmap(picFileFullName);
                        iv_image.setImageBitmap(loacalBitmap);
                    }
                }
                break;
            case REQUEST_CODE_CROP_IMAGE: // 裁剪数据
                String path = outCropFile.getAbsolutePath();
                Log.i(TAG, "图片裁剪后的本地路径是：" + path);
                Bitmap loacalBitmap = getLoacalBitmap(path);
                iv_image.setImageBitmap(loacalBitmap);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 200) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPic();
            } else {
                Toast.makeText(this, "没权限", Toast.LENGTH_SHORT).show();
            }
        }

    }

    /**
     * 加载本地图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);  ///把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getPic() {
        if (Build.VERSION.SDK_INT < 23) {
            takePicture();
        } else {
            //6.0
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //该权限已经有了
                takePicture();
            } else {
                //申请该权限
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, NICK_REQUEST_CAMERA_CODE);
            }
        }
    }

    /**
     * 拍照
     */
    public void takePicture() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File outFile = new File(outDir, "PAI" + System.currentTimeMillis() + ".jpg");
            picFileFullName = outFile.getAbsolutePath();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileProvider", outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

            } else {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outFile));

            }
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        } else {
            Log.e("TAG", "请确认已经插入SD卡");
        }
    }

    /**
     * 裁剪图片
     */
    private void cropImage() {
        File cropName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        outCropFile = new File(cropName, "Crop" + System.currentTimeMillis() + ".jpg");

        Intent intent = new Intent("com.android.camera.action.CROP");
        Uri uri1;
        Uri photoURI;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoURI = Uri.fromFile(outCropFile);
            uri1 = getImageContentUri(picFileFullName);
        } else {
            uri1 = Uri.fromFile(new File(picFileFullName));
            photoURI = Uri.fromFile(outCropFile);
        }

        //可以选择图片类型，如果是*表明所有类型的图片
        intent.setDataAndType(uri1, "image/*");
        // 下面这个crop = true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例，这里设置的是正方形（长宽比为1:1）
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
//        intent.putExtra("outputX", 300);
//        intent.putExtra("outputY", 300);
        //裁剪时是否保留图片的比例，这里的比例是1:1
        intent.putExtra("scale", true);
        //是否是圆形裁剪区域，设置了也不一定有效
        //intent.putExtra("circleCrop", true);

        //设置输出的格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

//        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outCropFile));
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent = Intent.createChooser(intent, getString(R.string.vrop_image));


        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    private Uri getImageContentUri(String path) {
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{path}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, path);
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        }
    }
}
