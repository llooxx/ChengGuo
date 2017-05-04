// Generated code from Butter Knife. Do not modify!
package com.chengmeng.sell.discuss;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class DiscussAdapter$ItemViewHolder$$ViewBinder<T extends com.chengmeng.sell.discuss.DiscussAdapter.ItemViewHolder> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131428427, "field 'sellDiscussItemImg'");
    target.sellDiscussItemImg = finder.castView(view, 2131428427, "field 'sellDiscussItemImg'");
    view = finder.findRequiredView(source, 2131428428, "field 'sellDiscussItemNick'");
    target.sellDiscussItemNick = finder.castView(view, 2131428428, "field 'sellDiscussItemNick'");
    view = finder.findRequiredView(source, 2131428429, "field 'sellDiscussItemText'");
    target.sellDiscussItemText = finder.castView(view, 2131428429, "field 'sellDiscussItemText'");
    view = finder.findRequiredView(source, 2131428430, "field 'sellDiscussItemTime'");
    target.sellDiscussItemTime = finder.castView(view, 2131428430, "field 'sellDiscussItemTime'");
  }

  @Override public void unbind(T target) {
    target.sellDiscussItemImg = null;
    target.sellDiscussItemNick = null;
    target.sellDiscussItemText = null;
    target.sellDiscussItemTime = null;
  }
}
