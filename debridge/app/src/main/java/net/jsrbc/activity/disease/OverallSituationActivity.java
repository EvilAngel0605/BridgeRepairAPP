package net.jsrbc.activity.disease;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.bridge.BridgeActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeDiseaseMapper;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.view.SlidingMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@AndroidActivity(R.layout.activity_overall_situation)
@AndroidMenu(R.menu.empty)
public class OverallSituationActivity extends BaseActivity
 implements View.OnClickListener{

    /** 当前桥梁分幅 */
    public final static String CURRENT_BRIDGE_SIDE = "CURRENT_BRIDGE_SIDE";

    /** 当前病害 */
    public final static String CURRENT_BRIDGE_DISEASE = "CURRENT_BRIDGE_DISEASE";

    @AndroidView(R.id.fab)
    @OnClick
    private FloatingActionButton fab;

    @AndroidView(R.id.rv_overall_situation)
    private RecyclerView overallSituationListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BridgeDiseaseMapper bridgeDiseaseMapper;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    private List<TblBridgeDisease> overallSituationList = new ArrayList<>();

    private OverallSituationListAdapter overallSituationListAdapter;

    private TblBridgeSide currentSide;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeActivity.CURRENT_SIDE);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(String.format("%s(%s)", currentSide.getBridgeName(), currentSide.getSideTypeName()));
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchOverallSituationData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab: {
                Intent intent = new Intent(this, OverallSituationFormActivity.class);
                intent.putExtra(CURRENT_BRIDGE_SIDE, currentSide);
                startActivity(intent);
                break;
            }
        }
    }

    /** 设置列表视图 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        overallSituationListAdapter = new OverallSituationListAdapter(overallSituationList);
        overallSituationListView.setLayoutManager(manager);
        overallSituationListView.setAdapter(overallSituationListAdapter);
    }

    /** 获取数据 */
    private void fetchOverallSituationData() {
        showProgressDialog();
        new Thread(()->{
            overallSituationList.clear();
            List<TblBridgeDisease> temp = bridgeDiseaseMapper.getOverallSituationList(currentSide.getTaskId(), currentSide.getBridgeId(), currentSide.getSideTypeId());
            if (temp != null) {
                overallSituationList.addAll(temp);
                overallSituationList.forEach(o->o.setDiseasePhotoList(bridgeDiseasePhotoMapper.getDiseasePhotoList(o.getId())));
            }
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                overallSituationListAdapter.notifyDataSetChanged();
                showEmptyView(overallSituationList.isEmpty());
            });
        }).start();
    }

    /** 显示空视图 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 列表适配器 */
    private class OverallSituationListAdapter extends RecyclerView.Adapter<OverallSituationListAdapter.ViewHolder> {

        private List<TblBridgeDisease> bridgeDiseaseList;

        private HashSet<SlidingMenu> prevSlidingMenus = new HashSet<>();

        OverallSituationListAdapter(List<TblBridgeDisease> bridgeDiseaseList) {
            this.bridgeDiseaseList = bridgeDiseaseList;
        }

        /** 关闭所有打开的菜单 */
        void closeOpenMenu(SlidingMenu excludeMenu) {
            prevSlidingMenus.stream().filter(m->m != excludeMenu).forEach(SlidingMenu::closeMenu);
            prevSlidingMenus.clear();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_overall_situation_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridgeDisease bridgeDisease = bridgeDiseaseList.get(position);
            holder.getNotesView().setText(bridgeDisease.getNotes());
            holder.getRecordUserView().setText(String.format("记录人：%s", bridgeDisease.getRecordUserName()));
            holder.getRecordDateView().setText(String.format("记录时间：%s", bridgeDisease.getRecordDate()));
            if (ListUtils.isEmpty(bridgeDisease.getDiseasePhotoList())) {
                holder.getNoPhotoView().setVisibility(View.VISIBLE);
                holder.getPhotoView().setVisibility(View.GONE);
            } else {
                holder.getNoPhotoView().setVisibility(View.GONE);
                holder.getPhotoView().setVisibility(View.VISIBLE);
                loadThumbnail(holder.getPhotoView(), bridgeDisease.getDiseasePhotoList().get(0).getPath());
            }
            holder.getWrapper().setOnClickListener(v->{
                if (!prevSlidingMenus.isEmpty()) {
                    closeOpenMenu(null);
                } else {
                    Intent intent = new Intent(OverallSituationActivity.this, OverallSituationFormActivity.class);
                    intent.putExtra(CURRENT_BRIDGE_DISEASE, bridgeDisease);
                    intent.putExtra(CURRENT_BRIDGE_SIDE, currentSide);
                    startActivity(intent);
                }
            });
            holder.getDeleteBtn().setOnClickListener(v->{
                SystemUtils.confirm(OverallSituationActivity.this, "确定删除", (d, i)->{
                    bridgeDiseaseMapper.deleteDisease(bridgeDisease.getId());
                    bridgeDiseaseList.remove(bridgeDisease);
                    notifyItemRemoved(holder.getAdapterPosition());
                    showEmptyView(ListUtils.isEmpty(bridgeDiseaseList));
                });
            });
            holder.getSlidingMenu().setOnMenuSlidingListener(slidingMenu -> {
                closeOpenMenu(slidingMenu);
                prevSlidingMenus.add(slidingMenu);
            });
        }

        @Override
        public int getItemCount() {
            return bridgeDiseaseList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private SlidingMenu slidingMenu;

            private LinearLayout wrapper;

            private ImageView photoView;

            private TextView noPhotoView;

            private TextView notesView;

            private TextView recordUserView;

            private TextView recordDateView;

            private TextView deleteBtn;

            ViewHolder(View view) {
                super(view);
                slidingMenu = view.findViewById(R.id.sliding_menu);
                wrapper = view.findViewById(R.id.ll_wrapper);
                photoView = view.findViewById(R.id.iv_photo);
                noPhotoView = view.findViewById(R.id.tv_no_photo);
                notesView = view.findViewById(R.id.tv_notes);
                recordUserView = view.findViewById(R.id.tv_record_user);
                recordDateView = view.findViewById(R.id.tv_record_date);
                deleteBtn = view.findViewById(R.id.tv_delete_btn);
            }

            SlidingMenu getSlidingMenu() {
                return slidingMenu;
            }

            LinearLayout getWrapper() {
                return wrapper;
            }

            ImageView getPhotoView() {
                return photoView;
            }

            TextView getNoPhotoView() {
                return noPhotoView;
            }

            TextView getNotesView() {
                return notesView;
            }

            TextView getRecordUserView() {
                return recordUserView;
            }

            TextView getRecordDateView() {
                return recordDateView;
            }

            TextView getDeleteBtn() {
                return deleteBtn;
            }
        }
    }
}
