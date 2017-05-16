package com.chengmeng.sell.main.bottom;

import com.chengmeng.tools.all.StaticMethod;
import com.chengmeng.tools.connect.ConnectDialog;
import com.chengmeng.tools.connect.ConnectList;
import com.chengmeng.tools.connect.ConnectListener;
import com.chengmeng.tools.connect.ServerURL;
import com.chengmeng.tools.map.LocationListener;

import org.json.JSONException;
import org.json.JSONObject;

public class SellMainFragmentRound extends SellMainFragmentBase {

    public SellMainFragmentRound() {
    }

    @Override
    public void onListLoad() {
        updateDataFromNet();
    }

    @Override
    public void onListRefresh() {
        page = -1;
        updateDataFromNet();
    }

    @Override
    public boolean onLazyLoad() {
        page = -1;
        updateDataFromNet();
        return true;
    }

    private void updateDataFromNet() {
        StaticMethod.Location(getActivity(), new LocationListener() {
            @Override
            public void locationRespose(String locationName, double x, double y, float limit) {
                JSONObject json = new JSONObject();
                try {
                    json.put("location_x", x + "");
                    json.put("location_y", y + "");
                    json.put("page", page + "");
                    json.put("category", index + "");
                } catch (JSONException e) {
                }
                final String jsonObj = json.toString();
                StaticMethod.POST(getActivity(), ServerURL.BUSINESS_MAIN_ROUND, new ConnectListener() {
                    @Override
                    public ConnectList setParam(ConnectList list) {
                        list.put("jsonObj", jsonObj);
                        return list;
                    }

                    @Override
                    public ConnectDialog showDialog(ConnectDialog dialog) {
                        return null;
                    }

                    @Override
                    public void onResponse(String response) {
                        dealResponse(response);
                        if (item_list != null && item_list.size() != 0)
                            page = item_list.get(item_list.size() - 1).getId();
                    }
                });
            }
        });
    }

}
