package com.chengmeng.more;

import android.support.v4.app.Fragment;

import com.chengmeng.R;
import com.chengmeng.message.homepage.MineService;
import com.chengmeng.message.msg_tool.InfoTool;
import com.chengmeng.tools.top.FragmentTActivity;

//原浆。

public class MyPublish extends FragmentTActivity {
    @Override
    public void onCreate() {
        setContentView(R.layout.more_my_publish);
        setTitle("已发布");
        showBackButton();
        initView();
        initActionBar(getResources().getColor(R.color.topbar_bg));

    }

    private void initView() {
        Fragment serviceFragment = new MineService(InfoTool.getUserID(this));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.more_my_publish_layout, serviceFragment).commit();
    }

    @Override
    public void showContextMenu() {

    }
}