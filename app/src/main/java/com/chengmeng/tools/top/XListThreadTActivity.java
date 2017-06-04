package com.chengmeng.tools.top;

import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chengmeng.R;
import com.chengmeng.message.baichuan.util.AndTools;
import com.chengmeng.tools.views.xlist.XListThreadActivity;

public abstract class XListThreadTActivity extends XListThreadActivity {

    // ////////允许子类调用的方法
    public void setTitle(String title) {
        ((TextView) findViewById(R.id.top_text)).setText(title);
        // 由于未知原因的标题栏颜色问题，此处只能这样处理了
        setTopColors(R.color.start_topbar_bg);
    }

    public void setTopColors(int top_color) {// 用setTopColors不行，有编译检查
        try {
            LinearLayout top_layout = (LinearLayout) findViewById(R.id.top_layout);
            top_layout.setBackgroundColor(getResources().getColor(top_color));
        } catch (Exception e) {
        }
    }

    public void showBackButton() {
        ImageButton top_back = (ImageButton) findViewById(R.id.top_back);
        top_back.setVisibility(ImageButton.VISIBLE);
        top_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void showMenuButton() {
        ImageButton top_menu = (ImageButton) findViewById(R.id.top_menu);
        top_menu.setVisibility(ImageButton.VISIBLE);
        top_menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showContextMenu();
            }
        });
    }

    public void initActionBar(int color) {
        TActivity.initActionBar(this, color);
    }

    // ////////允许子类调用的方法-辅助
    public void showToast(String text) {
        AndTools.showToast(text);
    }

    /**
     * 显示弹出式菜单
     */
    public abstract void showContextMenu();
}
