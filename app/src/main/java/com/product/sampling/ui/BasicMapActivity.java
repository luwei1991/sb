package com.product.sampling.ui;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RidePath;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.product.sampling.R;
import com.product.sampling.bean.TaskEntity;
import com.product.sampling.maputil.AMapUtil;
import com.product.sampling.maputil.ToastUtil;
import com.product.sampling.overlay.DrivingRouteOverlay;
import com.product.sampling.overlay.RideRouteOverlay;
import com.product.sampling.utils.GdLocationUtil;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * AMapV1地图中介绍如何显示世界图
 */
public class BasicMapActivity extends BaseActivity implements OnClickListener, AMapLocationListener, RouteSearch.OnRouteSearchListener, GeocodeSearch.OnGeocodeSearchListener {

    private static final int ROUTE_TYPE_WALK = 3;
    private final int ROUTE_TYPE_RIDE = 4;
    private final int ROUTE_TYPE_DRIVE = 2;

    private MapView mapView;
    private AMap aMap;

    TextView tvLocation;
    TextView tvDestination;
    TextView tvCostTime;

    private RadioGroup mRadioGroup;
    // 起点终点坐标
    private LatLonPoint startPoint = null;
    private LatLonPoint endPoint = null;

    private RouteSearch mRouteSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.basicmap_activity);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
