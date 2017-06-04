package com.chengmeng.tools.top;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import com.chengmeng.tools.bitmap.MultiBitmapTool;
import com.chengmeng.tools.file.FileTool;

import java.util.ArrayList;

public abstract class NetIActivity extends NetActivity {
    private final String[] context_items = new String[]{"相机拍照", "相册选取"};
    protected static final int CODE_CAMERA_START = 1;
    protected static final int CODE_ALBUM_START = 2;
    protected static final int CODE_MULITY_START = 3;
    protected static final int CODE_CROP_START = 4;
    private AlertDialog method_dialog;
    protected Uri uritempFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTempUri();
    }

    public void initTempUri() {
        //为了确保GaoNeng文件夹存在
        FileTool.getBaseSDCardPath();
        uritempFile = getTempUri();
    }

    public void cropPhoto(Uri in, int code) {
        cropPhoto(in, uritempFile, this, code, 0, 0, 0, 0);
    }

    public void startCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", "JPEG");//返回格式
        startActivityForResult(intent, CODE_CAMERA_START);
    }

    public void startAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CODE_ALBUM_START);
    }

    public void cropCamera(Intent data) {
        cropPhoto(data.getData(), CODE_CROP_START);// 裁剪图片
    }

    public void cropAlbum() {
        cropPhoto(uritempFile, CODE_CROP_START);// 裁剪图片
    }

    public Bitmap getUriBitmap() {
        try {
            return BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uritempFile));
        } catch (Exception e) {
            return null;
        }
    }

    public Bitmap getUriBitmap(Uri uri) {
        try {
            return BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(uri));
        } catch (Exception e) {
            return null;
        }
    }

    public void startMultiAlbum(int maxCount, ArrayList<String> old_img_array) {
        MultiBitmapTool.startBitmapChoose(this, CODE_MULITY_START, maxCount, old_img_array);
    }

    public ArrayList<String> getMultiAlbum(Intent data) {
        return MultiBitmapTool.getBitmapList(RESULT_OK, data);
    }

    public void showChooseDialog() {
        if (method_dialog == null)
            method_dialog = new AlertDialog.Builder(this).setTitle("请选择方式	")
                    .setItems(context_items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    startCamera();
                                    break;
                                case 1:
                                    startAlbum();
                                    break;
                            }
                        }
                    }).create();
        method_dialog.show();
    }

    //让用户自己处理去吧
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_START:
//                    if (data != null)
//                        cropCamera(data);
                    break;
                case CODE_ALBUM_START:
//                    cropAlbum();
                    break;
                case CODE_MULITY_START:
                    break;
                case CODE_CROP_START:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /////////////////静态方法//////////////////

    // 调用系统裁剪
    public static void cropPhoto(Uri in, Uri out, Activity activity, int code) {
        cropPhoto(in, out, activity, code, 1, 1, 300, 300);//用于头像
    }

    public static void cropPhoto(Uri in, Uri out, Activity activity, int code,
                                 int aspectX, int aspectY, int outputX, int outputY) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(in, "image/*");
        intent.putExtra("crop", "true");
        if (aspectX > 0 && aspectY > 0) {
            intent.putExtra("aspectX", aspectX);// 宽高的比例
            intent.putExtra("aspectY", aspectY);
        }
        if (outputX > 0 && outputY > 0) {
            intent.putExtra("outputX", outputX);// 裁剪图片宽高
            intent.putExtra("outputY", outputY);
        }
        intent.putExtra("return-data", false);//内存过小的手机返回大的图片会崩溃
        //uritempFile为Uri类变量，实例化uritempFile
        intent.putExtra(MediaStore.EXTRA_OUTPUT, out);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        activity.startActivityForResult(intent, code);
    }

    public static Uri getTempUri() {
        return Uri.parse("file://" + "/" +
                Environment.getExternalStorageDirectory().getPath()
                + "/GaoNeng/" + "headIconTemp.jpg");
    }

}
