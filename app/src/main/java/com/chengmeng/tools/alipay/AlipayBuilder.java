package com.chengmeng.tools.alipay;

import java.net.URLEncoder;

public class AlipayBuilder {
    // 商户PID
    private static final String PARTNER = "2088221600673106";
    // 商户收款账号
    private static final String SELLER = "jinanchengmeng@outlook.com";
    // 支付类型， 固定值，1为立即支付
    private static final int PAY_TYPE = 1;
    // 参数编码， 固定值
    private static final String DEFAULT_CHARSET = "utf-8";
    // 签名加密方式
    private static final String SIGN_TYPE = "RSA";
    // 支付等待超时
    private static final String WAIT_TIME = "30m";
    // 服务器异步通知页面路径----自己开服务器测试一下
    private static final String RESPONSE_URL = "http://notify.msp.hk/notify.htm";//http://notify.msp.hk/notify.htm
    // 服务接口名称， 固定值
    private static final String RESPONSE_METHOD = "mobile.securitypay.pay";//mobile.securitypay.pay
    //订单信息
    private String orderInfo = "";
    private String service_id = "";


    /**
     * 构建支付宝请求对象--仅此一个构造器，防止误解
     *
     * @param service_id     服务ID
     * @param service_name   服务名字
     * @param service_detail 服务详细描述
     * @param service_price  服务价格（请确保是数字格式）
     */
    public AlipayBuilder(String service_id, String service_name,
                         String service_detail, String service_price) {
        if (service_id == null) {//便于后期拓展处理
            return;
        }
        this.service_id = service_id;
        init();
        build(service_id, service_name, service_detail, service_price);
    }

    //初始化数据
    private void init() {
        //初始化字符串
        orderInfo = "";
        // 签约合作者身份ID
        orderInfo = "partner=" + "\"" + PARTNER + "\"";
        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
        // 支付类型， 固定值
        orderInfo += "&payment_type=\"" + PAY_TYPE + "\"";
        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"" + DEFAULT_CHARSET + "\"";
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + RESPONSE_URL + "\"";
        // 服务接口名称， 固定值
        orderInfo += "&service=\"" + RESPONSE_METHOD + "\"";
        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"" + WAIT_TIME + "\"";
        //----------其他可选数据----------
        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        //orderInfo += "&return_url=\"m.alipay.com\"";
        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";
    }

    private String build(String service_id, String service_name,
                         String service_detail, String service_price) {

        //service_price判断与处理，此处就用string，用double-try即可

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + service_id + "\"";
        // 商品名称
        orderInfo += "&subject=" + "\"" + service_name + "\"";
        // 商品详情
        orderInfo += "&body=" + "\"" + service_detail + "\"";
        // 商品金额
        orderInfo += "&total_fee=" + "\"" + service_price + "\"";
        return orderInfo;
    }

    public String build() {
        return orderInfo;
    }

    public String getServiceID() {
        return service_id;
    }

    //禁止外部调用，请使用静态方法
    private String buildAll(String order_info, String sign) {
        try {//仅需对sign 做URL编码
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (Exception e) {
        }
        String all_info = order_info + "&sign=\"" + sign +
                "\"&" + "sign_type=\"" + SIGN_TYPE + "\"";
        return all_info;
    }

    public static String builder(String service_id, String service_name,
                                 String service_detail, String service_price) {
        return new AlipayBuilder(service_id, service_name, service_detail, service_price).build();
    }

    public static String builderAll(String order_info, String sign) {
        return new AlipayBuilder(null, null, null, null).buildAll(order_info, sign);
    }

}
