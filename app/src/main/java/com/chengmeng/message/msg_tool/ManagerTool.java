package com.chengmeng.message.msg_tool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;

import com.chengmeng.main_main.MainMain;
import com.chengmeng.message.baichuan.mine.MineConfig;
import com.chengmeng.message.baichuan.util.AndTools;
import com.chengmeng.sell.discuss.DiscussTool;
import com.chengmeng.sell.main.bottom.AnSellMainItem;
import com.chengmeng.sell.main.bottom.SellMainAdapterBase;
import com.chengmeng.start.tool.UserTool;
import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectDialog;
import com.chengmeng.tools.connect.ConnectEasy;
import com.chengmeng.tools.connect.ConnectList;
import com.chengmeng.tools.connect.ConnectListener;
import com.chengmeng.tools.connect.ServerURL;

import java.util.ArrayList;

public class ManagerTool {
    private static final String managerPhone = "65169890";
    private static boolean isManagerLogin = false;

    public static boolean isManagerLogin() {
        return isManagerLogin;
    }

    public static boolean isManagerPhone(String phone) {
        if (!TextUtils.isEmpty(phone))
            return phone.endsWith(managerPhone);
        return false;
    }

    public static void setManagerLogin(boolean managerLogin) {
        isManagerLogin = managerLogin;
    }

    public static boolean isManagerEnable() {
        return !ServerURL.isDisenableManager();
    }

    public static void showLoginDialog(final Activity context,
                                       final String name, final String pass) {
        new AlertDialog.Builder(context)
                .setTitle("管理员模式")
                .setMessage("管理员模式下，将提供服务的删除功能。但也会禁用部分功能，" +
                        "要查看禁用的内容，请使用普通用户登录。\n为安全起见，管理员登录不保存任何信息，" +
                        "下次使用需要重新登录。")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginManager(context, name, pass);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private static void loginManager(final Activity context,
                                     final String name, final String pass) {
        ConnectEasy.POSTLOGIN(context, ServerURL.MANAGER_LOGIN, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("admin.username", name);
                list.put("admin.password", pass);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(context, "玩命登录中……", "玩命登录中……", false);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (!"1".equals(response)) {
                    AndTools.showToast(context, "登录失败");
                    return;
                }
                //登录成功
                AndTools.showToast(context, "登录成功");
                UserTool.saveUser(context, name, "");
                setManagerLogin(true);
                InfoTool.saveUserID(context, "");

                //登录讨论
                DiscussTool.getInstance().loginDiscuss(context, MineConfig.USER_DISCUSS,
                        "高能管理员", true, "www.sylsylsyl.com/software/qianxun/ICON/icon.png");

                Intent intent = new Intent(context, MainMain.class);
                context.startActivity(intent);
                context.finish();
            }
        });
    }

    public static void onListLongClick(final Activity context, final int position,
                                       final ArrayList<AnSellMainItem> item_list,
                                       final SellMainAdapterBase adapter) {
        AnSellMainItem item = item_list.get(position);
        new AlertDialog.Builder(context)
                .setTitle("服务屏蔽")
                .setMessage("将会屏蔽该服务：\n" + item.getName() + "\n确定？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteService(context, position, item_list, adapter);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private static void deleteService(final Activity context, final int position,
                                      final ArrayList<AnSellMainItem> item_list,
                                      final SellMainAdapterBase adapter) {
        StaticMethod.POST(context, ServerURL.MANAGER_DEL_SERVICE, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("serviceId", item_list.get(position).getId());
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(context, "正在执行……", "正在执行……", false);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                if (!"1".equals(response)) {
                    AndTools.showToast(context, "处理失败");
                    return;
                }
                //屏蔽成功
                AndTools.showToast(context, "处理成功");
                item_list.remove(position);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
