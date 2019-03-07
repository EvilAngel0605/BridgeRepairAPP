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
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@AndroidActivity(R.layout.activity_abutment_form)
@AndroidMenu(R.menu.save)
public class AbutmentFormActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener{

    /** 编辑的部件 */
    public final static String CURRENT_BRIDGE_PARTS = "CURRENT_PARTS";

    @AndroidView(R.id.sp_abutment_type)
    @OnItemSelected
    private Spinner abutmentTypeSelector;

    @AndroidView(R.id.rv_member_list)
    private RecyclerView partsMemberListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private TblBridgeSide currentBridgeSide;

    private List<TblBridgeParts> bridgePartsList = new ArrayList<>();

    private BridgePartsListAdapter bridgePartsListAdapter;

    /** 当前设置的位置 */
    private int currentPosition;

    @Override
    protected void created() {
        setDefaultToolbar();
        this.currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
        setAbutmentTypeSelector();
        setRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SystemUtils.hideSoftInputFromWindow(partsMemberListView, this);
        if (requestCode == ActivityRequestCode.GENERAL_REQUEST.getCode() &&
                resultCode == ActivityResultCode.SUCCESS.getCode()) {
            bridgePartsList.set(currentPosition, (TblBridgeParts) data.getSerializableExtra(CURRENT_BRIDGE_PARTS));
            bridgePartsListAdapter.notifyItemChanged(currentPosition);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        SystemUtils.hideSoftInputFromWindow(partsMemberListView, this);
        switch (adapterView.getId()) {
            case R.id.sp_abutment_type: {
                TblParamStructureType structureType = (TblParamStructureType)adapterView.getSelectedItem();
                fetchPartsMemberData(structureType.getId());
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        bridgePartsList.clear();
        bridgePartsListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                if (validate()) addAbutment();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** 增加桥台 */
    private void addAbutment() {
        showProgressDialog();
        new Thread(()->{
            int siteCount = bridgeSiteMapper.getSiteCountBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .startSiteNo(0)
                    .endSiteNo(siteCount)
                    .structureTypeId(((TblParamStructureType)abutmentTypeSelector.getSelectedItem()).getId())
                    .partsList(bridgePartsList)
                    .build();
            //判断输入的台号
            Stream<Integer> siteStream;
            if (bridgePartsForm.getStartSiteNo() == bridgePartsForm.getEndSiteNo()) {
                siteStream = Stream.of(bridgePartsForm.getStartSiteNo());
            } else if (bridgePartsForm.getStartSiteNo() == 0 && bridgePartsForm.getEndSiteNo() == 1) {
                siteStream = Stream.of(1);
            } else if (bridgePartsForm.getStartSiteNo() < bridgePartsForm.getEndSiteNo()){
                siteStream = Stream.of(bridgePartsForm.getStartSiteNo(), bridgePartsForm.getEndSiteNo());
            } else {
                return;
            }
            //补上桥台基础
            String materialTypeId = "";
            for (TblBridgeParts bridgeParts : bridgePartsForm.getPartsList()) {
                if (StringUtils.isEmpty(bridgeParts.getStructureTypeId()) && bridgeParts.getPartsTypeName().equals("桥台")) {
                    bridgeParts.setStructureTypeId(bridgePartsForm.getStructureTypeId());
                    if (StringUtils.isEmpty(materialTypeId)) materialTypeId = bridgeParts.getMaterialTypeId();
                }
                if (bridgeParts.getMemberTypeName().equals("台基础")) {
                    bridgeParts.setHorizontalCount(1);
                    bridgeParts.setMaterialTypeId(materialTypeId);
                }
                //如果全桥只有一孔，则大小桩号侧对称
                if (bridgePartsForm.getStartSiteNo() == 0 && bridgePartsForm.getEndSiteNo() == 1
                        && (bridgeParts.getHorizontalCount() != 0 || bridgeParts.getVerticalCount() != 0 ||
                                bridgeParts.getStakeSide() != StakeSide.NONE.getCode() || !StringUtils.isEmpty(bridgeParts.getSpecialSection()))) {
                    bridgeParts.setStakeSide(StakeSide.BOTH_SIDE.getCode());
                }
            }
            //遍历孔跨，添加部构件
            MemberUtils.addBridgeParts(bridgePartsForm, siteStream,
                    ()->{
                        bridgePartsMapper.deleteAbutmentParts(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                        bridgePartsMapper.deleteAbutmentBaseParts(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                        bridgePartsMapper.deleteAbutmentAttachParts(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                        bridgeMemberMapper.deleteAbutmentMember(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                        bridgeMemberMapper.deleteAbutmentBaseMember(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                        bridgeMemberMapper.deleteAbutmentAttachMember(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
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

    /** 校验输入的内容 */
    private boolean validate() {
        if (abutmentTypeSelector.getSelectedItem() == TblParamStructureType.empty()) {
            SystemUtils.prompt(this, "必须选择结构类型");
            return false;
        }
        if (bridgePartsList.isEmpty()) {
            SystemUtils.prompt(this, "无可添加的部构件");
            return false;
        }
        return true;
    }

    /** 设置部构件列表 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        partsMemberListView.setLayoutManager(manager);
        bridgePartsListAdapter = new BridgePartsListAdapter(bridgePartsList);
        partsMemberListView.setAdapter(bridgePartsListAdapter);
    }

    /** 设置上部结构类型选择菜单 */
    private void setAbutmentTypeSelector() {
        List<TblParamStructureType> abutmentTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.ABUTMENT_TYPE);
        abutmentTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, abutmentTypeList);
        abutmentTypeSelector.setAdapter(adapter);
    }

    /** 根据上部结构类型获取部构件数据 */
    private void fetchPartsMemberData(String abutmentTypeId) {
        showProgressDialog();
        new Thread(()->{
            bridgePartsList.clear();
            List<TblBridgeParts> tempAbutment = bridgePartsMapper.getBridgePartsListByAbutment(abutmentTypeId);
            List<TblBridgeParts> tempAttach = bridgePartsMapper.getBridgePartsListByAbutmentAttach();
            if (tempAbutment != null) bridgePartsList.addAll(tempAbutment);
            if (tempAttach != null && !StringUtils.isEmpty(abutmentTypeId)) bridgePartsList.addAll(tempAttach);
            AndroidConstant.HANDLER.post(()->{
                bridgePartsListAdapter.notifyDataSetChanged();
                showEmptyView(bridgePartsList.isEmpty());
                hideProgressDialog();
            });
        }).start();
    }

    /** 是否显示空白提示 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 桥梁部件列表适配器 */
    private class BridgePartsListAdapter extends RecyclerView.Adapter<BridgePartsListAdapter.ViewHolder> {

        private List<TblBridgeParts> bridgePartsList;

        BridgePartsListAdapter(List<TblBridgeParts> bridgePartsList) {
            this.bridgePartsList = bridgePartsList;
        }

        @Override
        public AbutmentFormActivity.BridgePartsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parts_member_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AbutmentFormActivity.BridgePartsListAdapter.ViewHolder holder, int position) {
            TblBridgeParts currentBridgeParts = bridgePartsList.get(position);
            //墩台基础自己处理了
            if (currentBridgeParts.getPartsTypeName().equals(PartsType.BASE.getValue())) {
                holder.getPartsMemberContentWrapper().setVisibility(View.GONE);
                return;
            }
            holder.getPartsMemberTitleView().setText(String.format("%s    %s", currentBridgeParts.getPartsTypeName(), currentBridgeParts.getMemberTypeName()));
            if (currentBridgeParts.getHorizontalCount() == 0 && currentBridgeParts.getVerticalCount() == 0 && StringUtils.isEmpty(currentBridgeParts.getSpecialSection())) {
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.red_light));
                holder.getPartsMemberContentView().setText("点击设置材料类型及构件数量等");
            } else {
                StringBuilder builder = new StringBuilder();
                if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeName())) builder.append(currentBridgeParts.getMaterialTypeName()).append("    ");
                if (currentBridgeParts.getHorizontalCount() != 0) builder.append("数量：").append(currentBridgeParts.getHorizontalCount()).append("    ");
                if (!StringUtils.isEmpty(currentBridgeParts.getSpecialSection())) builder.append(currentBridgeParts.getSpecialSection());
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.gray_dark));
                holder.getPartsMemberContentView().setText(builder.toString());
            }
            //设置行点击事件
            holder.getPartsMemberContentWrapper().setOnClickListener(v->{
                currentPosition = holder.getAdapterPosition();
                Intent intent = new Intent(AbutmentFormActivity.this, AbutmentMemberFormActivity.class);
                intent.putExtra(CURRENT_BRIDGE_PARTS, currentBridgeParts);
                startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
            });
        }

        @Override
        public int getItemCount() {
            return bridgePartsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout partsMemberContentWrapper;

            private TextView partsMemberTitleView;

            private TextView partsMemberContentView;

            ViewHolder(View view) {
                super(view);
                partsMemberContentWrapper = view.findViewById(R.id.ll_parts_member);
                partsMemberTitleView = view.findViewById(R.id.tv_parts_member_title);
                partsMemberContentView = view.findViewById(R.id.tv_parts_member_content);
            }

            LinearLayout getPartsMemberContentWrapper() {
                return partsMemberContentWrapper;
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
