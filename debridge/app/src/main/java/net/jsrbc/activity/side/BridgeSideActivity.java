package net.jsrbc.activity.side;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.bridge.BridgeActivity;
import net.jsrbc.activity.member.BridgeSiteActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.OperationType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

@AndroidActivity(R.layout.activity_bridge_side)
@AndroidMenu(R.menu.empty)
public class BridgeSideActivity extends BaseActivity implements View.OnClickListener {

    /** 传递出去的参数KEY */
    public final static String CURRENT_BRIDGE = "CURRENT_BRIDGE";

    /** 传递出去的参数KEY */
    public final static String OPERATION_TYPE = "OPERATION_TYPE";

    /** 传递出去的参数KEY */
    public final static String CURRENT_SIDE = "CURRENT_SIDE";

    @AndroidView(R.id.fab)
    @OnClick
    private FloatingActionButton fab;

    @AndroidView(R.id.rv_side_list)
    private RecyclerView sideListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    private TblBridge currentBridge;

    private List<TblBridgeSide> bridgeSideList = new ArrayList<>();

    private SideListAdapter sideListAdapter;

    @Override
    protected void created() {
        this.currentBridge = (TblBridge) getIntent().getSerializableExtra(BridgeActivity.CURRENT_BRIDGE);
        setDefaultToolbar();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBridgeSide();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:{
                Intent intent = new Intent(this, BridgeSideFormActivity.class);
                intent.putExtra(CURRENT_BRIDGE, currentBridge);
                intent.putExtra(OPERATION_TYPE, OperationType.ADD);
                startActivity(intent);
                break;
            }
        }
    }

    /** 获取桥梁分幅信息 */
    private void fetchBridgeSide() {
        new Thread(()->{
            bridgeSideList.clear();
            List<TblBridgeSide> temp = bridgeSideMapper.getBridgeSideListByBridgeId(currentBridge.getId());
            if (temp != null) bridgeSideList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                showEmptyView(bridgeSideList.isEmpty());
                sideListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 设置分幅列表 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        sideListView.setLayoutManager(manager);
        sideListAdapter = new SideListAdapter(bridgeSideList);
        sideListView.setAdapter(sideListAdapter);
    }

    /** 是否展示列表为空的提示 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 显示悬浮菜单 */
    private void showPopupMenu(View view, PopupMenu.OnMenuItemClickListener onMenuItemClickListener) {
        PopupMenu menu = new PopupMenu(BridgeSideActivity.this, view);
        menu.getMenuInflater().inflate(R.menu.activity_bridge_side_menu, menu.getMenu());
        menu.setOnMenuItemClickListener(onMenuItemClickListener);
        menu.show();
    }

    /** 分幅列表的适配器 */
    private class SideListAdapter extends RecyclerView.Adapter<SideListAdapter.ViewHolder> {

        private List<TblBridgeSide> bridgeSideList;

        SideListAdapter(List<TblBridgeSide> bridgeSideList) {
            this.bridgeSideList = bridgeSideList;
        }

        @Override
        public SideListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_side_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SideListAdapter.ViewHolder holder, int position) {
            TblBridgeSide currentSide = bridgeSideList.get(position);
            holder.getSideNameView().setText(currentSide.getSideTypeName());
            holder.getRoadNoView().setText(String.format("路线编号： %s", StringUtils.isEmpty(currentBridge.getRoadNo())?"未知":currentBridge.getRoadNo()));
            holder.getStakeNoView().setText(String.format("桥位桩号： %s", StringUtils.parseStakeNo(currentSide.getStakeNo())));
            holder.getBridgeLengthView().setText(String.format("桥梁全长： %s", currentSide.getBridgeLength() == 0F?"未知":(Math.round(currentSide.getBridgeLength()/1000)+"米")));
            holder.getBridgeWidthView().setText(String.format("桥面总宽： %s", currentSide.getDeckTotalWidth() == 0F?"未知":(Math.round(currentSide.getDeckTotalWidth()/1000)+"米")));
            holder.getBridgeCombinationView().setText(String.format("跨径组合(m)： %s", StringUtils.isEmpty(currentSide.getBridgeSpanCombination())?"未知":currentSide.getBridgeSpanCombination()));
            holder.getMoreActionBtn().setOnClickListener(v-> showPopupMenu(holder.getMoreActionBtn(), item->{
                switch (item.getItemId()) {
                    case R.id.action_member_manage: {
                        Intent intent = new Intent(BridgeSideActivity.this, BridgeSiteActivity.class);
                        intent.putExtra(CURRENT_SIDE, currentSide);
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_structure_manage: {
                        Intent intent = new Intent(BridgeSideActivity.this, BridgeSideFormActivity.class);
                        intent.putExtra(CURRENT_BRIDGE, currentBridge);
                        intent.putExtra(CURRENT_SIDE, currentSide);
                        intent.putExtra(OPERATION_TYPE, OperationType.EDIT);
                        startActivity(intent);
                        break;
                    }
                    case R.id.action_copy_side: {
                        TblBridgeSide bridgeSide = currentSide.clone();
                        bridgeSide.setId(null);
                        Intent intent = new Intent(BridgeSideActivity.this, BridgeSideFormActivity.class);
                        intent.putExtra(CURRENT_BRIDGE, currentBridge);
                        intent.putExtra(CURRENT_SIDE, bridgeSide);
                        intent.putExtra(OPERATION_TYPE, OperationType.COPY);
                        startActivity(intent);
                        break;
                    }
                }
                return true;
            }));
        }

        @Override
        public int getItemCount() {
            return bridgeSideList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private ImageView moreActionBtn;

            private TextView sideNameView;

            private TextView roadNoView;

            private TextView stakeNoView;

            private TextView bridgeLengthView;

            private TextView bridgeWidthView;

            private TextView bridgeCombinationView;

            ViewHolder(View view) {
                super(view);
                moreActionBtn = view.findViewById(R.id.iv_more_action);
                sideNameView = view.findViewById(R.id.side_name);
                roadNoView = view.findViewById(R.id.tv_road_no);
                stakeNoView = view.findViewById(R.id.tv_stake_no);
                bridgeLengthView = view.findViewById(R.id.tv_bridge_length);
                bridgeWidthView = view.findViewById(R.id.tv_bridge_width);
                bridgeCombinationView = view.findViewById(R.id.tv_bridge_combination);
            }

            ImageView getMoreActionBtn() {
                return moreActionBtn;
            }

            TextView getSideNameView() {
                return sideNameView;
            }

            TextView getRoadNoView() {
                return roadNoView;
            }

            TextView getStakeNoView() {
                return stakeNoView;
            }

            TextView getBridgeLengthView() {
                return bridgeLengthView;
            }

            TextView getBridgeWidthView() {
                return bridgeWidthView;
            }

            TextView getBridgeCombinationView() {
                return bridgeCombinationView;
            }
        }
    }
}
