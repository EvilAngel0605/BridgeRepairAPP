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

@AndroidActivity(R.layout.activity_superstructure_form)
@AndroidMenu(R.menu.save)
public class SuperstructureFormActivity extends BaseActivity
        implements AdapterView.OnItemSelectedListener{

    /** 编辑的部件 */
    public final static String CURRENT_BRIDGE_PARTS = "CURRENT_PARTS";

    @AndroidView(R.id.et_start_site_no)
    private EditText startSiteNoInput;

    @AndroidView(R.id.et_end_site_no)
    private EditText endSiteNoInput;

    @AndroidView(R.id.sp_superstructure_type)
    @OnItemSelected
    private Spinner superstructureTypeSelector;

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
        setSuperstructureTypeSelector();
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
        SystemUtils.hideSoftInputFromWindow(superstructureTypeSelector, this);
        switch (adapterView.getId()) {
            case R.id.sp_superstructure_type: {
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
                if (validate()) addSuperstructure();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** 添加上部结构的部构件 */
    private void addSuperstructure() {
        showProgressDialog();
        new Thread(()->{
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .startSiteNo(StringUtils.toInt(startSiteNoInput.getText().toString()))
                    .endSiteNo(StringUtils.toInt(endSiteNoInput.getText().toString()))
                    .structureTypeId(((TblParamStructureType)superstructureTypeSelector.getSelectedItem()).getId())
                    .partsList(bridgePartsList)
                    .build();
            //如果起始孔跨小于等于0，则设为1
            if (bridgePartsForm.getStartSiteNo() <= 0) bridgePartsForm.setStartSiteNo(1);
            //注入结构类型
            bridgePartsForm.getPartsList().forEach(item->item.setStructureTypeId(StringUtils.isEmpty(item.getStructureTypeId())?bridgePartsForm.getStructureTypeId():item.getStructureTypeId()));
            //批量添加部构件
            MemberUtils.addBridgeParts(bridgePartsForm, Stream.iterate(bridgePartsForm.getStartSiteNo(), i->++i).limit(bridgePartsForm.getEndSiteNo()-bridgePartsForm.getStartSiteNo()+1),
                    ()->{
                        bridgePartsMapper.deleteSuperPartsRangeOf(bridgePartsForm);
                        bridgeMemberMapper.deleteSuperMemberRangeOf(bridgePartsForm);
                    },
                    (pList, mList)->{
                        bridgePartsMapper.addOrReplaceBridgePartsList(pList);
                        bridgeMemberMapper.addOrReplaceBridgeMemberList(mList);
                    });
            //返回
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }

    /** 校验输入的内容 */
    private boolean validate() {
        //必填项不能为空
        if (StringUtils.isEmpty(startSiteNoInput.getText().toString())) {
            startSiteNoInput.requestFocus();
            startSiteNoInput.setError("起始孔号不能为空");
            return false;
        }
        if (StringUtils.isEmpty(endSiteNoInput.getText().toString())) {
            endSiteNoInput.requestFocus();
            endSiteNoInput.setError("结束孔号不能为空");
            return false;
        }
        //判断孔号有没有超出范围
        int siteCount = bridgeSiteMapper.getSiteCountBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
        int startSiteNo = StringUtils.toInt(startSiteNoInput.getText().toString());
        int endSiteNo = StringUtils.toInt(endSiteNoInput.getText().toString());
        if (startSiteNo > siteCount) {
            startSiteNoInput.requestFocus();
            startSiteNoInput.setError(String.format("起始孔号超出范围，总跨数为%s，请核查", siteCount));
            return false;
        }
        if (endSiteNo > siteCount) {
            endSiteNoInput.requestFocus();
            endSiteNoInput.setError(String.format("结束孔号超出范围，总跨数为%s，请核查", siteCount));
            return false;
        }
        if (endSiteNo < startSiteNo) {
            endSiteNoInput.requestFocus();
            endSiteNoInput.setError("结束孔号不能小于起始孔号，请核查");
            return false;
        }
        if (superstructureTypeSelector.getSelectedItem() == TblParamStructureType.empty()) {
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
    private void setSuperstructureTypeSelector() {
        List<TblParamStructureType> superstructureTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.SUPERSTRUCTURE_TYPE);
        superstructureTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, superstructureTypeList);
        superstructureTypeSelector.setAdapter(adapter);
    }

    /** 根据上部结构类型获取部构件数据 */
    private void fetchPartsMemberData(String superstructureTypeId) {
        showProgressDialog();
        new Thread(()->{
            bridgePartsList.clear();
            List<TblBridgeParts> temp = bridgePartsMapper.getBridgePartsListBySuperstructure(superstructureTypeId);
            if (temp != null) bridgePartsList.addAll(temp);
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
        public BridgePartsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parts_member_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BridgePartsListAdapter.ViewHolder holder, int position) {
            TblBridgeParts currentBridgeParts = bridgePartsList.get(position);
            holder.getPartsMemberTitleView().setText(String.format("%s    %s", currentBridgeParts.getPartsTypeName(), currentBridgeParts.getMemberTypeName()));
            if (currentBridgeParts.getHorizontalCount() == 0 && currentBridgeParts.getVerticalCount() == 0 && StringUtils.isEmpty(currentBridgeParts.getSpecialSection()) && currentBridgeParts.getStakeSide() == StakeSide.NONE.getCode()) {
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.red_light));
                holder.getPartsMemberContentView().setText("点击设置材料类型及构件数量等");
            } else {
                StringBuilder builder = new StringBuilder();
                if (!StringUtils.isEmpty(currentBridgeParts.getStructureTypeName())) builder.append(currentBridgeParts.getStructureTypeName()).append("    ");
                if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeName())) builder.append(currentBridgeParts.getMaterialTypeName()).append("    ");
                if (currentBridgeParts.getHorizontalCount() != 0) builder.append("横向：").append(currentBridgeParts.getHorizontalCount()).append("    ");
                if (currentBridgeParts.getVerticalCount() != 0) builder.append("纵向：").append(currentBridgeParts.getVerticalCount()).append("    ");
                if (currentBridgeParts.getStakeSide() != 0) builder.append(StakeSide.of(currentBridgeParts.getStakeSide()).getName()).append("    ");
                if (!StringUtils.isEmpty(currentBridgeParts.getSpecialSection())) builder.append("特殊节段：").append(currentBridgeParts.getSpecialSection());
                holder.getPartsMemberContentView().setTextColor(getColor(R.color.gray_dark));
                holder.getPartsMemberContentView().setText(builder.toString());
            }
            //设置行点击事件
            holder.getPartsMemberContentWrapper().setOnClickListener(v->{
                currentPosition = holder.getAdapterPosition();
                Intent intent = new Intent(SuperstructureFormActivity.this, SuperMemberFormActivity.class);
                intent.putExtra(CURRENT_BRIDGE_PARTS, JsonUtils.toJson(currentBridgeParts));
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
