package com.chengmeng.main_main;

import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chengmeng.R;
import com.chengmeng.message.info.AnBaseInfo;
import com.chengmeng.message.msg_tool.InfoTool;
import com.chengmeng.message.msg_tool.UserStateTool;
import com.chengmeng.tools.views.QianxunToast;
import com.chengmeng.tools.views.slidemenu.app.SlidingFragmentActivity;

//专为MainMain开发
public abstract class MainTActivity extends SlidingFragmentActivity {
    private View statusBarView = null;// 手动保证调用一次
    private int cur_page = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreate();
//        //通知栏一体化（注：此处slideMenu会自行处理顶部，确保不会错位）
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //涟漪点击效果（需要在setContentView之后调用，否则在onAtacheView会拉伸）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setTheme(android.R.style.Theme_Material_Light_NoActionBar);
    }

    // ////////允许子类调用的方法
    protected void setTopPosition(int position) {// 此处不要用全局变量
        TextView tv_title = (TextView) findViewById(R.id.top_text);
        String[] text_title = {"消息", "口袋掌柜", "附近需求", "成果", "我的"};
        if (position < 0 || position > 4) {
            tv_title.setText("橙果");
            return;
        }
        tv_title.setText(text_title[position]);
        cur_page = position;

    }

    protected void initHeadIcon(OnClickListener listener) {
        ImageView top_head = (ImageView) findViewById(R.id.top_head);
        top_head.setOnClickListener(listener);
        // 更新头像
        if (UserStateTool.isLoginEver(this)) {// 只要登录过
            AnBaseInfo base_info = InfoTool.getBaseInfo(this);// 已确保不会返回null
            String user_id = base_info.getUserId();
            if (null != user_id && !"".equals(user_id)) {// 本地有基本信息，就同步
                top_head.setImageBitmap(base_info.getHeadIcon());
            }
        }
    }


    protected void hideMenuButton() {
//        Button top_location = (Button) findViewById(R.id.top_location);
//        top_location.setVisibility(ImageButton.INVISIBLE);
//        TextView top_location_text = (TextView) findViewById(R.id.top_location_text);
//        top_location_text.setVisibility(ImageButton.NVISIBLE);
//        TextView top_title = (TextView) findViewById(R.id.top_text);
//        top_title.setVisibility(ImageButton.INVISIBLE);
    }

    // ////////允许子类调用的方法-辅助
    protected void showToast(String text) {
        QianxunToast.showToast(this, text, QianxunToast.LENGTH_SHORT);
    }

    // 子类只需要实现即可的方法/////////////////////////////////////////////////////////////

    /**
     * 相当于原来的OnCreate()，直接从setContentView开始写即可
     */
    protected abstract void onCreate();

    /**
     * 显示弹出式菜单
     */
    protected abstract void showContextMenu(View v, int cur_page);

    // ////////////////////////////显示popup menu///////////////////////////////
    private void showContextMenuTest(View v, int cur_page) {
        View popupWindow_view = getLayoutInflater().inflate(
                R.layout.tab_03_popbutton_delect, null, false);
        final PopupWindow popupwindow_search_reward = new PopupWindow(
                popupWindow_view, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, true);
        popupwindow_search_reward.showAsDropDown(v);

        TextView popbt_delect_01 = (TextView) popupWindow_view
                .findViewById(R.id.popbt_delect_01);
        popbt_delect_01.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                popupwindow_search_reward.dismiss();
            }
        });
        popupWindow_view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (popupwindow_search_reward != null
                        && popupwindow_search_reward.isShowing()) {
                    popupwindow_search_reward.dismiss();
                }
                return false;
            }
        });
    }

}
