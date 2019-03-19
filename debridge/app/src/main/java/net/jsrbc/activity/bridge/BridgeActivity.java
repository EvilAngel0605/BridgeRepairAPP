package net.jsrbc.activity.bridge;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.UserData.ScanActivity;
import net.jsrbc.activity.disease.OverallSituationActivity;
import net.jsrbc.activity.side.BridgeSideActivity;
import net.jsrbc.activity.system.CameraActivity;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.enumeration.PhotoType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_bridge)
@AndroidMenu(R.menu.activity_bridge_menu)
public class BridgeActivity extends BaseActivity implements View.OnClickListener {

    /** 传给分幅管理Activity的参数KEY */
    public final static String CURRENT_BRIDGE = "CURRENT_BRIDGE";

    /** 当前分幅 */
    public final static String CURRENT_SIDE = "CURRENT_SIDE";

    /** 当前的照片类型 */
    public final static String CURRENT_PHOTO_TYPE = "CURRENT_PHOTO_TYPE";

    @AndroidView(R.id.fab)
    @OnClick
    private FloatingActionButton fab;

    @AndroidView(R.id.iv_bridge_photo)
    private ImageView bridgePhotoView;

    @AndroidView(R.id.tv_no_front_photo)
    private TextView noFrontPhotoView;

    @AndroidView(R.id.tv_no_side_photo)
    private TextView noSidePhotoView;

    @AndroidView(R.id.tv_no_upward_photo)
    private TextView noUpwardPhotoView;

    @AndroidView(R.id.ib_bridge_front_photo)
    private ImageView frontPhotoBtn;

    @AndroidView(R.id.ib_bridge_side_photo)
    private ImageView sidePhotoBtn;

    @AndroidView(R.id.ib_bridge_upward_photo)
    private ImageView upwardPhotoBtn;

    @AndroidView(R.id.rl_bridge_side)
    @OnClick
    private RelativeLayout bridgeSideContent;

    @AndroidView(R.id.tv_bridge_side)
    private TextView bridgeSideView;

    @AndroidView(R.id.tv_bridge_deck_divide)
    private TextView deckDivideView;

    @AndroidView(R.id.tv_road_no)
    private TextView roadNoView;

    @AndroidView(R.id.tv_road_name)
    private TextView roadNameView;

    @AndroidView(R.id.tv_road_level)
    private TextView roadLevelView;

    @AndroidView(R.id.tv_address)
    private TextView addressView;

    @AndroidView(R.id.tv_units_name)
    private TextView unitsNameView;

    @AndroidView(R.id.tv_bridge_category)
    private TextView bridgeCategoryView;

    @AndroidView(R.id.tv_bridge_build_date)
    private TextView buildDateView;

    @AndroidView(R.id.tv_bridge_level)
    private TextView bridgeLevelView;

    @AndroidView(R.id.tv_inspection_date)
    private TextView inspectionDate;

    @AndroidView(R.id.tv_overall_situation)
    @OnClick
    private TextView overallSituationBtn;

    @AndroidView(R.id.tv_disease)
    @OnClick
    private TextView diseaseBtn;

    @AndroidActionView(R.id.mi_navigate)
    private MenuItem navigateMenuItem;

    @Mapper
    private BridgeMapper bridgeMapper;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    /** 当前桥梁对象 */
    private TblBridge currentBridge;

    /** 当前所有分幅 */
    private List<TblBridgeSide> currentSideList;

