package net.jsrbc.activity.member;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblParamStructureType;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.MemberType;
import net.jsrbc.enumeration.PartsType;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.enumeration.StructureGroupField;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnItemSelected;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.pojo.BridgePartsForm;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@AndroidActivity(R.layout.activity_pier_form)
@AndroidMenu(R.menu.save)
public class PierFormActivity extends BaseActivity implements AdapterView.OnItemSelectedListener{

    public final static String CURRENT_BRIDGE_PARTS = "CURRENT_BRIDGE_PARTS";

    @AndroidView(R.id.et_start_pier_no)
    private EditText startPierNoInput;

    @AndroidView(R.id.et_end_pier_no)
    private EditText endPierNoInput;

    @AndroidView(R.id.sp_pier_type)
    @OnItemSelected
    private Spinner pierTypeSelector;

    @AndroidView(R.id.rv_member_list)
    private RecyclerView partsMemberListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    @Mapper
    private BaseDataMapper baseDataMapper;

    private TblBridgeSide currentBridgeSide;

    private List<TblBridgeParts> bridgePartsList = new ArrayList<>();

    private BridgePartsListAdapter bridgePartsListAdapter;

    private int currentPosition;

    @Override
    protected void created() {
        setDefaultToolbar();
        this.currentBridgeSide =(TblBridgeSide) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
        setPierTypeSelector();
        setRecyclerView();
        fetchPartsMemberData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if (validate()) addPier();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.sp_pier_type: {
                SystemUtils.hideSoftInputFromWindow(pierTypeSelector, this);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SystemUtils.hideSoftInputFromWindow(startPierNoInput, this);
        if (requestCode == ActivityRequestCode.GENERAL_REQUEST.getCode() &&
                resultCode == ActivityResultCode.SUCCESS.getCode()) {
            bridgePartsList.set(currentPosition, (TblBridgeParts) data.getSerializableExtra(CURRENT_BRIDGE_PARTS));
            bridgePartsListAdapter.notifyItemChanged(currentPosition);
        }
    }

    private void addPier() {
        showProgressDialog();
        new Thread(()->{
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .startSiteNo(StringUtils.toInt(startPierNoInput.getText().toString()))
                    .endSiteNo(StringUtils.toInt(endPierNoInput.getText().toString()))
                    .structureTypeId(((TblParamStructureType)pierTypeSelector.getSelectedItem()).getId())
                    .partsList(bridgePartsList)
                    .build();
            //如果起始孔跨小于等于0，则设为1
            if (bridgePartsForm.getStartSiteNo() <= 0) bridgePartsForm.setStartSiteNo(1);
            //补充一个墩基础进去
            String materialTypeId = "";
            for (TblBridgeParts bridgeParts : bridgePartsForm.getPartsList()) {
                if (StringUtils.isEmpty(bridgeParts.getStructureTypeId()) && bridgeParts.getPartsTypeName().equals(PartsType.PIER.getValue())) {
                    bridgeParts.setStructureTypeId(bridgePartsForm.getStructureTypeId());
                    if (StringUtils.isEmpty(materialTypeId)) materialTypeId = bridgeParts.getMaterialTypeId();
                }
                if (bridgeParts.getMemberTypeName().equals(MemberType.PIER_BASE.getValue())) {
                    bridgeParts.setHorizontalCount(1);
                    bridgeParts.setMaterialTypeId(materialTypeId);
                }
                //桥墩都在大桩号侧
                if (bridgeParts.getHorizontalCount() != 0 || bridgeParts.getVerticalCount() != 0 ||
                        bridgeParts.getStakeSide() != StakeSide.NONE.getCode() || !StringUtils.isEmpty(bridgeParts.getSpecialSection()))
                    bridgeParts.setStakeSide(StakeSide.LARGER_SIDE.getCode());
            }
            //加入数据库
            MemberUtils.addBridgeParts(bridgePartsForm, Stream.iterate(bridgePartsForm.getStartSiteNo(), i->++i).limit(bridgePartsForm.getEndSiteNo()-bridgePartsForm.getStartSiteNo()+1),
                    ()->{
                        bridgePartsMapper.deletePierPartsRangeOf(bridgePartsForm);
                        bridgePartsMapper.deletePierBasePartsRangeOf(bridgePartsForm);
                        bridgeMemberMapper.deletePierMemberRangeOf(bridgePartsForm);
                        bridgeMemberMapper.deletePierBaseMemberRangeOf(bridgePartsForm);
                    },
                    (pList, mList)->{
                        bridgePartsMapper.addOrReplaceBridgePartsList(pList);
                        bridgeMemberMapper.addOrReplaceBridgeMemberList(mList);
                    });
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }

    /** 设置桥墩类型 */
    private void setPierTypeSelector() {
        List<TblParamStructureType> pierTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.PIER_TYPE);
        pierTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, pierTypeList);
        pierTypeSelector.setAdapter(adapter);
    }

    /** 设置RecyclerView */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        partsMemberListView.setLayoutManager(manager);
        bridgePartsListAdapter = new BridgePartsListAdapter(bridgePartsList);
        partsMemberListView.setAdapter(bridgePartsListAdapter);
    }

