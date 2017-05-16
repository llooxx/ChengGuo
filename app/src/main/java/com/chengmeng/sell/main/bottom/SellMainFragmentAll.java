package com.chengmeng.sell.main.bottom;

import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectDialog;
import com.chengmeng.tools.connect.ConnectList;
import com.chengmeng.tools.connect.ConnectListener;
import com.chengmeng.tools.connect.ServerURL;

public class SellMainFragmentAll extends SellMainFragmentBase {

    public SellMainFragmentAll() {
    }

    @Override
    public void onListLoad() {
        page++;
        updateDataFromNet();
    }

    @Override
    public void onListRefresh() {
        page = 1;
        updateDataFromNet();
    }

    @Override
    public boolean onLazyLoad() {
        page = 1;
        updateDataFromNet();
        return true;
    }

    private void updateDataFromNet() {
        StaticMethod.POST(getActivity(), ServerURL.BUSINESS_MAIN_HOT, new ConnectListener() {
            @Override
            public ConnectList setParam(ConnectList list) {
                list.put("page", page + "");
                return list;
            }

            @Override
            public ConnectDialog showDialog(ConnectDialog dialog) {
                return null;
            }

            @Override
            public void onResponse(String response) {
                dealResponse(response);
            }
        });
    }

}
