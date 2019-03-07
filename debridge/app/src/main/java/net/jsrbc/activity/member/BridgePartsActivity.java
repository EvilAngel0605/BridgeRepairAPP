package net.jsrbc.activity.member;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.view.SlidingMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AndroidActivity(R.layout.activity_bridge_parts)
@AndroidMenu(R.menu.empty)
public class BridgePartsActivity extends BaseActivity {

    public final static String CURRENT_PARTS = "CURRENT_PARTS";

    @AndroidView(R.id.rv_bridge_parts_list)
    private RecyclerView bridgePartsListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private TblBridgeSite currentBridgeSite;

    private BridgePartsListAdapter bridgePartsListAdapter;

    private List<TblBridgeParts> bridgePartsList = new ArrayList<>();

    private int currentPosition;

    @Override
    protected void created() {
        currentBridgeSite = (TblBridgeSite) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SITE);
        setDefaultToolbar();
        if (getSupportActionBar() != null) {
            StringBuilder title = new StringBuilder();
            if (currentBridgeSite.getJointNo() != 0) title.append("第").append(currentBridgeSite.getJointNo()).append("联");
            title.append("第").append(currentBridgeSite.getSiteNo()).append("孔").append("部构件列表");
            getSupportActionBar().setTitle(title.toString());
        }
        setRecyclerView();
        fetchBridgePartsData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityRequestCode.GENERAL_REQUEST.getCode() && resultCode == ActivityResultCode.SUCCESS.getCode()) {
            bridgePartsList.set(currentPosition, (TblBridgeParts) data.getSerializableExtra(CURRENT_PARTS));
            bridgePartsListAdapter.notifyItemChanged(currentPosition);
        }
    }

    /** 设置部件列表 */
    private void setRecyclerView() {
        bridgePartsListAdapter = new BridgePartsListAdapter(bridgePartsList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        bridgePartsListView.setLayoutManager(manager);
        bridgePartsListView.setAdapter(bridgePartsListAdapter);
    }

    /** 获取桥梁部构件信息 */
    private void fetchBridgePartsData() {
        showProgressDialog();
        new Thread(()->{
            bridgePartsList.clear();
            List<TblBridgeParts> temp = bridgePartsMapper.getBridgePartsListBySiteNo(currentBridgeSite.getBridgeId(), currentBridgeSite.getSideTypeId(), currentBridgeSite.getSiteNo());
            if (temp != null) bridgePartsList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                showEmptyView(bridgePartsList.isEmpty());
                bridgePartsListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 是否显示列表为空的提示 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 适配器 */
    private class BridgePartsListAdapter extends RecyclerView.Adapter<BridgePartsListAdapter.ViewHolder> {

        private List<TblBridgeParts> bridgePartsList;

        private HashSet<SlidingMenu> prevSlidingMenus = new HashSet<>();

        BridgePartsListAdapter(List<TblBridgeParts> bridgePartsList) {
            this.bridgePartsList = bridgePartsList;
        }

        void closeOpenedMenu(SlidingMenu excludeMenu) {
            prevSlidingMenus.stream().filter(m->m != excludeMenu).forEach(SlidingMenu::closeMenu);
            prevSlidingMenus.clear();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bridge_parts_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridgeParts bridgeParts = bridgePartsList.get(position);
            //设置标题
            holder.getBridgePartsTitleView().setText(String.format("%s    %s", bridgeParts.getPartsTypeName(), bridgeParts.getMemberTypeName()));
            //部件总结
            StringBuilder builder = new StringBuilder();
            if (!StringUtils.isEmpty(bridgeParts.getStructureTypeName())) builder.append(bridgeParts.getStructureTypeName()).append("    ");
            if (!StringUtils.isEmpty(bridgeParts.getMaterialTypeName())) builder.append(bridgeParts.getMaterialTypeName()).append("    ");
            if (bridgeParts.getHorizontalCount() != 0) builder.append("横向：").append(bridgeParts.getHorizontalCount()).append("    ");
            if (bridgeParts.getVerticalCount() != 0) builder.append("纵向：").append(bridgeParts.getVerticalCount()).append("    ");
            if (bridgeParts.getStakeSide() != 0) builder.append(StakeSide.of(bridgeParts.getStakeSide()).getName()).append("    ");
            if (!StringUtils.isEmpty(bridgeParts.getSpecialSection())) builder.append("含：").append(bridgeParts.getSpecialSection());
            holder.getBridgePartsDescView().setText(builder.toString());
            //设置整体的点击事件
            holder.getBridgePartsWrapper().setOnClickListener(v->{
                if (!prevSlidingMenus.isEmpty()) {
                    closeOpenedMenu(null);
                } else {
                    currentPosition = holder.getAdapterPosition();
                    Intent intent = new Intent(BridgePartsActivity.this, BridgePartsFormActivity.class);
                    intent.putExtra(CURRENT_PARTS, bridgeParts);
                    startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
                }
            });
            //设置事件
            holder.getDeleteBtn().setOnClickListener(v-> SystemUtils.confirm(BridgePartsActivity.this, "确定删除该部件及对应的所有构件？", (d, i)->{
                bridgePartsMapper.deleteBridgePartsById(bridgeParts.getId());
                bridgeMemberMapper.deleteBridgeMemberByParts(bridgeParts);
                bridgePartsList.remove(bridgeParts);
                notifyItemRemoved(holder.getAdapterPosition());
                showEmptyView(bridgePartsList.isEmpty());
            }));
            //滑动事件处理
            holder.getSlidingMenu().setOnMenuSlidingListener(m->{
                closeOpenedMenu(m);
                prevSlidingMenus.add(m);
            });
        }

        @Override
        public int getItemCount() {
            return bridgePartsList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private SlidingMenu slidingMenu;

            private LinearLayout bridgePartsWrapper;

            private TextView bridgePartsTitleView;

            private TextView bridgePartsDescView;

            private TextView deleteBtn;

            ViewHolder(View view) {
                super(view);
                slidingMenu = view.findViewById(R.id.sliding_menu);
                bridgePartsWrapper = view.findViewById(R.id.ll_bridge_parts_wrapper);
                bridgePartsTitleView = view.findViewById(R.id.tv_bridge_parts_title);
                bridgePartsDescView = view.findViewById(R.id.tv_bridge_parts_desc);
                deleteBtn = view.findViewById(R.id.tv_delete_btn);
            }

            SlidingMenu getSlidingMenu() {
                return slidingMenu;
            }

            LinearLayout getBridgePartsWrapper() {
                return bridgePartsWrapper;
            }

            TextView getBridgePartsTitleView() {
                return bridgePartsTitleView;
            }

            TextView getBridgePartsDescView() {
                return bridgePartsDescView;
            }

            TextView getDeleteBtn() {
                return deleteBtn;
            }
        }
    }
}
