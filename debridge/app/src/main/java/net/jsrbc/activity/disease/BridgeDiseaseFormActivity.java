package net.jsrbc.activity.disease;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.system.CameraActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.entity.TblBridgeMember;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.entity.TblParamDiseaseDesc;
import net.jsrbc.entity.TblParamDiseaseType;
import net.jsrbc.entity.TblParamEvaluationIndex;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.AnimationType;
import net.jsrbc.enumeration.DimensionUnit;
import net.jsrbc.enumeration.DiseaseDescParam;
import net.jsrbc.enumeration.InspectionDirection;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.PositionType;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnItemSelected;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.BridgeDiseaseMapper;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.NameGenerator;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.utils.UnitConverter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.jsrbc.enumeration.DiseaseDescParam.*;

@AndroidActivity(R.layout.activity_bridge_disease_form)
@AndroidMenu(R.menu.empty)
public class BridgeDiseaseFormActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    /** 当前病害 */
    public final static String CURRENT_DISEASE = "CURRENT_DISEASE";

    @AndroidView(R.id.iv_photo_header)
    private ImageView photoHeaderView;

    @AndroidView(R.id.fab_take_photo)
    @OnClick
    private FloatingActionButton takePhotoBtn;

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @AndroidView(R.id.sv_form_content)
    private NestedScrollView formContent;

    @AndroidView(R.id.ll_btn_bar)
    private LinearLayout buttonBar;

    @AndroidView(R.id.ll_disease_photo)
    @OnClick
    private LinearLayout diseasePhotoWrapper;

    @AndroidView(R.id.iv_photo)
    private ImageView photoView;

    @AndroidView(R.id.sp_site_no)
    @OnItemSelected
    private Spinner siteNoSelector;

    @AndroidView(R.id.sp_member_type)
    @OnItemSelected
    private Spinner memberTypeSelector;

    @AndroidView(R.id.sp_member_no)
    private Spinner memberNoSelector;

    @AndroidView(R.id.sp_disease_type)
    @OnItemSelected
    private Spinner diseaseTypeSelector;

    @AndroidView(R.id.sp_desc_type)
    @OnItemSelected
    private Spinner descTypeSelector;

    @AndroidView(R.id.tv_horizontal_position_start)
    private TextView horizontalPositionStartLabel;

    @AndroidView(R.id.tv_vertical_position_start)
    private TextView verticalPositionStartLabel;

    @AndroidView(R.id.tv_long_position_start)
    private TextView longPositionStartLabel;

    @AndroidView(R.id.et_horizontal_position_start)
    private EditText horizontalPositionStartView;

    @AndroidView(R.id.et_horizontal_position_end)
    private EditText horizontalPositionEndView;

    @AndroidView(R.id.et_long_position_start)
    private EditText longPositionStartView;

    @AndroidView(R.id.et_long_position_end)
    private EditText longPositionEndView;

    @AndroidView(R.id.et_vertical_position_start)
    private EditText verticalPositionStartView;

    @AndroidView(R.id.et_vertical_position_end)
    private EditText verticalPositionEndView;

    @AndroidView(R.id.sp_position)
    private Spinner positionSelector;

    @AndroidView(R.id.et_count)
    private EditText countInput;

    @AndroidView(R.id.et_angle)
    private EditText angleInput;

    @AndroidView(R.id.et_min_length)
    private EditText minLengthInput;

    @AndroidView(R.id.et_max_length)
    private EditText maxLengthInput;

    @AndroidView(R.id.et_min_width)
    private EditText minWidthInput;

    @AndroidView(R.id.et_max_width)
    private EditText maxWidthInput;

    @AndroidView(R.id.et_min_depth)
    private EditText minDepthInput;

    @AndroidView(R.id.et_max_depth)
    private EditText maxDepthInput;

    @AndroidView(R.id.et_percent_degree)
    private EditText percentDegreeInput;

    @AndroidView(R.id.sp_desc_degree)
    private Spinner descDegreeSelector;

    @AndroidView(R.id.et_behavior_degree)
    private EditText behaviorDegreeInput;

    @AndroidView(R.id.sp_deduction_scale)
    private Spinner deductionScaleSelector;

    @AndroidView(R.id.sc_significant)
    private SwitchCompat significantSwitch;

    @AndroidView(R.id.et_notes)
    private EditText notesInput;

    @AndroidView(R.id.ll_horizontal_position_start)
    private LinearLayout horizontalPositionStartWrapper;

    @AndroidView(R.id.ll_horizontal_position_end)
    private LinearLayout horizontalPositionEndWrapper;

    @AndroidView(R.id.ll_long_position_start)
    private LinearLayout longPositionStartWrapper;

    @AndroidView(R.id.ll_long_position_end)
    private LinearLayout longPositionEndWrapper;

    @AndroidView(R.id.ll_vertical_position_start)
    private LinearLayout verticalPositionStartWrapper;

    @AndroidView(R.id.ll_vertical_position_end)
    private LinearLayout verticalPositionEndWrapper;

    @AndroidView(R.id.ll_position)
    private LinearLayout positionWrapper;

    @AndroidView(R.id.ll_count)
    private LinearLayout countWrapper;

    @AndroidView(R.id.ll_angle)
    private LinearLayout angleWrapper;

    @AndroidView(R.id.ll_min_length)
    private LinearLayout minLengthWrapper;

    @AndroidView(R.id.ll_max_length)
    private LinearLayout maxLengthWrapper;

    @AndroidView(R.id.ll_min_width)
    private LinearLayout minWidthWrapper;

    @AndroidView(R.id.ll_max_width)
    private LinearLayout maxWidthWrapper;

    @AndroidView(R.id.ll_min_depth)
    private LinearLayout minDepthWrapper;

    @AndroidView(R.id.ll_max_depth)
    private LinearLayout maxDepthWrapper;

    @AndroidView(R.id.ll_percent_degree)
    private LinearLayout percentDegreeWrapper;

    @AndroidView(R.id.ll_desc_degree)
    private LinearLayout descDegreeWrapper;

    @AndroidView(R.id.ll_behavior_degree)
    private LinearLayout behaviorDegreeWrapper;

    @AndroidView(R.id.tv_length_label)
    private TextView lengthLabel;

    @AndroidView(R.id.tv_width_label)
    private TextView widthLabel;

    @AndroidView(R.id.tv_depth_label)
    private TextView depthLabel;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    @Mapper
    private BridgeDiseaseMapper bridgeDiseaseMapper;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    private List<TblBridgeSite> bridgeSiteList;

    /** 按钮是否可见 */
    private boolean isBtnBarVisible;

    private TblBridgeSide currentSide;

    private TblBridgeSite currentSite;

    private PositionType currentPosition;

    private TblBridgeDisease currentBridgeDisease;

    private TblParamEvaluationIndex currentEvaluationIndex;

    private InspectionDirection inspectionDirection;

    @Override
    protected void created() {
        setDefaultToolbar();
        initData();
        initFormContent();
        initFormItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDiseasePhotoView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_take_photo: {
                Intent intent = new Intent(this, CameraActivity.class);
                intent.putExtra(CameraActivity.PREVIEW_ACTIVITY, DiseasePhotoPreviewActivity.class);
                intent.putExtra(CURRENT_DISEASE, currentBridgeDisease);
                startActivity(intent);
                break;
            }
            case R.id.ll_disease_photo: {
                Intent intent = new Intent(this, DiseasePhotoWallActivity.class);
                intent.putExtra(CURRENT_DISEASE, currentBridgeDisease);
                startActivity(intent);
                break;
            }
            case R.id.tv_cancel: {
                finish();
                break;
            }
            case R.id.tv_save: {
                save();
                break;
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_site_no: {
                setMemberTypeSelector();
                break;
            }
            case R.id.sp_member_type: {
                setMemberNoSelector();
                setDiseaseTypeSelector();
                break;
            }
            case R.id.sp_disease_type: {
                setDescTypeSelector();
                setDeductionScaleSelector();
                break;
            }
            case R.id.sp_desc_type: {
                setDiseaseDescParam();
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    /** 设置病害照片视图 */
    private void setDiseasePhotoView() {
        TblBridgeDiseasePhoto photo = bridgeDiseasePhotoMapper.getLatestPhoto(currentBridgeDisease.getId());
        if (photo != null) {
            photoHeaderView.setImageBitmap(ImageUtils.createCompressedBitmap(photo.getPath()));
            photoView.setImageBitmap(ImageUtils.createThumbnailBitmap(photo.getPath()));
        } else {
            photoHeaderView.setImageBitmap(null);
            photoView.setImageBitmap(null);
        }
    }

    /** 初始化数据 */
    private void initData() {
        //检测方向默认从左往右
        inspectionDirection = DataUtils.get(this, AndroidConstant.INSPECTION_DIRECTION, InspectionDirection.class);
        if (inspectionDirection == null) inspectionDirection = InspectionDirection.LEFT_TO_RIGHT;
        currentSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeDiseaseActivity.CURRENT_SIDE);
        currentSite = (TblBridgeSite) getIntent().getSerializableExtra(BridgeDiseaseActivity.CURRENT_SITE);
        currentPosition = (PositionType) getIntent().getSerializableExtra(BridgeDiseaseActivity.POSITION_TYPE);
        currentBridgeDisease = (TblBridgeDisease) getIntent().getSerializableExtra(BridgeDiseaseActivity.CURRENT_DISEASE);
        if (currentBridgeDisease == null) currentBridgeDisease = new TblBridgeDisease.Builder()
                .id(StringUtils.createId())
                .taskId(currentSide.getTaskId())
                .bridgeId(currentSide.getBridgeId())
                .sideTypeId(currentSide.getSideTypeId())
                .build();
        bridgeSiteList = JsonUtils.toList(getIntent().getStringExtra(BridgeDiseaseActivity.SITE_LIST), TblBridgeSite.class);
    }

    /** 保存病害 */
    private void save() {
        //获取表单项信息
        TblBridgeSite bridgeSite = (TblBridgeSite) siteNoSelector.getSelectedItem();
        TblBridgeParts bridgeParts = (TblBridgeParts) memberTypeSelector.getSelectedItem();
        TblBridgeMember bridgeMember = (TblBridgeMember) memberNoSelector.getSelectedItem();
        TblParamDiseaseType diseaseType = (TblParamDiseaseType) diseaseTypeSelector.getSelectedItem();
        TblParamDiseaseDesc diseaseDesc = (TblParamDiseaseDesc) descTypeSelector.getSelectedItem();
        int deductionScale = deductionScaleSelector.getSelectedItemPosition() == 0 ? 2 : deductionScaleSelector.getSelectedItemPosition();
        int deductionPoint = scaleToPoint(deductionScale, currentEvaluationIndex.getMaxScale());
        int isSignificant = significantSwitch.isChecked() ? 1 : 0;
        //注入总体参数
        currentBridgeDisease.setSiteNo(bridgeSite.getSiteNo());
        currentBridgeDisease.setMaxSiteNo(bridgeSiteList.size());
        currentBridgeDisease.setJointNo(bridgeSite.getJointNo());
        currentBridgeDisease.setPositionTypeId(bridgeParts.getPositionTypeId());
        currentBridgeDisease.setPositionTypeName(bridgeParts.getPositionTypeName());
        currentBridgeDisease.setBridgePartsId(bridgeParts.getId());
        currentBridgeDisease.setPartsTypeId(bridgeParts.getPartsTypeId());
        currentBridgeDisease.setPartsTypeName(bridgeParts.getPartsTypeName());
        currentBridgeDisease.setBridgeMemberId(bridgeMember.getId());
        currentBridgeDisease.setMemberTypeId(bridgeParts.getMemberTypeId());
        currentBridgeDisease.setMemberTypeName(bridgeMember.getMemberTypeName());
        currentBridgeDisease.setMemberDesc(bridgeMember.getMemberDesc());
        currentBridgeDisease.setMaterialTypeId(bridgeParts.getMaterialTypeId());
        currentBridgeDisease.setMemberHorizontalNo(bridgeMember.getHorizontalNo());
        currentBridgeDisease.setMemberVerticalNo(bridgeMember.getVerticalNo());
        currentBridgeDisease.setMemberStakeSide(bridgeMember.getStakeSide());
        currentBridgeDisease.setMemberSpecialSection(bridgeMember.getSpecialSection());
        currentBridgeDisease.setDiseaseTypeId(diseaseType.getId());
        currentBridgeDisease.setDiseaseTypeName(diseaseType.getName());
        currentBridgeDisease.setDiseaseDesc(diseaseDesc.getDiseaseDesc());
        currentBridgeDisease.setMemberHorizontalCount(bridgeParts.getHorizontalCount());
        currentBridgeDisease.setMemberVerticalCount(bridgeParts.getVerticalCount());
        currentBridgeDisease.setDiseaseDescId(diseaseDesc.getId());
        currentBridgeDisease.setDeductionScale(deductionScale);
        currentBridgeDisease.setDeductionPoint(deductionPoint);
        currentBridgeDisease.setIsSignificant(isSignificant);
        currentBridgeDisease.setNotes(notesInput.getText().toString());
        //注入病害描述参数
        if (containDiseaseDescParam(H_POSITION_START)) currentBridgeDisease.sethPositionStart(StringUtils.toDouble(horizontalPositionStartView.getText().toString()));
        if (containDiseaseDescParam(H_POSITION_END)) currentBridgeDisease.sethPositionEnd(StringUtils.toDouble(horizontalPositionEndView.getText().toString()));
        if (containDiseaseDescParam(L_POSITION_START)) currentBridgeDisease.setlPositionStart(StringUtils.toDouble(longPositionStartView.getText().toString()));
        if (containDiseaseDescParam(L_POSITION_END)) currentBridgeDisease.setlPositionEnd(StringUtils.toDouble(longPositionEndView.getText().toString()));
        if (containDiseaseDescParam(V_POSITION_START)) currentBridgeDisease.setvPositionStart(StringUtils.toDouble(verticalPositionStartView.getText().toString()));
        if (containDiseaseDescParam(V_POSITION_END)) currentBridgeDisease.setvPositionEnd(StringUtils.toDouble(verticalPositionEndView.getText().toString()));
        if (containDiseaseDescParam(POSITION)) {
            TblParamType position = (TblParamType) positionSelector.getSelectedItem();
            currentBridgeDisease.setPosition(position.getName());
        }
        if (containDiseaseDescParam(COUNT)) currentBridgeDisease.setCount(StringUtils.toInt(countInput.getText().toString()));
        if (containDiseaseDescParam(ANGLE)) currentBridgeDisease.setAngle(StringUtils.toDouble(angleInput.getText().toString()));
        if (containDiseaseDescParam(MIN_LENGTH)) currentBridgeDisease.setMinLength(getDimensionMillimetreValue(minLengthInput, MIN_LENGTH));
        if (containDiseaseDescParam(MAX_LENGTH)) currentBridgeDisease.setMaxLength(getDimensionMillimetreValue(maxLengthInput, MAX_LENGTH));
        if (containDiseaseDescParam(MIN_WIDTH)) currentBridgeDisease.setMinWidth(getDimensionMillimetreValue(minWidthInput, MIN_WIDTH));
        if (containDiseaseDescParam(MAX_WIDTH)) currentBridgeDisease.setMaxWidth(getDimensionMillimetreValue(maxWidthInput, MAX_WIDTH));
        if (containDiseaseDescParam(MIN_DEPTH)) currentBridgeDisease.setMinDepth(getDimensionMillimetreValue(minDepthInput, MIN_DEPTH));
        if (containDiseaseDescParam(MAX_DEPTH)) currentBridgeDisease.setMaxDepth(getDimensionMillimetreValue(maxDepthInput, MAX_DEPTH));
        if (containDiseaseDescParam(PERCENT_DEGREE)) currentBridgeDisease.setPercentDegree(StringUtils.toDouble(percentDegreeInput.getText().toString()));
        if (containDiseaseDescParam(DESC_DEGREE)) {
            TblParamType descDegree = (TblParamType) descDegreeSelector.getSelectedItem();
            currentBridgeDisease.setDescDegree(descDegree.getName());
        }
        if (containDiseaseDescParam(BEHAVIOR_DESC)) currentBridgeDisease.setBehaviorDesc(behaviorDegreeInput.getText().toString());
        if (validate()) {
            //保存记录人信息
            currentBridgeDisease.setRecordUser(DataUtils.getCurrentToken(this).getContent().getId());
            currentBridgeDisease.setRecordUserName(DataUtils.getCurrentToken(this).getContent().getName());
            currentBridgeDisease.setRecordDate(StringUtils.fromDate(new Date()));
            //设为需要上传的数据
            currentBridgeDisease.setUpload(UploadStatus.NEED_UPLOAD.getCode());
            //保存并退出
            bridgeDiseaseMapper.addOrReplaceDisease(currentBridgeDisease);
            //更新病害类型频率
            bridgeDiseaseMapper.increaseDiseaseTypeFrequency(currentBridgeDisease.getMaterialTypeId(), currentBridgeDisease.getMemberTypeId(), currentBridgeDisease.getDiseaseTypeId());
            //返回结果
            currentBridgeDisease.setDiseasePhotoList(bridgeDiseasePhotoMapper.getDiseasePhotoList(currentBridgeDisease.getId()));
            Intent intent = new Intent();
            intent.putExtra(CURRENT_DISEASE, currentBridgeDisease);
            setResult(ActivityResultCode.SUCCESS.getCode(), intent);
            finish();
        }
    }

    /** 初始化表单项 */
    private void initFormItems() {
        setSiteNoSelector();
    }

    /** 设置孔号选择器 */
    private void setSiteNoSelector() {
        ArrayAdapter<TblBridgeSite> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, bridgeSiteList);
        siteNoSelector.setAdapter(adapter);
        if (currentBridgeDisease.getSiteNo() == 0) siteNoSelector.setSelection(currentSite.getSiteNo()-1);
        else siteNoSelector.setSelection(currentBridgeDisease.getSiteNo()-1);
    }

    /** 设置构件类型选择菜单 */
    private void setMemberTypeSelector() {
        TblBridgeSite bridgeSite = (TblBridgeSite) siteNoSelector.getSelectedItem();
        List<TblBridgeParts> bridgePartsList = bridgePartsMapper.getBridgePartsListBySiteNoAndPosition(currentSide.getBridgeId(),currentSide.getSideTypeId(),
                bridgeSite.getSiteNo(), currentPosition.getValue());
        if (bridgePartsList == null) bridgePartsList = new ArrayList<>();
        bridgePartsList.add(0, TblBridgeParts.empty());
        ArrayAdapter<TblBridgeParts> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, bridgePartsList);
        memberTypeSelector.setAdapter(adapter);
        if (!StringUtils.isEmpty(currentBridgeDisease.getMemberTypeId())) {
            TblBridgeParts bridgeParts = new TblBridgeParts();
            bridgeParts.setMemberTypeId(currentBridgeDisease.getMemberTypeId());
            memberTypeSelector.setSelection(ListUtils.indexOf(bridgePartsList, bridgeParts, Comparator.comparing(TblBridgeParts::getMemberTypeId)));
        }
    }

    /** 设置构件编号选择器 */
    private void setMemberNoSelector() {
        TblBridgeSite bridgeSite = (TblBridgeSite) siteNoSelector.getSelectedItem();
        TblBridgeParts bridgeParts = (TblBridgeParts) memberTypeSelector.getSelectedItem();
        List<TblBridgeMember> bridgeMemberList = bridgeMemberMapper.getBridgeMemberList(currentSide.getBridgeId(), currentSide.getSideTypeId(), bridgeSite.getSiteNo(), bridgeParts.getMemberTypeId());
        if (bridgeMemberList == null) {
            bridgeMemberList = new ArrayList<>();
        } else {
            Collections.sort(bridgeMemberList);
            bridgeMemberList.forEach(m->m.setBridgeMemberName(NameGenerator.generateMemberName(m, inspectionDirection)));
        }
        bridgeMemberList.add(0, TblBridgeMember.empty());
        ArrayAdapter<TblBridgeMember> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, bridgeMemberList);
        memberNoSelector.setAdapter(adapter);
        if (!StringUtils.isEmpty(currentBridgeDisease.getMemberDesc())) {
            TblBridgeMember bridgeMember = TblBridgeMember.of(currentBridgeDisease);
            memberNoSelector.setSelection(ListUtils.indexOf(bridgeMemberList, bridgeMember, (m1, m2)->{
                if (m1.getBridgeId().equals(m2.getBridgeId()) && m1.getSideTypeId().equals(m2.getSideTypeId()) &&
                        m1.getMemberTypeId().equals(m2.getMemberTypeId()) && m1.getStakeSide() == m2.getStakeSide() &&
                        m1.getSpecialSection().equals(m2.getSpecialSection()) && m1.getHorizontalNo() == m2.getHorizontalNo() &&
                        m1.getVerticalNo() == m2.getVerticalNo()) {
                    return 0;
                } else {
                    return 1;
                }
            }));
        }
    }

    /** 获取病害类型列表 */
    private void setDiseaseTypeSelector() {
        TblBridgeParts bridgeParts = (TblBridgeParts) memberTypeSelector.getSelectedItem();
        List<TblParamDiseaseType> diseaseTypeList = bridgeDiseaseMapper.getDiseaseTypeListByMemberType(bridgeParts.getMaterialTypeId(), bridgeParts.getMemberTypeId());
        if (diseaseTypeList == null) diseaseTypeList = new ArrayList<>();
        diseaseTypeList.add(0, TblParamDiseaseType.empty());
        ArrayAdapter<TblParamDiseaseType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, diseaseTypeList);
        diseaseTypeSelector.setAdapter(adapter);
        if (!StringUtils.isEmpty(currentBridgeDisease.getDiseaseTypeId())) {
            TblParamDiseaseType diseaseType = new TblParamDiseaseType();
            diseaseType.setId(currentBridgeDisease.getDiseaseTypeId());
            diseaseTypeSelector.setSelection(ListUtils.indexOf(diseaseTypeList, diseaseType, Comparator.comparing(TblParamDiseaseType::getId)));
        }
    }

    /** 获取病害描述方式 */
    private void setDescTypeSelector() {
        TblParamDiseaseType diseaseType = (TblParamDiseaseType) diseaseTypeSelector.getSelectedItem();
        List<TblParamDiseaseDesc> diseaseDescList = bridgeDiseaseMapper.getDiseaseDescListByDiseaseDescType(diseaseType.getDiseaseDescTypeId());
        if (diseaseDescList == null) diseaseDescList = new ArrayList<>();
        diseaseDescList.add(0, TblParamDiseaseDesc.empty());
        ArrayAdapter<TblParamDiseaseDesc> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, diseaseDescList);
        descTypeSelector.setAdapter(adapter);
        if (!StringUtils.isEmpty(currentBridgeDisease.getDiseaseDescId())) {
            TblParamDiseaseDesc diseaseDesc = new TblParamDiseaseDesc();
            diseaseDesc.setId(currentBridgeDisease.getDiseaseDescId());
            descTypeSelector.setSelection(ListUtils.indexOf(diseaseDescList, diseaseDesc, Comparator.comparing(TblParamDiseaseDesc::getId)));
        }
    }

    /** 设置扣分选项 */
    private void setDeductionScaleSelector() {
        TblParamDiseaseType diseaseType = (TblParamDiseaseType) diseaseTypeSelector.getSelectedItem();
        currentEvaluationIndex = bridgeDiseaseMapper.getEvaluationIndex(diseaseType.getEvaluationIndexId());
        if (currentEvaluationIndex == null) currentEvaluationIndex = TblParamEvaluationIndex.empty();
        List<String> deductionScaleList = Stream.iterate(1, t->t+1).limit(currentEvaluationIndex.getMaxScale()).map(i->"标度"+i).collect(Collectors.toList());
        deductionScaleList.add(0, "默认为标度2");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, deductionScaleList);
        deductionScaleSelector.setAdapter(adapter);
        if (deductionScaleList.size() >= currentBridgeDisease.getDeductionScale()) deductionScaleSelector.setSelection(currentBridgeDisease.getDeductionScale());
    }

    /** 设置病害的描述参数 */
    private void setDiseaseDescParam() {
        //先隐藏所有的参数
        hideDiseaseDescParam();
        //判断应该显示哪些参数
        if (containDiseaseDescParam(H_POSITION_START)) {
            horizontalPositionStartWrapper.setVisibility(View.VISIBLE);
            horizontalPositionStartLabel.setText("距左缘起始距离");
            if (currentBridgeDisease.gethPositionStart() > 0) horizontalPositionStartView.setText(String.valueOf(currentBridgeDisease.gethPositionStart()));
            if (containDiseaseDescParam(H_POSITION_END)) {
                horizontalPositionEndWrapper.setVisibility(View.VISIBLE);
                if (currentBridgeDisease.gethPositionEnd() > 0) horizontalPositionEndView.setText(String.valueOf(currentBridgeDisease.gethPositionEnd()));
            } else {
                horizontalPositionStartLabel.setText("距左缘距离");
            }
        }
        if (containDiseaseDescParam(L_POSITION_START)) {
            longPositionStartWrapper.setVisibility(View.VISIBLE);
            longPositionStartLabel.setText("距小桩号侧墩起始距离");
            if (currentBridgeDisease.getlPositionStart() > 0) longPositionStartView.setText(String.valueOf(currentBridgeDisease.getlPositionStart()));
            if (containDiseaseDescParam(L_POSITION_END)) {
                longPositionEndWrapper.setVisibility(View.VISIBLE);
                if (currentBridgeDisease.getlPositionEnd() > 0) longPositionEndView.setText(String.valueOf(currentBridgeDisease.getlPositionEnd()));
            } else {
                longPositionStartLabel.setText("距小桩号侧墩距离");
            }
        }
        if (containDiseaseDescParam(V_POSITION_START)) {
            verticalPositionStartWrapper.setVisibility(View.VISIBLE);
            verticalPositionStartLabel.setText("距上缘起始距离");
            if (currentBridgeDisease.getvPositionStart() > 0) verticalPositionStartView.setText(String.valueOf(currentBridgeDisease.getvPositionStart()));
            if (containDiseaseDescParam(V_POSITION_END)) {
                verticalPositionEndWrapper.setVisibility(View.VISIBLE);
                if (currentBridgeDisease.getvPositionEnd() > 0) verticalPositionEndView.setText(String.valueOf(currentBridgeDisease.getvPositionEnd()));
            } else {
                verticalPositionStartLabel.setText("距上缘距离");
            }
        }
        if (containDiseaseDescParam(POSITION)) {
            positionWrapper.setVisibility(View.VISIBLE);
            List<TblParamType> positionTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.POSITION);
            positionTypeList.add(0, TblParamType.empty());
            ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, positionTypeList);
            positionSelector.setAdapter(adapter);
            if (!StringUtils.isEmpty(currentBridgeDisease.getPosition())) {
                TblParamType position = new TblParamType();
                position.setName(currentBridgeDisease.getPosition());
                positionSelector.setSelection(ListUtils.indexOf(positionTypeList, position, Comparator.comparing(TblParamType::getName)));
            }
        }
        if (containDiseaseDescParam(COUNT)) {
            countWrapper.setVisibility(View.VISIBLE);
            if (currentBridgeDisease.getCount() > 0) countInput.setText(String.valueOf(currentBridgeDisease.getCount()));
        }
        if (containDiseaseDescParam(ANGLE)) {
            angleWrapper.setVisibility(View.VISIBLE);
            if (currentBridgeDisease.getAngle() > 0) angleInput.setText(String.valueOf(currentBridgeDisease.getAngle()));
        }
        if (containDiseaseDescParam(MIN_LENGTH)) {
            lengthLabel.setText("长度");
            minLengthWrapper.setVisibility(View.VISIBLE);
            minLengthInput.setHint("单位："+parseUnits(MIN_LENGTH));
            if (currentBridgeDisease.getMinLength() > 0) setDimensionMillimetreValue(minLengthInput, currentBridgeDisease.getMinLength(), MIN_LENGTH);
        }
        if (containDiseaseDescParam(MAX_LENGTH)) {
            lengthLabel.setText("最小长度");
            maxLengthWrapper.setVisibility(View.VISIBLE);
            maxLengthInput.setHint("单位："+parseUnits(MAX_LENGTH));
            if (currentBridgeDisease.getMaxLength() > 0) setDimensionMillimetreValue(maxLengthInput, currentBridgeDisease.getMaxLength(), MAX_LENGTH);
        }
        if (containDiseaseDescParam(MIN_WIDTH)) {
            widthLabel.setText("宽度");
            minWidthWrapper.setVisibility(View.VISIBLE);
            minWidthInput.setHint("单位："+parseUnits(MIN_WIDTH));
            if (currentBridgeDisease.getMinWidth() > 0) setDimensionMillimetreValue(minWidthInput, currentBridgeDisease.getMinWidth(), MIN_WIDTH);
        }
        if (containDiseaseDescParam(MAX_WIDTH)) {
            widthLabel.setText("最小宽度");
            maxWidthWrapper.setVisibility(View.VISIBLE);
            maxWidthInput.setHint("单位："+parseUnits(MAX_WIDTH));
            if (currentBridgeDisease.getMaxWidth() > 0) setDimensionMillimetreValue(maxWidthInput, currentBridgeDisease.getMaxWidth(), MAX_WIDTH);
        }
        if (containDiseaseDescParam(MIN_DEPTH)) {
            depthLabel.setText("深度");
            minDepthWrapper.setVisibility(View.VISIBLE);
            minDepthInput.setHint("单位："+parseUnits(MIN_DEPTH));
            if (currentBridgeDisease.getMinDepth() > 0) setDimensionMillimetreValue(minDepthInput, currentBridgeDisease.getMinDepth(), MIN_DEPTH);
        }
        if (containDiseaseDescParam(MAX_DEPTH)) {
            depthLabel.setText("最小深度");
            maxDepthWrapper.setVisibility(View.VISIBLE);
            maxDepthInput.setHint("单位："+parseUnits(MAX_DEPTH));
            if (currentBridgeDisease.getMaxDepth() > 0) setDimensionMillimetreValue(maxDepthInput, currentBridgeDisease.getMaxDepth(), MAX_DEPTH);
        }
        if (containDiseaseDescParam(PERCENT_DEGREE)) {
            percentDegreeWrapper.setVisibility(View.VISIBLE);
            if (currentBridgeDisease.getPercentDegree() > 0) percentDegreeInput.setText(String.valueOf(currentBridgeDisease.getPercentDegree()));
        }
        if (containDiseaseDescParam(DESC_DEGREE)) {
            descDegreeWrapper.setVisibility(View.VISIBLE);
            List<TblParamType> descDegreeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.DESC_DEGREE);
            descDegreeList.add(0, TblParamType.empty());
            ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, descDegreeList);
            descDegreeSelector.setAdapter(adapter);
            if (!StringUtils.isEmpty(currentBridgeDisease.getDescDegree())) {
                TblParamType descDegree = new TblParamType();
                descDegree.setName(currentBridgeDisease.getDescDegree());
                descDegreeSelector.setSelection(ListUtils.indexOf(descDegreeList, descDegree, Comparator.comparing(TblParamType::getName)));
            }
        }
        if (containDiseaseDescParam(BEHAVIOR_DESC)) {
            behaviorDegreeWrapper.setVisibility(View.VISIBLE);
            behaviorDegreeInput.setText(String.valueOf(currentBridgeDisease.getBehaviorDesc()));
        }
        significantSwitch.setChecked(currentBridgeDisease.getIsSignificant() == 1);
        if (!StringUtils.isEmpty(currentBridgeDisease.getNotes())) notesInput.setText(currentBridgeDisease.getNotes());
    }

    /** 隐藏病害的描述参数 */
    private void hideDiseaseDescParam() {
        horizontalPositionStartWrapper.setVisibility(View.GONE);
        horizontalPositionEndWrapper.setVisibility(View.GONE);
        longPositionStartWrapper.setVisibility(View.GONE);
        longPositionEndWrapper.setVisibility(View.GONE);
        verticalPositionStartWrapper.setVisibility(View.GONE);
        verticalPositionEndWrapper.setVisibility(View.GONE);
        positionWrapper.setVisibility(View.GONE);
        countWrapper.setVisibility(View.GONE);
        angleWrapper.setVisibility(View.GONE);
        minLengthWrapper.setVisibility(View.GONE);
        maxLengthWrapper.setVisibility(View.GONE);
        minWidthWrapper.setVisibility(View.GONE);
        maxWidthWrapper.setVisibility(View.GONE);
        minDepthWrapper.setVisibility(View.GONE);
        maxDepthWrapper.setVisibility(View.GONE);
        percentDegreeWrapper.setVisibility(View.GONE);
        descDegreeWrapper.setVisibility(View.GONE);
        behaviorDegreeWrapper.setVisibility(View.GONE);
    }

    /** 设置表单的滑动事件 */
    private void initFormContent() {
        formContent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY)->{
            if (oldScrollY < scrollY - 18) {
                if (!isBtnBarVisible) {
                    isBtnBarVisible = true;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(buttonBar, AnimationType.TRANSLATION_Y.getValue(), 0, buttonBar.getHeight());
                    animator.setDuration(500);
                    animator.start();
                }
            } else if (oldScrollY > scrollY + 18) {
                if (isBtnBarVisible) {
                    isBtnBarVisible = false;
                    ObjectAnimator animator = ObjectAnimator.ofFloat(buttonBar, AnimationType.TRANSLATION_Y.getValue(), buttonBar.getHeight(), 0);
                    animator.setDuration(500);
                    animator.start();
                }
            }
        });
    }

    /** 表单校验 */
    private boolean validate() {
        if (memberTypeSelector.getSelectedItem() == TblBridgeParts.empty()) {
            SystemUtils.prompt(this, "构件类型不能留空");
            return false;
        }
        if (memberNoSelector.getSelectedItem() == TblBridgeMember.empty()) {
            SystemUtils.prompt(this, "构件编号不能留空");
            return false;
        }
        if (diseaseTypeSelector.getSelectedItem() == TblParamDiseaseType.empty()) {
            SystemUtils.prompt(this, "病害类型不能留空");
            return false;
        }
        if (descTypeSelector.getSelectedItem() == TblParamDiseaseDesc.empty()) {
            SystemUtils.prompt(this, "病害描述类型不能为空");
            return false;
        }
        if (bridgeDiseaseMapper.checkDiseaseDuplication(currentBridgeDisease) > 0) {
            SystemUtils.prompt(this, "病害重复，请核对");
            return false;
        }
        return true;
    }

    /** 是否包含病害描述参数 */
    private boolean containDiseaseDescParam(DiseaseDescParam diseaseDescParam) {
        TblParamDiseaseDesc diseaseDesc = (TblParamDiseaseDesc) descTypeSelector.getSelectedItem();
        return diseaseDesc.getDiseaseDesc().contains(diseaseDescParam.getValue());
    }

    /** 解析单位 */
    private String parseUnits(DiseaseDescParam diseaseDescParam) {
        TblParamDiseaseDesc diseaseDesc = (TblParamDiseaseDesc) descTypeSelector.getSelectedItem();
        Pattern p = Pattern.compile("\\{"+diseaseDescParam.getValue()+",([^\\{\\}]*?)\\}");
        Matcher m = p.matcher(diseaseDesc.getDiseaseDesc());
        if (m.find()) return m.group(1);
        return "";
    }

    /** 评定标度转化为扣分值 */
    private int scaleToPoint(int scale, int maxScale) {
        int[] threeScale = {0, 20, 35};
        int[] fourScale = {0, 25, 40, 50};
        int[] fiveScale = {0, 35, 45, 60, 100};
        switch (maxScale) {
            case 3:
                return threeScale[scale-1];
            case 4:
                return fourScale[scale-1];
            case 5:
                return fiveScale[scale-1];
            default:
                return 0;
        }
    }

    /** 获取对应尺寸的毫米值 */
    private double getDimensionMillimetreValue(EditText input, DiseaseDescParam param) {
        return UnitConverter.toMillimetre(StringUtils.toDouble(input.getText().toString()), DimensionUnit.of(parseUnits(param)));
    }

    /** 设置毫米值 */
    private void setDimensionMillimetreValue(EditText input, double value, DiseaseDescParam param) {
        input.setText(String.valueOf(UnitConverter.fromMillimetre(value, DimensionUnit.of(parseUnits(param)))));
    }
}
