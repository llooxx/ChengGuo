// Generated code from Butter Knife. Do not modify!
package com.chengmeng.sell.discuss;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DiscussAdapter$TopViewHolder$$ViewBinder<T extends com.chengmeng.sell.discuss.DiscussAdapter.TopViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428431, "field 'sellDiscussTopTitle'");
    target.sellDiscussTopTitle = finder.castView(view, 2131428431, "field 'sellDiscussTopTitle'");
    view = finder.findRequiredView(source, 2131428432, "field 'sellDiscussTopText'");
    target.sellDiscussTopText = finder.castView(view, 2131428432, "field 'sellDiscussTopText'");
  }

  @Override public void unbind(T target) {
    target.sellDiscussTopTitle = null;
    target.sellDiscussTopText = null;
  }
}
