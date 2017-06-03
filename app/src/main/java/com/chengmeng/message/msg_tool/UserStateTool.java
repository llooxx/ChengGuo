package com.chengmeng.message.msg_tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.chengmeng.message.baichuan.mine.BaiChuanUtils;
import com.chengmeng.start.login.Login;
import com.chengmeng.start.tool.UserTool;
import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectTool;
import com.chengmeng.tools.connect.ServerURL;
import com.chengmeng.tools.views.QianxunToast;

/**
 * 用户状态相关类
 */
public class UserStateTool {

    /**
     * 用户是否现在是登录状态
     */
    public static boolean isLoginNow(Context context) {
        return UserTool.getLoginState(context);
    }

    /**
     * 用户是否曾经登录过
     */
    public static boolean isLoginEver(Context context) {
        if (isLoginNow(context)) return true;
        try {
            return !UserTool.getUser(context)[1].equals("");// 已经确保不会null
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 跳转到登录界面,并显示提示信息
     */
    public static void goToLogin(Context context) {
        goToLogin(context, true);
    }

    /**
     * 跳转到登录界面
     */
    public static void goToLogin(Context context, boolean show_toast) {
        if (show_toast) QianxunToast.showToast(context, "欢迎登录", QianxunToast.LENGTH_SHORT);
        Intent intent = new Intent(context, Login.class);
        context.startActivity(intent);
    }

    /**
     * 退出登录，其中包含finish并跳转到登录界面的代码
     */
    public static void logout(Activity activity) {
        try {
            UserTool.saveUser(activity, "", "");// 清空用户名与密码
            ConnectTool.saveCookie(activity, "");// 清空cookie
            StaticMethod.POST(activity, ServerURL.LOGOUT, null);
            BaiChuanUtils.logout(activity);//退出登录
        } catch (Exception e) {
        }
    }

    // 检测网络是否可用
    private static boolean isNetworkEnable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

}
