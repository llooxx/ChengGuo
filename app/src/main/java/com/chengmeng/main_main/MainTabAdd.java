package com.chengmeng.main_main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.chengmeng.R;
import com.chengmeng.dynamic.PostDynamic;
import com.chengmeng.message.msg_tool.UserStateTool;
import com.chengmeng.message.baichuan.util.AndTools;
import com.chengmeng.request.PostRequest;
import com.chengmeng.service.PostServiceActivity;

//返回式启动
public class MainTabAdd extends Activity implements OnClickListener {
    public static final int CODE_ADD = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 对话框全屏代码（写在style中不成功）
        Window win = getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        win.setGravity(Gravity.BOTTOM);
//        win.setWindowAnimations(R.style.bottomDialogAnim); // 添加动画
        // 界面
        setContentView(R.layout.tab_add);
        initVIew();
    }

    private void initVIew() {
        Button btn_add_sell = (Button) findViewById(R.id.tab_add_btn_sell);
        Button btn_add_show = (Button) findViewById(R.id.tab_add_btn_show);
        Button btn_cancel = (Button) findViewById(R.id.tab_add_btn_cancel);

        RotateAnimation animation = new RotateAnimation(0f, 180f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(500);//设置动画持续时间
        animation.setInterpolator(new DecelerateInterpolator());
        btn_cancel.startAnimation(animation);

        RelativeLayout l1 = (RelativeLayout) findViewById(R.id.layout1);
        RelativeLayout l2 = (RelativeLayout) findViewById(R.id.layout2);

        startAnimation(btn_add_sell, l1, true, 45f);
        startAnimation(btn_add_show, l2, true, -45f);
        FrameLayout layout = (FrameLayout) findViewById(R.id.tab_add_layout);
        btn_add_show.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        layout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_add_btn_sell2:
                startMyActivity(PostServiceActivity.class);
                break;
            case R.id.tab_add_btn_show:
                startMyActivity(PostDynamic.class);
                break;
            case R.id.tab_add_btn_cancel:
            case R.id.tab_add_layout:
                finish();
                break;
        }
    }

    private void startMyActivity(Class<?> cls) {
        if (UserStateTool.isLoginEver(this)) {
            if (UserStateTool.isLoginNow(this)) {
                Intent intent = new Intent(MainTabAdd.this, cls);
                startActivity(intent);
                finish();
            } else AndTools.showToast(this, "您正处于离线状态");
        } else {
            UserStateTool.goToLogin(this);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        setResult(CODE_ADD);
        super.onDestroy();
    }

    public void startAnimation(final View view, final View view2, final boolean isRotate, final float r) {
        AnimationSet as = new AnimationSet(true);
        Animation a1 = new ScaleAnimation(0.1f, 1.0f, 0.1f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        a1.setDuration(250);
        as.addAnimation(a1);
        if (isRotate) {
            RotateAnimation a2 = new RotateAnimation(0f, r, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
            as.addAnimation(a2);
            a2.setDuration(400);

            RotateAnimation a3 = new RotateAnimation(0f, -r, Animation.RELATIVE_TO_SELF,
                    0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            a3.setFillAfter(true);
            a3.setStartOffset(50);//执行前的等待时间
            a3.setDuration(400);
            a3.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    view.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    showButton();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(a3);
        }
//        as.setFillAfter(true);
        as.setStartOffset(50);//执行前的等待时间
        view2.startAnimation(as);
    }

    public void showButton() {
        Button btn_add_sell = (Button) findViewById(R.id.tab_add_btn_sell2);
        Button btn_all_request = (Button) findViewById(R.id.tab_add_btn_show2);
        btn_add_sell.setVisibility(View.VISIBLE);
        btn_all_request.setVisibility(View.VISIBLE);
        btn_add_sell.setOnClickListener(this);
        btn_all_request.setOnClickListener(this);
    }
}
