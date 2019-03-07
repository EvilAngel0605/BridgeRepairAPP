package net.jsrbc.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import net.jsrbc.activity.bridge.BridgeActivity;
import net.jsrbc.activity.bridge.BridgeListActivity;
import net.jsrbc.activity.bridge.BridgeUploadActivity;
import net.jsrbc.activity.system.SettingActivity;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.event.OnClose;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.pojo.User;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.utils.clusterutil.clustering.Cluster;
import net.jsrbc.utils.clusterutil.clustering.ClusterItem;
import net.jsrbc.utils.clusterutil.clustering.ClusterManager;

import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.enumeration.TaskType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.R;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnNavigationItemSelected;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.utils.overlayutil.DrivingRouteOverlay;

import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_main)
@AndroidMenu(R.menu.search)
public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        BaiduMap.OnMapLoadedCallback, ClusterManager.OnClusterItemClickListener<MainActivity.BridgeClusterItem>,
        ClusterManager.OnClusterClickListener<MainActivity.BridgeClusterItem>, BaiduMap.OnMapClickListener, OnGetRoutePlanResultListener, SearchView.OnQueryTextListener, SearchView.OnCloseListener {

    @AndroidView(R.id.toolbar)
    private Toolbar toolbar;

    @AndroidView(R.id.drawer_layout)
    private DrawerLayout drawer;

    @AndroidView(R.id.nav_view)
    @OnNavigationItemSelected
    private NavigationView navigationView;

    @AndroidView(R.id.tv_frequency)
    private TextView frequencyText;

    @AndroidView(R.id.ll_frequency)
    @OnClick
    private LinearLayout frequencyBtn;

    @AndroidView(R.id.tv_regular)
    private TextView regularText;

    @AndroidView(R.id.ll_regular)
    @OnClick
    private LinearLayout regularBtn;

    @AndroidView(R.id.tv_special)
    private TextView specialText;

    @AndroidView(R.id.ll_special)
    @OnClick
    private LinearLayout specialBtn;

    @AndroidView(R.id.map_view)
    private MapView mapView;

    @AndroidView(R.id.ib_refresh)
    @OnClick
    private ImageButton refreshBridgeBtn;

    @AndroidView(R.id.ib_location)
    @OnClick
    private ImageButton toMyLocationBtn;

    @AndroidView(R.id.ib_route)
    @OnClick
    private ImageButton routePlanBtn;

    @AndroidView(R.id.cv_bridge_info)
    private CardView bridgeInfoView;

    @AndroidView(R.id.tv_bridge_name)
    private TextView bridgeNameView;

    @AndroidView(R.id.tv_bridgeCategory)
    private TextView bridgeCategoryView;

    @AndroidView(R.id.tv_build_date)
    private TextView bridgeBuildDateView;

    @AndroidView(R.id.tv_evaluation_level)
    private TextView bridgeEvaluationLevelView;

    @AndroidView(R.id.btn_inspection)
    @OnClick
    private Button inspectionBtn;

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    @OnClose
    private SearchView searchView;

    @Mapper
    private BridgeMapper bridgeMapper;

    /** 百度地图 */
    private BaiduMap baiduMap;

    /** 路径规划 */
    private RoutePlanSearch routePlanSearch;

    /** 路径规划覆盖物 */
    private DrivingRouteOverlay drivingRouteOverlay;

    /** 定位客户端 */
    private LocationClient locationClient;

    /** 我的位置 */
    private MyLocationData myLocation;

    /** 聚合点管理 */
    private ClusterManager<BridgeClusterItem> clusterManager;

    /** 上次选中的mark */
    private Marker prevSelectedMark;

    /** 选中的桥梁 */
    private TblBridge selectedBridge;

    /** 是否第一次定位 */
    private boolean isFirstLocate = true;

    /** 当前检查类型 */
    private TaskType currentTaskType;

    /** 过滤桥梁的字符串 */
    private String bridgeFilterStr;

    @Override
    protected void created() {
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        init();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_frequency: {
                setCurrentTab(TaskType.FREQUENCY);
                break;
            }
            case R.id.ll_regular: {
                setCurrentTab(TaskType.REGULAR);
                break;
            }
            case R.id.ll_special: {
                setCurrentTab(TaskType.SPECIAL);
                break;
            }
            case R.id.ib_location: {
                toMyLocation();
                break;
            }
            case R.id.ib_refresh: {
                if (currentTaskType != null) {
                    drawBridgeMap(currentTaskType);
                    //开始动画
                    ObjectAnimator animator = ObjectAnimator.ofFloat(refreshBridgeBtn, "rotation", 0, 720);
                    animator.setDuration(3000);
                    animator.start();
                }
                break;
            }
            case R.id.ib_route: {
                //规划路径
                if (myLocation == null) {
                    SystemUtils.shortToast(this, "定位失败，请检查GPS是否开启");
                } else if (prevSelectedMark == null) {
                    SystemUtils.shortToast(this, "请选择目的地桥梁");
                } else {
                    showProgressDialog();
                    routePlanSearch.drivingSearch(new DrivingRoutePlanOption()
                            .from(PlanNode.withLocation(new LatLng(myLocation.latitude, myLocation.longitude)))
                            .to(PlanNode.withLocation(prevSelectedMark.getPosition())));
                }
                break;
            }
            case R.id.btn_inspection: {
                Intent intent = new Intent(this, BridgeActivity.class);
                intent.putExtra(BridgeListActivity.CURRENT_BRIDGE, JsonUtils.toJson(selectedBridge));
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    public boolean onClusterClick(Cluster<BridgeClusterItem> cluster) {
        toLocation(cluster.getPosition());
        return false;
    }

    @Override
    public boolean onClusterItemClick(BridgeClusterItem item, Marker marker) {
        if (prevSelectedMark != null) setMarkerSelected(prevSelectedMark, false);
        setMarkerSelected(marker, true);
        selectedBridge = item.getBridge();
        showBridgeInfo(selectedBridge);
        prevSelectedMark = marker;
        routePlanBtn.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {}

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {}

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {}

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
        if (drivingRouteResult == null || drivingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
            SystemUtils.shortToast(this, "未找到路径规划结果");
        } else {
            if (!drivingRouteResult.getRouteLines().isEmpty()) {
                DrivingRouteLine routeLine = drivingRouteResult.getRouteLines().get(0);
                drivingRouteOverlay = new DrivingRouteOverlay(baiduMap);
                drivingRouteOverlay.setData(routeLine);
                drivingRouteOverlay.addToMap();
                drivingRouteOverlay.zoomToSpan();
            }
        }
        //隐藏进度对话框
        hideProgressDialog();
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {}

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {}

    @Override
    public void onMapClick(LatLng latLng) {
        hideBridgeInfo();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        hideBridgeInfo();
        return false;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        searchView.setQueryHint("搜索...");
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_task:{
                startActivity(new Intent(this, MyTaskActivity.class));
                break;
            }
            case R.id.nav_sync: {
                startActivity(new Intent(this, BridgeUploadActivity.class));
                break;
            }
            case R.id.nav_settings: {
                startActivity(new Intent(this, SettingActivity.class));
                break;
            }
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    public boolean onClose() {
        bridgeFilterStr = null;
        drawBridgeMap(currentTaskType);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        bridgeFilterStr = query;
        drawBridgeMap(currentTaskType);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    /** 设置选中的tab */
    private void setCurrentTab(TaskType taskType) {
        this.currentTaskType = taskType;
        //先清空选中状态
        frequencyText.setBackground(null);
        regularText.setBackground(null);
        specialText.setBackground(null);
        //设置选中项
        switch (taskType) {
            case FREQUENCY:
                frequencyText.setBackground(getResources().getDrawable(R.drawable.shape_round_rect_red, null));
                break;
            case REGULAR:
                regularText.setBackground(getResources().getDrawable(R.drawable.shape_round_rect_red, null));
                break;
            case SPECIAL:
                specialText.setBackground(getResources().getDrawable(R.drawable.shape_round_rect_red, null));
                break;
        }
        //获取并在地图上绘制桥梁
        toMyLocation();
        drawBridgeMap(taskType);
    }

    /** 在地图上绘制出所有的桥梁 */
    private void drawBridgeMap(TaskType taskType) {
        new Thread(()->{
            List<TblBridge> bridgeList = bridgeMapper.getBridgeListByTaskType(taskType.getCode());
            if (bridgeList == null) return;
            //选择地图移动到的桥梁
            if (!StringUtils.isEmpty(bridgeFilterStr)) bridgeList = bridgeList.stream().filter(b->String.format("%s  %s", b.getRoadNo(), b.getName()).contains(bridgeFilterStr)).collect(Collectors.toList());
            TblBridge target = bridgeList.stream().findAny().orElse(null);
            if (target == null) return;
            //获取聚合点集合
            List<BridgeClusterItem> bridgeClusterItemList = bridgeList.stream().map(BridgeClusterItem::new).collect(Collectors.toList());
            //清除旧的覆盖物，同时添加新的覆盖物
            clusterManager.clearItems();
            baiduMap.clear();
            clusterManager.addItems(bridgeClusterItemList);
            //刷新地图
            AndroidConstant.HANDLER.post(()-> baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder()
                    .target(new LatLng(target.getLatitude(), target.getLongitude()))
                    .zoom(10.5F)
                    .build())));
        }).start();
    }

    /** 初始化 */
    private void init() {
        initMap();
        initMyLocation();
        setCurrentTab(TaskType.REGULAR);
    }

    /** 设置用户信息 */
    private void setUserInfo() {
        User user = DataUtils.getCurrentToken(this).getContent();
        View headView = navigationView.getHeaderView(0);
        ((TextView)headView.findViewById(R.id.tv_user_name)).setText(user.getName());
        ((TextView)headView.findViewById(R.id.tv_user_unit)).setText(user.getUnitsName());
    }

    /** 初始化百度地图 */
    private void initMap() {
        //百度地图
        baiduMap = mapView.getMap();
        baiduMap.setOnMapLoadedCallback(this);
        //我的位置
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMyLocationConfiguration(new MyLocationConfiguration(MyLocationConfiguration.LocationMode.COMPASS, true, null));
        //点聚合
        clusterManager = new ClusterManager<>(this, baiduMap);
        baiduMap.setOnMapClickListener(this);
        baiduMap.setOnMapStatusChangeListener(clusterManager);
        baiduMap.setOnMarkerClickListener(clusterManager);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        //路径规划
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(this);
    }

    /** 开启定位系统 */
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

    /** 地图移动到我的位置 */
    private void toMyLocation() {
        if (baiduMap != null && myLocation != null) baiduMap.setMyLocationData(myLocation);
    }

    /** 地图移动至指定地点 */
    private void toLocation(LatLng ll) {
        MapStatus u = new MapStatus.Builder()
                .target(ll)
                .zoom(15F)
                .build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(u));
    }

    /** 设置地图标记的选中状态 */
    private void setMarkerSelected(Marker marker, boolean isSelected) {
        if (isSelected) marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_bridge_selected));
        else marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_bridge));
    }

    /** 展示桥梁信息窗口 */
    private void showBridgeInfo(TblBridge bridge) {
        String bridgeName = String.format("%s  %s  %s", bridge.getRoadNo(), StringUtils.parseStakeNo(bridge.getStakeNo()), bridge.getName());
        bridgeNameView.setText(bridgeName);
        bridgeCategoryView.setText(StringUtils.isEmpty(bridge.getBridgeCategoryName())?"未知":bridge.getBridgeCategoryName());
        bridgeBuildDateView.setText(StringUtils.isEmpty(bridge.getBuildDateStr())?"未知":bridge.getBuildDateStr());
        bridgeEvaluationLevelView.setText(StringUtils.isEmpty(bridge.getBridgeEvaluationLevel())?"未知":bridge.getBridgeEvaluationLevel());
        //动画展示信息窗口
        ObjectAnimator animator = ObjectAnimator.ofFloat(bridgeInfoView, "translationY", -bridgeInfoView.getHeight(), 0);
        animator.setDuration(500);
        animator.start();
    }

    /** 隐藏桥梁信息窗口 */
    private void hideBridgeInfo() {
        if (prevSelectedMark != null) {
            setMarkerSelected(prevSelectedMark, false);
            prevSelectedMark = null;
            //动画隐藏信息窗口
            ObjectAnimator animator = ObjectAnimator.ofFloat(bridgeInfoView, "translationY", 0, -bridgeInfoView.getHeight());
            animator.setDuration(500);
            animator.start();
            //移除路径规划按钮
            routePlanBtn.setVisibility(View.GONE);
            //移除路径规划结果
            if (drivingRouteOverlay != null) {
                drivingRouteOverlay.removeFromMap();
                drivingRouteOverlay = null;
            }
        }
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
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(new LatLng(myLocation.latitude, myLocation.longitude)).zoom(18.0F);
                baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }
        }
    }

    /** 地图点聚合标记 */
    public class BridgeClusterItem implements ClusterItem {

        private TblBridge bridge;

        BridgeClusterItem(TblBridge bridge) {
            this.bridge = bridge;
        }

        @Override
        public LatLng getPosition() {
            return new LatLng(this.bridge.getLatitude(), this.bridge.getLongitude());
        }

        TblBridge getBridge() {
            return this.bridge;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_bridge);
        }
    }

    @Override
    public void onMapLoaded() {
        LatLng ll = new LatLng(32.065553, 118.804334);
        if (myLocation != null) ll = new LatLng(myLocation.latitude, myLocation.longitude);
        MapStatus status = new MapStatus.Builder().target(ll).zoom(18.0F).build();
        baiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(status));
    }

    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mapView.onResume();
        setUserInfo();
        super.onResume();
    }

    /** 返回键按下后返回桌面，不退出 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            SystemUtils.toDesk(this);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        locationClient.stop();
        if (routePlanSearch != null) routePlanSearch.destroy();
        baiduMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        super.onDestroy();
    }
}
