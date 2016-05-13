package wang.shenglifei.jeep.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.DistanceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import wang.shenglifei.jeep.R;
import wang.shenglifei.jeep.activitys.ResultActivity;
import wang.shenglifei.jeep.helper.Helper;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaiduMapFragment extends Fragment {


    ImageButton location_btn = null;
    ImageButton route_btn = null;
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    private LocationClient locationClient = null;
    ImageButton search_btn = null;

    PoiSearch mPoiSearch = null;
    Overlay polyline = null;
    ArrayList<PoiInfo> pois = new ArrayList<>();

    //ArrayList<PoiInfo> pois = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_baidu_map, container, false);
        location_btn = (ImageButton)v.findViewById(R.id.location_btn);
        location_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    location_btn.setBackgroundColor(Color.GRAY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    location_btn.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });
        //定位按钮点击事件
        location_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationClient.requestLocation();
            }
        });

        route_btn = (ImageButton)v.findViewById(R.id.route_btn);
        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<PoiInfo> arrayList = new ArrayList<PoiInfo>();
                String[] items = new String[pois.size()];
                boolean[] itemsCheck = new boolean[pois.size()];
                for (int i = 0; i < pois.size(); i++) {
                    PoiInfo poi = pois.get(i);
                    items[i] = poi.name;
                    itemsCheck[i] = true;
                    arrayList.add(poi);
                }
                final ArrayList<PoiInfo> checksArray = new ArrayList<PoiInfo>(arrayList);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("路径推荐");
                builder.setMultiChoiceItems(
                        items, itemsCheck, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (!isChecked) {
                                    checksArray.set(which, null);
                                } else {
                                    checksArray.set(which, arrayList.get(which));
                                }
                            }
                        });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override

                    public void onClick(DialogInterface dialog, int which) {
                        PolylineOptions options = new PolylineOptions();
                        ArrayList<LatLng> pts = new ArrayList<LatLng>();
                        ArrayList<LatLng> u = new ArrayList<LatLng>();
                        if (polyline != null)
                            polyline.remove();

                        pts.add(locLat);

                        for (int i = 0; i < checksArray.size(); i++) {
                            PoiInfo poi = checksArray.get(i);
                            if (poi != null ) {
                                u.add(poi.location);
                            }
                        }
                        //寻找最短路径 贪心算法
                        while(!u.isEmpty()) {
                            LatLng latLng = pts.get(pts.size() - 1);
                            Double minDistance = Double.POSITIVE_INFINITY;
                            int index = 0;
                            for (int i = 0; i < u.size(); i++) {
                                Double distance = DistanceUtil.getDistance(latLng,u.get(i));
                                if (distance < minDistance) {
                                    minDistance = distance;
                                    index = i;
                                }
                            }
                            pts.add(u.get(index));
                            u.remove(index);
                        }

                        if (pts.size() < 2)
                            return;
                        options.points(pts);
                        options.color(Color.RED);
                        options.color(Color.RED);
                        polyline = mBaiduMap.addOverlay(options);
                    }
                });
                builder.setNegativeButton("取消", null).show();
            }
        });
        Overlay polyline = null;
        mMapView = (MapView) v.findViewById(R.id.bmapView);
        mMapView.showScaleControl(true);
        mMapView.showZoomControls(true);
        mBaiduMap = mMapView.getMap();
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        //获取是否允许楼块效果
        mBaiduMap.setBuildingsEnabled(true);

        locationClient = new LocationClient(getActivity().getApplication().getApplicationContext());

        //配置定位SDK参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); //打开GPRS
        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
        locationClient.setLocOption(option);
        //注册位置监听
        locationClient.registerLocationListener(myListener);
        if (locationClient != null && !locationClient.isStarted()) {
            locationClient.requestLocation();
            locationClient.start();
        } else {
            Log.e("LocSDK", "locClient is null or not started");
        }
        //搜索View
        final SearchView searchView = (SearchView)v.findViewById(R.id.search_view);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("岳麓书院");
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.onActionViewExpanded();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    if (!query.equals("岳麓书院"))  {
                        Toast.makeText(getActivity().getApplication().getApplicationContext(),
                                "搜索内容还没有！", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ResultActivity.class);
                    intent.putExtra(Helper.result, query);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        search_btn = (ImageButton)v.findViewById(R.id.search_btn);
        search_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    search_btn.setBackgroundColor(Color.GRAY);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    search_btn.setBackgroundColor(Color.TRANSPARENT);
                }
                return false;
            }
        });
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoiSearch = PoiSearch.newInstance();
                mPoiSearch.setOnGetPoiSearchResultListener(poiListener);
                //检索周围10000m内的景点
                mPoiSearch.searchNearby((new PoiNearbySearchOption())
                        .location(locLat).keyword("景点").radius(Helper.raduis)
                        .pageCapacity(Helper.number));
            }
        });
        return v;
    }

    private OnGetPoiSearchResultListener poiListener = new OnGetPoiSearchResultListener() {
        @Override
        public void onGetPoiResult(PoiResult poiResult) {
            if (poiResult == null || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                Toast.makeText(getActivity(), "未找到结果", Toast.LENGTH_LONG).show();
                return;
            }
            if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {
                MyPoiOverlay poiOverlay = new MyPoiOverlay(mBaiduMap);
                poiOverlay.setData(poiResult);
                mBaiduMap.setOnMarkerClickListener(poiOverlay);
                poiOverlay.addToMap();
                poiOverlay.zoomToSpan();
            }
        }

        @Override
        public void onGetPoiDetailResult(PoiDetailResult poiDetailResult) {
        }
    };
    private LatLng locLat = null;

    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {

            double lat=location.getLatitude();
            double lon=location.getLongitude();
            LatLng latLng = new LatLng(lat, lon);
            locLat = latLng;
            MapStatus mapStatus = new MapStatus.Builder().zoom(18.0f).target(latLng).build();
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);

            MyLocationData locData = new MyLocationData.Builder().accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).latitude(lat).longitude(lon).build();

            // 设置定位数据
            mBaiduMap.setMyLocationData(locData);
            mBaiduMap.animateMapStatus(mapStatusUpdate, 500);


        }
    };

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        mMapView = null;
        mPoiSearch.destroy();
        super.onDestroy();

    }

    class MyPoiOverlay extends PoiOverlay {
        public MyPoiOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public boolean onPoiClick(int i) {
            super.onPoiClick(i);
            final PoiInfo poiInfo = getPoiResult().getAllPoi().get(i);
            //检索poi详细信息
            new AlertDialog.Builder(getActivity()).setTitle(poiInfo.name)
                    .setMessage("对该景点是否感兴趣？")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(!pois.contains(poiInfo))
                                pois.add(poiInfo);
                            Toast.makeText(getActivity().getApplication().getApplicationContext(),"" + pois.size(),Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();

            return true;
        }
    }

}