//        setCurrentLocationDetails();
        TaskEntity taskEntity = (TaskEntity) getIntent().getSerializableExtra("task");
        getLatlon(taskEntity.companyaddress);
        //获取权限定位（如果没有开启权限，会弹出对话框，询问是否开启权限）
        requestLocation(this);
    }

    private void setCurrentLocationDetails() {
        // 地址逆解析
        GeocodeSearch geocoderSearch = new GeocodeSearch(getApplicationContext());
        geocoderSearch.setOnGeocodeSearchListener(this);
        // 第一个参数表示一个Latlng(经纬度)，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(endPoint, 500, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);

    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();

        }
        tvLocation = findViewById(R.id.tv_location);
        tvDestination = findViewById(R.id.tv_destination);
        tvCostTime = findViewById(R.id.tv_cost_time);

        mRadioGroup = findViewById(R.id.check_trip);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_walk) {
                    calculateFootRoute(ROUTE_TYPE_WALK);
                } else if (checkedId == R.id.radio_diver) {
                    calculateDriveRoute(ROUTE_TYPE_DRIVE);
                } else if (checkedId == R.id.radio_bus) {
                    calculateBusRoute();
                } else if (checkedId == R.id.radio_ride) {
                    calculateRadeRoute(ROUTE_TYPE_RIDE);
                }
            }
        });
        mRouteSearch = new RouteSearch(this);
        mRouteSearch.setRouteSearchListener(this);
        registerListener();
    }

    /**
     * 注册监听
     */
    private void registerListener() {
//        aMap.setOnMapClickListener(this);
//        aMap.setOnMarkerClickListener(WalkRouteActivity.this);
//        aMap.setOnInfoWindowClickListener(WalkRouteActivity.this);
//        aMap.setInfoWindowAdapter(WalkRouteActivity.this);

    }

    private void setfromandtoMarker() {
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(startPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(endPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)));
    }

    private void calculateBusRoute() {
        BusPath busPath = new BusPath();
        BusRouteOverlay mBusrouteOverlay = new BusRouteOverlay(this, aMap,
                busPath, startPoint,
                endPoint);
        mBusrouteOverlay.removeFromMap();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                startPoint = AMapUtil.convertToLatLonPoint(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
                tvLocation.setText(amapLocation.getAddress());
                Log.e("amapLocation", amapLocation.toString());

                MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
                myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
                aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
                aMap.setMyLocationEnabled(true);
                zoomToSpan();
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
                if (amapLocation.getErrorCode() == 12) {
                    Toast.makeText(BasicMapActivity.this, "缺少定位权限,请到设置->安全和隐私->定位服务,打开允许访问我的位置信息", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    //计算驾车路线
    private void calculateDriveRoute(int routeType) {
        showProgressDialog("正在搜索");
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DrivingDefault, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        }
    }

    //计算步行路线
    private void calculateFootRoute(int routeType) {
        showProgressDialog("正在搜索");
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    //计算骑行路线
    private void calculateRadeRoute(int routeType) {
        showProgressDialog("正在搜索");
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                startPoint, endPoint);
        if (routeType == ROUTE_TYPE_RIDE) {// 骑行路径规划
            RouteSearch.RideRouteQuery query = new RouteSearch.RideRouteQuery(fromAndTo, RouteSearch.RidingDefault);
            mRouteSearch.calculateRideRouteAsyn(query);// 异步路径规划骑行模式查询
        }
    }

//------------------生命周期重写函数---------------------------


    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    DriveRouteResult mDriveRouteResult;

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            BasicMapActivity.this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    tvCostTime.setText(des + ",打车约" + taxiCost + "元");
                } else if (result != null && result.getPaths() == null) {
                    showShortToast("无数据");
                }

            } else {
                showShortToast("无数据");
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }

    }

    WalkRouteResult mWalkRouteResult;

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            setfromandtoMarker();
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;

                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            this, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    tvCostTime.setText(des);
                } else if (result != null && result.getPaths() == null) {
                    showShortToast("无数据");
                }
            } else {
                showShortToast("无数据");
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    RideRouteResult mRideRouteResult;

    @Override
    public void onRideRouteSearched(RideRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mRideRouteResult = result;
                    final RidePath ridePath = mRideRouteResult.getPaths()
                            .get(0);
                    RideRouteOverlay rideRouteOverlay = new RideRouteOverlay(
                            this, aMap, ridePath,
                            mRideRouteResult.getStartPos(),
                            mRideRouteResult.getTargetPos());
                    rideRouteOverlay.removeFromMap();
                    rideRouteOverlay.addToMap();
                    rideRouteOverlay.zoomToSpan();
                    int dis = (int) ridePath.getDistance();
                    int dur = (int) ridePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    tvCostTime.setText(des);
                } else if (result != null && result.getPaths() == null) {
                    showShortToast("无数据");
                }
            } else {
                showShortToast("无数据");
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), errorCode);
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int i) {
        String formatAddress = result.getRegeocodeAddress().getFormatAddress();
        tvDestination.setText(formatAddress);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getGeocodeAddressList() != null && result.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = result.getGeocodeAddressList().get(0);
                if (address != null) {
                    endPoint = address.getLatLonPoint();
                    aMap.addMarker(new MarkerOptions()
                            .position(AMapUtil.convertToLatLng(endPoint))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.amap_end)));
                    zoomToSpan();
                }
            } else {
                ToastUtil.show(BasicMapActivity.this, "无法获取目的地坐标");
            }
        } else {
            ToastUtil.showerror(BasicMapActivity.this, rCode);
        }
    }

    private void zoomToSpan() {
        if (startPoint != null && endPoint != null) {
            if (aMap == null) {
                return;
            }
            try {
                LatLngBounds.Builder b = LatLngBounds.builder();
                b.include(new LatLng(startPoint.getLatitude(), startPoint.getLongitude()));
                b.include(new LatLng(endPoint.getLatitude(), endPoint.getLongitude()));
                LatLngBounds bounds = b.build();
                aMap.animateCamera(CameraUpdateFactory
                        .newLatLngBounds(bounds, 100));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        GdLocationUtil.getInstance().stopLoaction();
    }

    public void getLatlon(String name) {
        tvDestination.setText(name);
        GeocodeSearch geocoderSearch = new GeocodeSearch(this);
        GeocodeQuery query = new GeocodeQuery(name, "");// 第一个参数表示地址，第二个参数表示查询城市，中文或者中文全拼，citycode、adcode， 
        geocoderSearch.getFromLocationNameAsyn(query);// 设置同步地理编码请求 
        geocoderSearch.setOnGeocodeSearchListener(this);
    }
}