package net.jsrbc.activity.bridge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNCommonSettingParam;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

@AndroidActivity(R.layout.activity_bridge_map)
@AndroidMenu(R.menu.empty)
public class BridgeMapActivity extends BaseActivity
        implements BaiduMap.OnMapLoadedCallback, View.OnClickListener, BaiduMap.OnMapClickListener{

    @AndroidView(R.id.map_view)
    private MapView mapView;

    @AndroidView(R.id.ib_my_location)
    @OnClick
    private ImageButton myLocationBtn;

    @AndroidView(R.id.ib_bridge_location)
    @OnClick
    private ImageButton bridgeLocationBtn;

    @AndroidView(R.id.ib_set_location)
    @OnClick
    private ImageButton setLocationBtn;

    @AndroidView(R.id.ib_cancel)
    @OnClick
    private ImageButton cancelBtn;

    @AndroidView(R.id.btn_nav)
    @OnClick
    private Button navBtn;

    @Mapper
    private BridgeMapper bridgeMapper;

    private TblBridge currentBridge;

    private BaiduMap baiduMap;

    private LocationClient locationClient;

    private MyLocationData myLocation;

    private LatLng bridgeLocation;

    private boolean isFirstLocate = true;

    private boolean changeBridgeLocation = false;

    /** 导航引擎是否初始化成功 */
    private boolean hasInitSuccess = false;

    @Override
    protected void created() {
        currentBridge = (TblBridge) getIntent().getSerializableExtra(BridgeActivity.CURRENT_BRIDGE);
        setDefaultToolbar();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(String.format("%s  %s  %s", currentBridge.getRoadNo(), StringUtils.parseStakeNo(currentBridge.getStakeNo()), currentBridge.getName()));
        initMap();
        initNav();
        initMyLocation();
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_my_location: {
                toMyLocation();
                break;
            }
            case R.id.ib_bridge_location: {
                toLocation(bridgeLocation);
                break;
            }
            case R.id.ib_set_location: {
                if (!changeBridgeLocation) {
                    SystemUtils.prompt(this, "请现在地图上点击选择桥梁位置");
                } else {
                    currentBridge.setLatitude(bridgeLocation.latitude);
                    currentBridge.setLongitude(bridgeLocation.longitude);
                    currentBridge.setUploadLocation(UploadStatus.NEED_UPLOAD.getCode());
                    bridgeMapper.updateBridgeLocation(currentBridge);
                    baiduMap.clear();
                    addOverlay(bridgeLocation, R.mipmap.ic_marker_bridge_selected);
                    toLocation(bridgeLocation);
                    //隐藏保存按钮
                    changeBridgeLocation = false;
                    toggleBridgeLocationChangeBtn();
                }
                break;
            }
            case R.id.ib_cancel: {
                baiduMap.clear();
                bridgeLocation = new LatLng(currentBridge.getLatitude(), currentBridge.getLongitude());
                addOverlay(bridgeLocation, R.mipmap.ic_marker_bridge_selected);
                toLocation(bridgeLocation);
                changeBridgeLocation = false;
                toggleBridgeLocationChangeBtn();
                break;
            }
            case R.id.btn_nav: {
                if (BaiduNaviManager.isNaviInited()) routePlanToNavi();
                break;
            }
        }
    }

    @Override
    public void onMapLoaded() {
        bridgeLocation = new LatLng(currentBridge.getLatitude(), currentBridge.getLongitude());
        MapStatus status = new MapStatus.Builder().target(bridgeLocation).zoom(18.0F).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(status));
        addOverlay(bridgeLocation, R.mipmap.ic_marker_bridge_selected);
    }

    @Override
    public void onMapClick(LatLng ll) {
        this.bridgeLocation = ll;
        baiduMap.clear();
        addOverlay(ll, R.mipmap.ic_marker_bridge);
        //显示保存按钮
        this.changeBridgeLocation = true;
        toggleBridgeLocationChangeBtn();
    }

    /** 是否显示变更桥梁位置按钮 */
    private void toggleBridgeLocationChangeBtn() {
        if (changeBridgeLocation) {
            setLocationBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {
            setLocationBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }
    }

    /** 添加标签 */
    private void addOverlay(LatLng ll, int resId) {
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(resId);
        MarkerOptions options = new MarkerOptions()
                .position(ll)
                .icon(descriptor)
                .zIndex(9)
                .animateType(MarkerOptions.MarkerAnimateType.grow);
        baiduMap.addOverlay(options);
    }

    private void initMap() {
        baiduMap = mapView.getMap();
        baiduMap.setOnMapLoadedCallback(this);
        baiduMap.setOnMapClickListener(this);
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null));
    }

    /** 地图移动到我的位置 */
    private void toMyLocation() {
        if (baiduMap != null && myLocation != null) {
            baiduMap.setMyLocationData(myLocation);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(new LatLng(myLocation.latitude, myLocation.longitude));
            baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        }
    }

    /** 跳转到指定地点 */
    private void toLocation(LatLng ll) {
        MapStatus u = new MapStatus.Builder().target(ll).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(u));
    }

    private void initMyLocation() {
        locationClient = new LocationClient(this);
        locationClient.registerLocationListener(new MyLocationListener());
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setCoorType(CoordType.BD09LL.name());
        //设置定位时间间隔
        option.setScanSpan(LocationClientOption.MIN_SCAN_SPAN);
        locationClient.setLocOption(option);
        locationClient.start();
    }

    /** 定位监听 */
    private class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null) return;
            myLocation = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .build();
            if (isFirstLocate) {
                isFirstLocate = false;
                baiduMap.setMyLocationData(myLocation);
            }
        }
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }

    /** 初始化导航系统 */
    private void initNav() {
        BaiduNaviManager.getInstance().init(this, AndroidConstant.BASE_DIRECTORY, "TEMP", new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (status == 0) {
                    Log.d(BridgeMapActivity.class.getName(), "百度地图API KEY校验成功");
                } else {
                    Log.e(BridgeMapActivity.class.getName(), "百度地图API KEY校验失败");
                }
            }

            @Override
            public void initStart() {
                Log.d(BridgeMapActivity.class.getName(), "百度导航初始化成功");
            }

            @Override
            public void initSuccess() {
                hasInitSuccess = true;
                initNavSetting();
                Log.d(BridgeMapActivity.class.getName(), "百度导航开始初始化");
            }

            @Override
            public void initFailed() {
                SystemUtils.prompt(BridgeMapActivity.this, "百度导航初始化失败");
            }
        }, null, null, null);
    }

    /** 初始化导航设置 */
    private void initNavSetting() {
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
        BNaviSettingManager.setIsAutoQuitWhenArrived(true);
        Bundle bundle = new Bundle();
        bundle.putString(BNCommonSettingParam.TTS_APP_ID, "10453135");
        BNaviSettingManager.setNaviSdkParam(bundle);
    }

    /** 发起导航 */
    private void routePlanToNavi() {
        if (!hasInitSuccess) {
            SystemUtils.prompt(this, "导航引擎尚未初始化，请重启后再试");
        }else {
            showProgressDialog();
            BNRoutePlanNode sNode = new BNRoutePlanNode(myLocation.longitude, myLocation.latitude, "我的位置", null, BNRoutePlanNode.CoordinateType.BD09LL);
            BNRoutePlanNode eNode = new BNRoutePlanNode(bridgeLocation.longitude, bridgeLocation.latitude, "桥梁位置", null, BNRoutePlanNode.CoordinateType.BD09LL);
            List<BNRoutePlanNode> list = new ArrayList<>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(this, list, 1, true, new BridgeRoutePlanListener());
        }
    }

    /** 导航事件监听 */
    private class BridgeRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        @Override
        public void onJumpToNavigator() {
            Intent intent = new Intent(BridgeMapActivity.this, BridgeNavActivity.class);
            startActivity(intent);
            hideProgressDialog();
        }

        @Override
        public void onRoutePlanFailed() {
            hideProgressDialog();
            SystemUtils.prompt(BridgeMapActivity.this,"导航路径规划失败！");
        }
    }
}
