package net.jsrbc.activity.bridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.task.MyTaskActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblTask;
import net.jsrbc.enumeration.ConnectionStatus;
import net.jsrbc.enumeration.TaskType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.service.DownloadService;
import net.jsrbc.service.UploadService;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_bridge_upload)
@AndroidMenu(R.menu.activity_bridge_upload_menu)
public class BridgeUploadActivity extends BaseActivity
        implements SearchView.OnQueryTextListener{

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @AndroidView(R.id.rv_bridge_list)
    private RecyclerView bridgeListView;

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    private SearchView searchView;

    @Mapper
    private BridgeMapper bridgeMapper;

    private List<TblBridge> bridgeList = new ArrayList<>();

    private BridgeListViewAdapter bridgeListViewAdapter;

    private LocalBroadcastManager localBroadcastManager;

    private LocalReceiver localReceiver;

    @Override
    protected void created() {
        setDefaultToolbar();
        registerLocalBroadcast();
        setRecyclerView();
        fetchBridge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    /** 设置列表视图 */
    private void setRecyclerView() {
        bridgeListViewAdapter = new BridgeListViewAdapter(bridgeList);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        bridgeListView.setLayoutManager(manager);
        bridgeListView.setAdapter(bridgeListViewAdapter);
    }

    /** 获取待上传的桥梁 */
    private void fetchBridge() {
        showProgressDialog();
        new Thread(()->{
            bridgeList.clear();
            List<TblBridge> temp = bridgeMapper.getBridgeListForUpload();
            if (temp != null) bridgeList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                showEmptyView(ListUtils.isEmpty(bridgeList));
                bridgeListViewAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        bridgeListViewAdapter = new BridgeListViewAdapter(bridgeList.stream()
                .filter(b->String.format("%s  %s", b.getRoadNo(), b.getName()).contains(newText))
                .collect(Collectors.toList()));
        bridgeListView.setAdapter(bridgeListViewAdapter);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_all_upload: {
                if (UploadService.isUploading()) {
                    SystemUtils.prompt(this, "请等待当前上传任务完成");
                } else {
                    SystemUtils.confirm(this, "确定上传所有桥梁", (d, i)-> bridgeListViewAdapter.getList()
                            .forEach(b -> {
                                Intent intent = new Intent(BridgeUploadActivity.this, UploadService.class);
                                intent.putExtra(UploadService.CURRENT_UPLOAD_BRIDGE, b);
                                startService(intent);
                            }));
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** 是否展示空视图 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 注册本地广播 */
    private void registerLocalBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UploadService.UPLOAD_PROGRESS);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    /** 本地广播监听 */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectionStatus status = (ConnectionStatus) intent.getSerializableExtra(UploadService.UPLOAD_STATUS);
            switch (status) {
                case LINKED:
                    TblBridge bridge = (TblBridge) intent.getSerializableExtra(UploadService.CURRENT_UPLOAD_BRIDGE);
                    if (bridge.getProgress() == getResources().getInteger(R.integer.upload_max_progress)) {
                        List<TblBridge> temp = bridgeListViewAdapter.getList().stream().filter(b->!b.getId().equals(bridge.getId())).collect(Collectors.toList());
                        bridgeListViewAdapter.getList().clear();
                        bridgeListViewAdapter.getList().addAll(temp);
                    } else {
                        bridgeListViewAdapter.getList().stream().filter(t->t.getId().equals(bridge.getId())).findFirst().ifPresent(t->t.setProgress(bridge.getProgress()));
                    }
                    bridgeListViewAdapter.notifyDataSetChanged();
                    showEmptyView(ListUtils.isEmpty(bridgeListViewAdapter.getList()));
                    break;
                case DISCONNECTED:
                    SystemUtils.prompt(BridgeUploadActivity.this, "上传失败，请检查网络是否稳定");
                    break;
                case NON_AUTH:
                    SystemUtils.sessionTimeout(BridgeUploadActivity.this);
                    break;
            }
        }
    }

    /** 桥梁列表适配器 */
    private class BridgeListViewAdapter extends RecyclerView.Adapter<BridgeListViewAdapter.ViewHolder> {

        private List<TblBridge> bridgeList;

        BridgeListViewAdapter(List<TblBridge> bridgeList) {
            this.bridgeList = bridgeList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upload_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TblBridge bridge = bridgeList.get(position);
            String bridgeName = String.format("%s  %s  %s", bridge.getRoadNo(), StringUtils.parseStakeNo(bridge.getStakeNo()), bridge.getName());
            //判断桥梁照片是否可见
            if (FileUtils.exists(bridge.getSidePath())) {
                holder.getBridgePhotoView().setVisibility(View.VISIBLE);
                holder.getNoPhotoView().setVisibility(View.GONE);
                loadThumbnail(holder.getBridgePhotoView(), bridge.getSidePath());
            } else {
                holder.getBridgePhotoView().setVisibility(View.GONE);
                holder.getNoPhotoView().setVisibility(View.VISIBLE);
            }
            holder.getBridgeNameView().setText(bridgeName);
            holder.getTaskTypeView().setText(TaskType.of(bridge.getTaskType()).getText());
            //描述语句
            if (bridge.getProgress() > 0 && UploadService.isUploading()) {
                holder.getUploadProgress().setVisibility(View.VISIBLE);
                holder.getDescView().setVisibility(View.GONE);
                holder.getUploadProgress().setProgress(bridge.getProgress());
            } else {
                holder.getUploadProgress().setVisibility(View.GONE);
                holder.getDescView().setVisibility(View.VISIBLE);
                holder.getDescView().setText("点击上传");
            }
            //点击桥梁开始上传
            holder.getWrapper().setOnClickListener(v->{
                SystemUtils.confirm(BridgeUploadActivity.this, "确定上传？", (d, i)->{
                    Intent intent = new Intent(BridgeUploadActivity.this, UploadService.class);
                    intent.putExtra(UploadService.CURRENT_UPLOAD_BRIDGE, bridge);
                    startService(intent);
                });
            });
        }

        @Override
        public int getItemCount() {
            return bridgeList.size();
        }

        public List<TblBridge> getList() {
            return bridgeList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout wrapper;

            private ImageView bridgePhotoView;

            private TextView noPhotoView;

            private TextView bridgeNameView;

            private TextView taskTypeView;

            private TextView descView;

            private ProgressBar uploadProgress;

            ViewHolder(View view) {
                super(view);
                wrapper = view.findViewById(R.id.ll_wrapper);
                bridgePhotoView = view.findViewById(R.id.iv_bridge_photo);
                noPhotoView = view.findViewById(R.id.tv_no_photo);
                bridgeNameView = view.findViewById(R.id.tv_bridge_name);
                taskTypeView = view.findViewById(R.id.tv_task_type);
                descView = view.findViewById(R.id.tv_desc);
                uploadProgress = view.findViewById(R.id.pb_upload_progress);
            }

            LinearLayout getWrapper() {
                return wrapper;
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

            TextView getTaskTypeView() {
                return taskTypeView;
            }

            TextView getDescView() {
                return descView;
            }

            ProgressBar getUploadProgress() {
                return uploadProgress;
            }
        }
    }
}
