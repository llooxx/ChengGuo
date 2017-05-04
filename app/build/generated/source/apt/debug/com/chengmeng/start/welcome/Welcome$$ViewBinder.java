// Generated code from Butter Knife. Do not modify!
package com.chengmeng.start.welcome;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class Welcome$$ViewBinder<T extends com.chengmeng.start.welcome.Welcome> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428686, "field 'img0'");
    target.img0 = finder.castView(view, 2131428686, "field 'img0'");
    view = finder.findRequiredView(source, 2131428687, "field 'img1'");
    target.img1 = finder.castView(view, 2131428687, "field 'img1'");
  }

  @Override public void unbind(T target) {
    target.img0 = null;
    target.img1 = null;
  }
}
