// Generated code from Butter Knife. Do not modify!
package com.szdd.qianxun.more;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class AboutMe$$ViewBinder<T extends com.szdd.qianxun.more.AboutMe> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428033, "field 'moreVeesion'");
    target.moreVeesion = finder.castView(view, 2131428033, "field 'moreVeesion'");
    view = finder.findRequiredView(source, 2131428035, "field 'moreTime'");
    target.moreTime = finder.castView(view, 2131428035, "field 'moreTime'");
  }

  @Override public void unbind(T target) {
    target.moreVeesion = null;
    target.moreTime = null;
  }
}
