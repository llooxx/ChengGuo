package com.chengmeng.tools.bitmap;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class MultiBitmapTool {

    public static ArrayList<String> getBitmapList(int resultCode, Intent data) {
        if (resultCode == -1) {//因为 RESULT_OK=-1
            ArrayList<String> img_array = data.getStringArrayListExtra("result_list");
            //boolean need_compress = data.getBooleanExtra("need_compress", false);
            return img_array;
        }
        return null;
    }

    public static void startBitmapChoose(Activity activity, int requestCode, int maxCount,
                                         ArrayList<String> old_img_array) {
        Intent intent = getBitmapIntent(activity, maxCount, old_img_array);
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
        }
    }

    public static void startBitmapChoose(Fragment fragment, int requestCode, int maxCount,
                                         ArrayList<String> old_img_array) {
        Intent intent = getBitmapIntent(fragment.getActivity(), maxCount, old_img_array);
        try {
            fragment.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
        }
    }

    private static Intent getBitmapIntent(Activity activity, int maxCount,
                                          ArrayList<String> old_img_array) {
        Intent intent = new Intent(activity, MultiBitmap.class);
        //内部此类有不完善之处，我修改了布局文件
        // aliwx_multi_image_player下的select_finish/left_button的大小
        //另外，在java中，对null强制转换不会报错。
        intent.putExtra("titleRightText", "确定");
        intent.putExtra("maxCount", maxCount);
        intent.putExtra("max_toast", "最多选择" + maxCount + "张图片");
        intent.putExtra("need_choose_original_pic", false);
        intent.putStringArrayListExtra("pre_checked_images", old_img_array);
        return intent;
    }
}
