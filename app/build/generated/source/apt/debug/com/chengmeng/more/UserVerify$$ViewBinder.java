// Generated code from Butter Knife. Do not modify!
package com.chengmeng.more;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class UserVerify$$ViewBinder<T extends com.chengmeng.more.UserVerify> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131493584, "field 'moreUserVerifyIdStatus'");
    target.moreUserVerifyIdStatus = finder.castView(view, 2131493584, "field 'moreUserVerifyIdStatus'");
    view = finder.findRequiredView(source, 2131493586, "field 'moreUserVerifyStuStatus'");
    target.moreUserVerifyStuStatus = finder.castView(view, 2131493586, "field 'moreUserVerifyStuStatus'");
    view = finder.findRequiredView(source, 2131493583, "method 'onClick'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onClick(p0);
        }
      });
    view = finder.findRequiredView(source, 2131493585, "method 'onClick'");
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