    /** 当前拍照的类型 */
    private PhotoType currentPhotoType;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentBridge = JsonUtils.fromJson(getIntent().getStringExtra(BridgeListActivity.CURRENT_BRIDGE), TblBridge.class);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentBridge.getName());
        setBridgeInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentBridge = bridgeMapper.getBridgeById(currentBridge.getId());
        currentSideList = bridgeSideMapper.getBridgeSideListByBridgeId(currentBridge.getId());
        setBridgeInfo();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_bridge_side: {
                Intent intent = new Intent(BridgeActivity.this, BridgeSideActivity.class);
                intent.putExtra(CURRENT_BRIDGE, currentBridge);
                startActivity(intent);
                break;
            }
            case R.id.fab: {
                PopupMenu popupMenu = new PopupMenu(this, fab);
                getMenuInflater().inflate(R.menu.photo_type, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item->{
                    switch (item.getItemId()) {
                        case R.id.mi_front:
                            currentPhotoType = PhotoType.FRONT;
                            break;
                        case R.id.mi_side:
                            currentPhotoType = PhotoType.SIDE;
                            break;
                        case R.id.mi_upward:
                            currentPhotoType = PhotoType.UPWARD;
                            break;
                    }
                    Intent intent = new Intent(BridgeActivity.this, CameraActivity.class);
                    intent.putExtra(CameraActivity.PREVIEW_ACTIVITY, BridgePreviewActivity.class);
                    intent.putExtra(CURRENT_BRIDGE, currentBridge);
                    intent.putExtra(CURRENT_PHOTO_TYPE, currentPhotoType);
                    startActivity(intent);
                    return true;
                });
                popupMenu.show();
                break;
            }
            case R.id.tv_overall_situation: {
                if (ListUtils.isEmpty(currentSideList)) {
                    SystemUtils.prompt(this, "该桥尚未添加分幅，请在分幅管理中添加");
                }else {
                    SystemUtils.choose(this, currentSideList.stream().map(TblBridgeSide::getSideTypeName).collect(Collectors.toList()), (d, i) -> {
                        Intent intent = new Intent(BridgeActivity.this, OverallSituationActivity.class);
                        intent.putExtra(CURRENT_SIDE, currentSideList.get(i));
                        startActivity(intent);
                    });
                }
                break;
            }
            case R.id.tv_disease: {
                if (ListUtils.isEmpty(currentSideList)) {
                    SystemUtils.prompt(this, "该桥尚未添加分幅，请在分幅管理中添加");
                }else {
                    /*SystemUtils.choose(this, currentSideList.stream().map(TblBridgeSide::getSideTypeName).collect(Collectors.toList()), (d, i) -> {
                        Intent intent = new Intent(BridgeActivity.this, BridgeDiseaseActivity.class);
                        intent.putExtra(CURRENT_SIDE, currentSideList.get(i));
                        startActivity(intent);
                    });
                    */
                    Intent intent = new Intent(BridgeActivity.this, ScanActivity.class);
                    startActivity(intent);
                    //BridgeActivity.this.finish();

                }
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_navigate: {
                Intent intent = new Intent(this, BridgeMapActivity.class);
                intent.putExtra(CURRENT_BRIDGE, currentBridge);
                startActivity(intent);
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** 设置桥梁信息 */
    private void setBridgeInfo() {
        setHeaderBridgePhoto();
        setBridgePhoto(frontPhotoBtn, noFrontPhotoView, currentBridge.getFrontPath());
        setBridgePhoto(sidePhotoBtn, noSidePhotoView, currentBridge.getSidePath());
        setBridgePhoto(upwardPhotoBtn, noUpwardPhotoView, currentBridge.getUpwardPath());
        getBridgeInfoMap().forEach((k, v)->k.setText(StringUtils.isEmpty(v)?"未知":v));
        //设置分幅信息
        bridgeSideView.setText(String.format("本桥共分为%s幅", currentBridge.getSideCount()));
    }

    /** 设置桥梁照片，没照片则显示无照片的提示 */
    private void setBridgePhoto(ImageView photoView, TextView noPhotoView, String filePath) {
        if (FileUtils.exists(filePath)) {
            photoView.setVisibility(View.VISIBLE);
            photoView.setImageBitmap(ImageUtils.createThumbnailBitmap(filePath));
        } else {
            noPhotoView.setVisibility(View.VISIBLE);
        }
    }

    /** 设置头部显示的桥梁照片 */
    private void setHeaderBridgePhoto() {
        if (FileUtils.exists(currentBridge.getSidePath())) {
            bridgePhotoView.setImageBitmap(ImageUtils.createCompressedBitmap(currentBridge.getSidePath()));
        } else if (FileUtils.exists(currentBridge.getFrontPath())) {
            bridgePhotoView.setImageBitmap(ImageUtils.createCompressedBitmap(currentBridge.getFrontPath()));
        } else if (FileUtils.exists(currentBridge.getUpwardPath())) {
            bridgePhotoView.setImageBitmap(ImageUtils.createCompressedBitmap(currentBridge.getUpwardPath()));
        }
    }

    /** 获取视图与对应属性的关联对象 */
    private LinkedHashMap<TextView, String> getBridgeInfoMap() {
        LinkedHashMap<TextView, String> map = new LinkedHashMap<>();
        map.put(deckDivideView, currentBridge.getDeckDivideTypeName());
        map.put(roadNoView, currentBridge.getRoadNo());
        map.put(roadNameView, currentBridge.getRoadName());
        map.put(roadLevelView, currentBridge.getRoadLevelName());
        map.put(addressView, currentBridge.getFullAddressName());
        map.put(unitsNameView, currentBridge.getUnitsName());
        map.put(bridgeCategoryView, currentBridge.getBridgeCategoryName());
        map.put(buildDateView, currentBridge.getBuildDateStr());
        map.put(bridgeLevelView, currentBridge.getBridgeEvaluationLevel());
        map.put(inspectionDate, currentBridge.getInspectionDate());
        return map;
    }
}
