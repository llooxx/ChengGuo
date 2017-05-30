package com.chengmeng.sell.category;

import android.annotation.SuppressLint;

import com.chengmeng.message.info.AnUserInfo;
import com.chengmeng.message.msg_tool.InfoTool;
import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectDialog;
import com.chengmeng.tools.connect.ConnectList;
import com.chengmeng.tools.connect.ConnectListener;
import com.chengmeng.tools.connect.ServerURL;

public class SellCategoryFragmentSchool extends SellCategoryFragmentBase {
    private String school = "";

    public SellCategoryFragmentSchool() {
    }

    @SuppressLint("ValidFragment")
    public SellCategoryFragmentSchool(int index) {
        super(index);
    }

    @Override
    public void onListLoad() {
        page++;
        updateDataFromNet();
    }

    private void initSchool() {
        AnUserInfo info = InfoTool.getUserInfo(getActivity());
        if (info != null)
            school = info.getSchool();
        if (school == null)
            school = "";
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
        if (school.equals(""))
            initSchool();
        if (!school.equals(""))
            StaticMethod.POST(getActivity(), ServerURL.BUSINESS_SCHOOL, new ConnectListener() {
                @Override
                public ConnectList setParam(ConnectList list) {
                    list.put("page", page + "");
                    list.put("category", index + "");
                    list.put("school", school);
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
