package net.jsrbc.activity.bridge;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblTask;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_bridge_list)
@AndroidMenu(R.menu.search)
public class BridgeListActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    /** 传给BridgeActivity的对象KEY */
    public final static String CURRENT_BRIDGE = "CURRENT_BRIDGE";

    @AndroidView(R.id.no_bridge_tips)
    private TextView noBridgeTips;

    @AndroidView(R.id.rv_bridge_list)
    private RecyclerView bridgeListView;

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    private SearchView searchView;

    @Mapper
    private BridgeMapper bridgeMapper;

    private TblTask currentTask;

    private BridgeListAdapter bridgeListAdapter;

    private List<TblBridge> bridgeList = new ArrayList<>();

    @Override
    protected void created() {
        setDefaultToolbar();
        currentTask = JsonUtils.fromJson(getIntent().getStringExtra(MyTaskActivity.CURRENT_TASK), TblTask.class);
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchBridgeList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        searchView.setQueryHint("搜索...");
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        bridgeListAdapter = new BridgeListAdapter(bridgeList.stream()
                .filter(b->String.format("%s  %s", b.getRoadNo(), b.getName()).contains(newText))
                .collect(Collectors.toList()));
        bridgeListView.setAdapter(bridgeListAdapter);
        return false;
    }

    /** 设置列表视图 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        bridgeListView.setLayoutManager(manager);
        bridgeListAdapter = new BridgeListAdapter(bridgeList);
        bridgeListView.setAdapter(bridgeListAdapter);
    }

    /** 获取桥梁集合 */
    private void fetchBridgeList() {
        showProgressDialog();
        new Thread(()->{
            bridgeList.clear();
            List<TblBridge> temp = bridgeMapper.getBridgeListByTaskId(currentTask.getId());
            if (temp != null) bridgeList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                showNoBridgeTips(bridgeList.isEmpty());
                bridgeListAdapter.notifyDataSetChanged();
                hideProgressDialog();
            });
        }).start();
    }

    /** 是否显示桥梁列表为空的提示 */
    private void showNoBridgeTips(boolean isShow) {
        if (isShow) noBridgeTips.setVisibility(View.VISIBLE);
        else noBridgeTips.setVisibility(View.GONE);
    }

    /** 桥梁列表视图适配器 */
    private class BridgeListAdapter extends RecyclerView.Adapter<BridgeListAdapter.ViewHolder> {

        private List<TblBridge> bridgeList;

        BridgeListAdapter(List<TblBridge> bridgeList) {
            this.bridgeList = bridgeList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bridge_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridge currentBridge = bridgeList.get(position);
            String bridgeName = String.format("%s  %s  %s", currentBridge.getRoadNo(), StringUtils.parseStakeNo(currentBridge.getStakeNo()), currentBridge.getName());
            //判断桥梁照片是否可见
            if (FileUtils.exists(currentBridge.getSidePath())) {
                holder.getBridgePhotoView().setVisibility(View.VISIBLE);
                holder.getNoPhotoView().setVisibility(View.GONE);
                loadThumbnail(holder.getBridgePhotoView(), currentBridge.getSidePath());
            } else {
                holder.getBridgePhotoView().setVisibility(View.GONE);
                holder.getNoPhotoView().setVisibility(View.VISIBLE);
            }
            holder.getBridgeNameView().setText(bridgeName);
            holder.getBuildDateView().setText(StringUtils.isEmpty(currentBridge.getBuildDateStr())?"未知":currentBridge.getBuildDateStr());
            holder.getSideCountView().setText(String.format("分幅：%s幅", currentBridge.getSideCount()));
            holder.getBridgeCategoryView().setText(String.format("桥梁分类：%s", StringUtils.isEmpty(currentBridge.getBridgeCategoryName())?"未知":currentBridge.getBridgeCategoryName()));
            holder.getBridgeEvaluationView().setText(String.format("上次评级：%s", StringUtils.isEmpty(currentBridge.getBridgeEvaluationLevel())?"未知":currentBridge.getBridgeEvaluationLevel()+"类"));
            holder.getBridgeItemContent().setOnClickListener(v->{
                Intent intent = new Intent(BridgeListActivity.this, BridgeActivity.class);
                intent.putExtra(CURRENT_BRIDGE, JsonUtils.toJson(currentBridge));
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return bridgeList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout bridgeItemContent;

            private ImageView bridgePhotoView;

            private TextView noPhotoView;

            private TextView bridgeNameView;

            private TextView buildDateView;

            private TextView sideCountView;

            private TextView bridgeCategoryView;

            private TextView bridgeEvaluationView;

            ViewHolder(View view) {
                super(view);
                bridgeItemContent = view.findViewById(R.id.ll_bridge_list_item);
                bridgePhotoView = view.findViewById(R.id.iv_bridge_photo);
                noPhotoView = view.findViewById(R.id.tv_no_photo);
                bridgeNameView = view.findViewById(R.id.tv_bridge_name);
                buildDateView = view.findViewById(R.id.tv_build_date);
                sideCountView = view.findViewById(R.id.tv_side_count);
                bridgeCategoryView = view.findViewById(R.id.tv_bridge_category);
                bridgeEvaluationView = view.findViewById(R.id.tv_bridge_evaluation);
            }

            LinearLayout getBridgeItemContent() {
                return bridgeItemContent;
            }

            ImageView getBridgePhotoView() {
                return bridgePhotoView;
            }

            TextView getNoPhotoView() {
                return noPhotoView;
            }

            TextView getBridgeNameView() {
                return bridgeNameView;
            }

            TextView getBuildDateView() {
                return buildDateView;
            }

            TextView getSideCountView() {
                return sideCountView;
            }

            TextView getBridgeCategoryView() {
                return bridgeCategoryView;
            }

            TextView getBridgeEvaluationView() {
                return bridgeEvaluationView;
            }
        }
    }
}
