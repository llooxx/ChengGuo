package com.chengmeng.message.msg_tool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.alibaba.mobileim.WXAPI;
import com.alibaba.mobileim.YWAccountType;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.YWConversation;
import com.alibaba.mobileim.conversation.YWConversationType;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.kit.common.IMUtility;
import com.alibaba.mobileim.ui.WxChattingActvity;
import com.alibaba.mobileim.utility.IMUtil;
import com.chengmeng.R;
import com.chengmeng.main_main.MyApplication;
import com.chengmeng.message.baichuan.im.LoginSampleHelper;
import com.chengmeng.start.welcome.Welcome;

import java.lang.reflect.Field;
import java.util.List;

public class NoteNumberTool {
    //必须使用，Activity启动页
    private final static String lancherActivityClassName = Welcome.class.getName();
    private static int XIAOMI_CODE = 0;
    private NotificationManager nm;
    private Notification notification;
    private Context activity;

    public NoteNumberTool(Context activity) {
        this.activity = activity;
    }

    public void sendBadgeNumber(YWConversation con, YWMessage msg, int allMsgCount) {//最早请在onResume中
        try {
            Intent intent = getPendIntent(MyApplication.getInstance(), msg);
            sendBadgeNumber(getNoteText(con, msg, allMsgCount), allMsgCount + "", intent);
        } catch (Exception e) {
        }
    }

    private void sendBadgeNumber(String text, String number, Intent intent) {
        if (TextUtils.isEmpty(number)) {
            number = "0";
        } else {
            int numInt = Integer.valueOf(number);
            number = String.valueOf(Math.max(0, Math.min(numInt, 99)));
        }
        if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
            sendToXiaoMi(text, number, intent);
        } else if (Build.MANUFACTURER.equalsIgnoreCase("samsung")) {
            sendToSony(number);
        } else if (Build.MANUFACTURER.toLowerCase().contains("sony")) {
            sendToSamsumg(number);
        } else {
            sendToXiaoMi(text, number, intent);
        }
    }

    private void sendToXiaoMi(String text, String number, Intent intent) {
        nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
            builder.setContentTitle(activity.getResources().getString(R.string.app_name));
            builder.setContentText(text);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.drawable.ic_launcher);
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            PendingIntent pend = PendingIntent.getActivity(MyApplication.getInstance(),
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pend);
            notification = builder.build();

            Class miuiNotificationClass = Class.forName("android.app.MiuiNotification");
            Object miuiNotification = miuiNotificationClass.newInstance();
            Field field = miuiNotification.getClass().getDeclaredField("messageCount");
            field.setAccessible(true);
            field.set(miuiNotification, Integer.valueOf(number));// 设置信息数
            field = notification.getClass().getField("extraNotification");
            field.setAccessible(true);
            field.set(notification, miuiNotification);

            //miui6以上版本需要使用通知发送
            if (XIAOMI_CODE == 0) //一次初始化即可（这个数字我查找了好久……）
                XIAOMI_CODE = LoginSampleHelper.getInstance().getIMKit()
                        .getAccount().getLongLoginUserId().hashCode();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    nm.notify(XIAOMI_CODE, notification);
                }
            }, 200);
        } catch (Exception e) { //miui 6之前的版本，会捕获到异常
            Intent localIntent = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
            localIntent.putExtra("android.intent.extra.update_application_component_name", activity.getPackageName() + "/" + lancherActivityClassName);
            localIntent.putExtra("android.intent.extra.update_application_message_text", number);
            activity.sendBroadcast(localIntent);
        }

    }


    private void sendToSony(String number) {
        boolean isShow = true;
        if ("0".equals(number)) {
            isShow = false;
        }
        Intent localIntent = new Intent();
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", isShow);//是否显示
        localIntent.setAction("com.sonyericsson.home.action.UPDATE_BADGE");
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", lancherActivityClassName);//启动页
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", number);//数字
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", activity.getPackageName());//包名
        activity.sendBroadcast(localIntent);
    }

    private void sendToSamsumg(String number) {
        Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
        localIntent.putExtra("badge_count", number);//数字
        localIntent.putExtra("badge_count_package_name", activity.getPackageName());//包名
        localIntent.putExtra("badge_count_class_name", lancherActivityClassName); //启动页
        activity.sendBroadcast(localIntent);
    }


    private static String getNoteText(YWConversation con,
                                      YWMessage msg, int allMsgCount) {
        List<YWConversation> conversations = LoginSampleHelper.getInstance()
                .getIMKit().getAccount().getConversationManager().getConversationList();
        int cvsCount = 0;//未读的联系人个数;msg:当前的消息;allMsgCount：当前的未读消息数目
        String tips = "";
        String sender = "";//当前消息的发送者
        String text = "";//当前消息的内容
        if (conversations != null)
            for (YWConversation conversation : conversations)
                if (conversation.getUnreadCount() > 0)
                    ++cvsCount;
        if (cvsCount == 1 || allMsgCount == 1) {//减少无关运算
            sender = msg.getAuthorUserName();
            WXAPI tempInstance = WXAPI.getInstance();
            if (tempInstance != null) {
                IYWContact contact = IMUtility.getContactProfileInfo(msg.getAuthorUserId(),
                        msg.getAuthorAppkey());
                if (contact != null && !TextUtils.isEmpty(contact.getShowName())) {
                    sender = contact.getShowName();
                } else {
                    contact = tempInstance.getWXIMContact(msg.getAuthorUserId());
                    if (contact != null) {
                        sender = contact.getShowName();
                    }
                }
                sender = sender.equals(msg.getAuthorUserId()) &&
                        !TextUtils.isEmpty(msg.getAuthorUserName()) ?
                        msg.getAuthorUserName() : sender;
            }
            text = IMUtil.getContent(msg, WXAPI.getInstance().getLoginUserId(),
                    YWConversationType.P2P);//当前消息的内容
        }
        if (allMsgCount == 1) {
            tips = sender + "：" + text;
        } else if (cvsCount == 1) {
            tips = sender + " 发来" + allMsgCount + "条消息。";
        } else {
            tips = cvsCount + "位朋友发来" + allMsgCount + "条消息。";
        }
        return tips;
    }


    private static Intent getPendIntent(Context context, YWMessage msg) {
        Intent intent = WXAPI.getInstance().getNotificationIntent();
        String cvsId;
        if (intent == null) {
            intent = new Intent(context, WxChattingActvity.class);
        } else {
            cvsId = intent.getComponent().getClassName();
            intent = new Intent();
            intent.setClassName(context, cvsId);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("receiverId", msg.getAuthorUserId());

        cvsId = msg.getConversationId();
        intent.putExtra("conversationId", cvsId);
        intent.putExtra("conversationType", YWConversationType.P2P);
        intent.putExtra(YWAccountType.class.getSimpleName(), YWAccountType.open.getValue());

        return intent;

    }

}
