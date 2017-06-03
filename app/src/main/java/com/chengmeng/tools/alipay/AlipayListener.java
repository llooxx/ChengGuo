package com.chengmeng.tools.alipay;

public interface AlipayListener {

    /**
     * 支付是否成功
     *
     * @param is_success 是否支付成功
     */
    public void onResult(boolean is_success);

}
