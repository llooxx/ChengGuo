// Generated code from Butter Knife. Do not modify!
package com.szdd.qianxun.more;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class UserVerify$$ViewBinder<T extends com.szdd.qianxun.more.UserVerify> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428050, "field 'moreUserVerifyIdStatus'");
    target.moreUserVerifyIdStatus = finder.castView(view, 2131428050, "field 'moreUserVerifyIdStatus'");
    view = finder.findRequiredView(source, 2131428052, "field 'moreUserVerifyStuStatus'");
    target.moreUserVerifyStuStatus = finder.castView(view, 2131428052, "field 'moreUserVerifyStuStatus'");
    view = finder.findRequiredView(source, 2131428049, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131428051, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
  }

  @Override public void unbind(T target) {
    target.moreUserVerifyIdStatus = null;
    target.moreUserVerifyStuStatus = null;
  }
}