    /** 根据桥墩结构类型获取部构件数据 */
    private void fetchPartsMemberData() {
        showProgressDialog();
        new Thread(()->{
            bridgePartsList.clear();
            List<TblBridgeParts> temp = bridgePartsMapper.getBridgePartsListByPier();
            if (temp != null) bridgePartsList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                showEmptyView(bridgePartsList.isEmpty());
                bridgePartsListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 校验输入的内容 */
    private boolean validate() {
        //必填项不能为空
        if (StringUtils.isEmpty(startPierNoInput.getText().toString())) {
            startPierNoInput.requestFocus();
            startPierNoInput.setError("起始墩号不能为空");
            return false;
        }
        if (StringUtils.isEmpty(endPierNoInput.getText().toString())) {
            endPierNoInput.requestFocus();
            endPierNoInput.setError("结束墩号不能为空");
            return false;
        }
        //判断墩号有没有超出范围
        int siteCount = bridgeSiteMapper.getSiteCountBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
        int startPierNo = StringUtils.toInt(startPierNoInput.getText().toString());
        int endPierNo = StringUtils.toInt(endPierNoInput.getText().toString());
        if (startPierNo == 0 || startPierNo > siteCount-1) {
            startPierNoInput.requestFocus();
            startPierNoInput.setError(String.format("起始墩号超出范围，墩号范围应在(0, %s]内，请核查", siteCount-1));
            return false;
        }
        if (endPierNo == 0 || endPierNo > siteCount-1) {
            endPierNoInput.requestFocus();
            endPierNoInput.setError(String.format("结束墩号超出范围，墩号范围应在(0, %s]内，请核查", siteCount-1));
            return false;
        }
        if (endPierNo < startPierNo) {
            endPierNoInput.requestFocus();
            endPierNoInput.setError("结束墩号不能小于起始墩号，请核查");
            return false;
        }
        if (pierTypeSelector.getSelectedItem() == TblParamStructureType.empty()) {
            SystemUtils.prompt(this, "必须选择桥墩类型");
            return false;
        }
        if (bridgePartsList.isEmpty()) {
            SystemUtils.prompt(this, "无可添加的部构件");
            return false;
        }
        return true;
    }

    /** 是否显示空白提示 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    private class BridgePartsListAdapter extends RecyclerView.Adapter<BridgePartsListAdapter.ViewHolder> {

        private List<TblBridgeParts> bridgePartsList;

        BridgePartsListAdapter(List<TblBridgeParts> bridgePartsList) {
            this.bridgePartsList = bridgePartsList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parts_member_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridgeParts bridgeParts = bridgePartsList.get(position);
            //墩台基础自己处理了
            if (bridgeParts.getPartsTypeName().equals(PartsType.BASE.getValue())) {
                holder.getPartsMemberWrapper().setVisibility(View.GONE);
                return;
            }
            holder.getPartsMemberTitleView().setText(String.format("%s    %s", bridgeParts.getPartsTypeName(), bridgeParts.getMemberTypeName()));
            if (bridgeParts.getHorizontalCount() == 0) {
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.red_light));
                holder.getPartsMemberContentView().setText("点击设置材料类型及构件数量等");
            } else {
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.gray_dark));
                holder.getPartsMemberContentView().setText(String.format("数量：%s    材料类型：%s", bridgeParts.getHorizontalCount(), bridgeParts.getMaterialTypeName()));
            }
            //设置项目点击事件
            holder.getPartsMemberWrapper().setOnClickListener(v->{
                currentPosition = holder.getAdapterPosition();
                Intent intent = new Intent(PierFormActivity.this, PierMemberFormActivity.class);
                intent.putExtra(CURRENT_BRIDGE_PARTS, bridgeParts);
                startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
            });
        }

        @Override
        public int getItemCount() {
            return bridgePartsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout partsMemberWrapper;

            private TextView partsMemberTitleView;

            private TextView partsMemberContentView;

            ViewHolder(View view) {
                super(view);
                partsMemberWrapper = view.findViewById(R.id.ll_parts_member);
                partsMemberTitleView = view.findViewById(R.id.tv_parts_member_title);
                partsMemberContentView = view.findViewById(R.id.tv_parts_member_content);
            }

            LinearLayout getPartsMemberWrapper() {
                return partsMemberWrapper;
            }

            TextView getPartsMemberTitleView() {
                return partsMemberTitleView;
            }

            TextView getPartsMemberContentView() {
                return partsMemberContentView;
            }
        }
    }
}
