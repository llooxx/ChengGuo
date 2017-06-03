package com.chengmeng.tools.alipay;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.alipay.sdk.app.PayTask;
import com.chengmeng.message.msg_tool.InfoTool;
import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectDialog;
import com.chengmeng.tools.connect.ConnectList;
import com.chengmeng.tools.connect.ConnectListener;
import com.chengmeng.tools.connect.ServerURL;

public class AlipayEasy extends AsyncTask<Void, Void, String> {
    private Activity activity = null;
    private AlipayBuilder builder = null;
    private AlipayListener listener = null;
    private String payInfo = "";
    private int curr_money = 0;//当前余额，以后可能有用

    //构造器
    public AlipayEasy(Activity activity, AlipayBuilder builder, AlipayListener listener) {
        this.activity = activity;
        this.builder = builder;
        this.listener = listener;
    }

    //开始运行
    public void start() {//这里可以考虑请求余额，后期
        if (builder == null) {
            if (listener != null)
                listener.onResult(false);
            return;
        }
        getAlipaySign();
    }

    //获取签名
    private void getAlipaySign() {
        StaticMethod.POST(activity, ServerURL.ALIPAY_SIGN, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("ali_sign", builder.build());
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                dialog.config(activity, "正在处理", "请稍候……", false);
                return dialog;
            }

            @Override
            public void onResponse(String response) {
                startToPay(response);
            }
        });
    }

    //开始支付
    private void startToPay(String response) {
        if (response == null || response.equals("")
                || response.equals("0")) {//失败处理
            if (listener != null)
                listener.onResult(false);
            return;
        }
        payInfo = AlipayBuilder.builderAll(builder.build(), response);
        AlipayEasy.this.execute();
    }

    //后台执行发起支付
    @Override
    protected String doInBackground(Void... params) {
        // 构造PayTask 对象
        PayTask alipay = new PayTask(activity);
        // 调用支付接口，获取支付结果
        String result = alipay.pay(payInfo, true);
        return result;
    }

    //支付宝返回后
    @Override
    protected void onPostExecute(String result) {
        AlipayResult alipayResult = new AlipayResult(result);//其中会处理null
        String resultStatus = alipayResult.getResultStatus();
        /**
         * 同步返回的结果必须放置到服务端进行验证
         * （验证的规则请看https://doc.open.alipay.com/doc2/detail.htm
         * ?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&docType=1)
         * 建议商户依赖异步通知
         *  判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
         * 判断resultStatus 为非"9000"则代表可能支付失败
         * "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
         * 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
         */
        if (TextUtils.equals(resultStatus, "9000")) {
            //在支付宝中，只能通知后台结果了，成败就不必管了
            tellServerAlipayState(true);
            if (listener != null)
                listener.onResult(true);
        } else {
            if (listener != null)
                listener.onResult(false);
        }
    }

    //通知后台支付状态
    private void tellServerAlipayState(final boolean state) {
        StaticMethod.POST(activity, ServerURL.ALIPAY_CONFIRM, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {//userId,ugsId,isSucceed
                list.put("userId", InfoTool.getUserID(activity));
                list.put("ugsId", builder.getServiceID());
                list.put("isSucceed", state ? 1 : 0);
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
            }
        });
    }


    /**
     * 发起支付
     */
    public static void pay(Activity activity, AlipayBuilder builder, AlipayListener listener) {
        new AlipayEasy(activity, builder, listener).start();
    }

}
