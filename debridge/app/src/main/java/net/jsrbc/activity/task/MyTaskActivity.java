package net.jsrbc.activity.task;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.jsrbc.activity.bridge.BridgeListActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblTask;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.ConnectionStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.R;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.mapper.TaskMapper;
import net.jsrbc.service.DownloadService;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.view.SlidingMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_my_task)
@AndroidMenu(R.menu.search)
public class MyTaskActivity extends BaseActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener{

    /** 当前任务，用于传Intent参数 */
    public final static String CURRENT_TASK = "CURRENT_TASK";

    @AndroidView(R.id.toolbar)
    private Toolbar toolbar;

    @AndroidView(R.id.rv_my_task_list)
    private RecyclerView myTaskListView;

    @AndroidView(R.id.fab)
    @OnClick
    private FloatingActionButton fab;

    @AndroidView(R.id.no_task_tips)
    private TextView noTaskTips;

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    private SearchView searchView;

    @Mapper
    private TaskMapper taskMapper;

    @Mapper
    private BridgeMapper bridgeMapper;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    private List<TblTask> myTaskList = new ArrayList<>();

    private MyTaskListAdapter myTaskListAdapter;

    private LocalBroadcastManager localBroadcastManager;

    private LocalReceiver localReceiver;

    @Override
    protected void created() {
        setDefaultToolbar();
        registerLocalBroadcast();
        setMyTaskListView();
        fetchMyTaskList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        searchView.setQueryHint("搜索...");
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:{
                if (DownloadService.isExecuting()) {
                    SystemUtils.prompt(this, "请等待当前任务下载结束后再试");
                    return;
                }
                showProgressDialog();
                new Thread(()->{
                    if (hasBridgeToUpload()) {
                        AndroidConstant.HANDLER.post(()->SystemUtils.prompt(this, "发现有桥梁数据待同步， 请同步后再下载任务单"));
                    } else {
                        Intent intent = new Intent(this, TaskDownloadActivity.class);
                        AndroidConstant.HANDLER.post(()->startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode()));
                    }
                    hideProgressDialog();
                }).start();
                break;
            }
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myTaskListAdapter = new MyTaskListAdapter(myTaskList.stream()
                .filter(t-> t.getName().contains(newText) || t.getManagerName().contains(newText))
                .collect(Collectors.toList()));
        myTaskListView.setAdapter(myTaskListAdapter);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ActivityRequestCode.of(requestCode) == ActivityRequestCode.GENERAL_REQUEST &&
                ActivityResultCode.of(resultCode) == ActivityResultCode.SUCCESS) {
            fetchMyTaskList();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }

    /** 是否显示内容为空的提示 */
    private void showNoTaskTips(boolean isShow) {
        if (isShow) {
            noTaskTips.setVisibility(View.VISIBLE);
        } else {
            noTaskTips.setVisibility(View.INVISIBLE);
        }
    }

    /** 注册本地广播监听 */
    private void registerLocalBroadcast() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadService.DOWNLOAD_PROGRESS);
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver, intentFilter);
    }

    /** 设置列表视图 */
    private void setMyTaskListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        myTaskListView.setLayoutManager(layoutManager);
        myTaskListAdapter = new MyTaskListAdapter(myTaskList);
        myTaskListView.setAdapter(myTaskListAdapter);
    }

    /** 获取任务单列表 */
    private void fetchMyTaskList() {
        new Thread(()->{
            myTaskList.clear();
            List<TblTask> tempTaskList = taskMapper.getTaskList();
            if (tempTaskList != null) myTaskList.addAll(taskMapper.getTaskList());
            AndroidConstant.HANDLER.post(()->{
                showNoTaskTips(myTaskList.isEmpty());
                myTaskListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 判断是否有桥梁待上传 */
    private boolean hasBridgeToUpload() {
        return bridgeMapper.getBridgeCountForUpload() > 0;
    }

    /** 本地广播监听 */
    private class LocalReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectionStatus status = (ConnectionStatus) intent.getSerializableExtra(DownloadService.DOWNLOAD_STATUS);
            switch (status) {
                case LINKED:
                    TblTask task = JsonUtils.fromJson(intent.getStringExtra(DownloadService.CURRENT_DOWNLOAD_TASK), TblTask.class);
                    myTaskListAdapter.getList().stream().filter(t->t.getId().equals(task.getId())).findFirst().ifPresent(t->t.setProgress(task.getProgress()));
                    myTaskListAdapter.notifyDataSetChanged();
                    break;
                case DISCONNECTED:
                    SystemUtils.prompt(MyTaskActivity.this, "下载失败，请检查网络是否稳定");
                    break;
                case NON_AUTH:
                    SystemUtils.sessionTimeout(MyTaskActivity.this);
                    break;
            }
        }
    }

    /** recyclerView的适配器 */
    private class MyTaskListAdapter extends RecyclerView.Adapter<MyTaskListAdapter.ViewHolder> {

        private List<TblTask> myTaskList;

        private HashSet<SlidingMenu> prevSlidingMenus = new HashSet<>();

        MyTaskListAdapter(List<TblTask> myTaskList) {
            this.myTaskList = myTaskList;
        }

        /**
         * 关闭打开的菜单
         * @param excludeMenu  排除在外的菜单
         */
        void closeOpenedMenu(SlidingMenu excludeMenu) {
            prevSlidingMenus.stream().filter(m->m != excludeMenu).forEach(SlidingMenu::closeMenu);
            prevSlidingMenus.clear();
        }

        @Override
        public MyTaskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_task_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(MyTaskListAdapter.ViewHolder holder, int position) {
            //重新绘制时先关闭打开的菜单
            closeOpenedMenu(null);
            TblTask currentTask = myTaskList.get(position);
            String managerName = "负责人：" + currentTask.getManagerName();
            String total = "桥梁总数：" + currentTask.getTotal();
            holder.getTaskNameView().setText(currentTask.getName());
            holder.getStartDateView().setText(currentTask.getStartDate());
            holder.getManagerNameView().setText(managerName);
            holder.getTotalView().setText(total);
            holder.getTaskProgressBar().setProgress(currentTask.getProgress());
            if (currentTask.getProgress() == getResources().getInteger(R.integer.taskMaxProgress)) {
                holder.getTaskProgressBar().setVisibility(View.GONE);
                holder.getTaskInfoContent().setVisibility(View.VISIBLE);
            } else {
                holder.getTaskProgressBar().setVisibility(View.VISIBLE);
                holder.getTaskInfoContent().setVisibility(View.GONE);
            }

            holder.getTaskRowContent().setOnClickListener(v->{
                //先清空打开的菜单
                if (!prevSlidingMenus.isEmpty()) {
                    closeOpenedMenu(null);
                } else {
                    Intent intent = new Intent(MyTaskActivity.this, BridgeListActivity.class);
                    intent.putExtra(CURRENT_TASK, JsonUtils.toJson(currentTask));
                    startActivity(intent);
                }
            });

            //设置事件监听
            holder.getDeleteBtn().setOnClickListener(v-> SystemUtils.confirm(MyTaskActivity.this, "确定删除？", (v2, i)->{
                showProgressDialog();
                new Thread(()->{
                    taskMapper.deleteTask(currentTask.getId());
                    SystemUtils.deleteTaskCascade(taskMapper, currentTask.getId());
                    myTaskList.remove(currentTask);
                    AndroidConstant.HANDLER.post(()->{
                        hideProgressDialog();
                        notifyItemRemoved(holder.getAdapterPosition());
                        if (myTaskList.isEmpty()) showNoTaskTips(true);
                    });
                }).start();
            }));

            //处理菜单滑动的情况
            holder.getSlidingMenu().setOnMenuSlidingListener(slidingMenu -> {
                closeOpenedMenu(slidingMenu);
                MyTaskListAdapter.this.prevSlidingMenus.add(slidingMenu);
            });
        }

        @Override
        public int getItemCount() {
            return myTaskList.size();
        }

        public List<TblTask> getList() {
            return myTaskList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout taskRowContent;

            private TextView taskNameView;

            private TextView startDateView;

            private LinearLayout taskInfoContent;

            private TextView managerNameView;

            private TextView totalView;

            private ProgressBar taskProgressBar;

            private TextView deleteBtn;

            private SlidingMenu slidingMenu;

            ViewHolder(View view) {
                super(view);
                taskRowContent = view.findViewById(R.id.ll_task_row_content);
                taskNameView = view.findViewById(R.id.tv_task_name);
                startDateView = view.findViewById(R.id.tv_start_date);
                taskInfoContent = view.findViewById(R.id.ll_task_info);
                managerNameView = view.findViewById(R.id.tv_manager_name);
                totalView = view.findViewById(R.id.tv_total);
                taskProgressBar = view.findViewById(R.id.pb_task_progress);
                deleteBtn = view.findViewById(R.id.tv_delete_btn);
                slidingMenu = view.findViewById(R.id.sliding_menu);
            }

            LinearLayout getTaskRowContent() {
                return taskRowContent;
            }

            TextView getTaskNameView() {
                return taskNameView;
            }

            TextView getStartDateView() {
                return startDateView;
            }

            LinearLayout getTaskInfoContent() {
                return taskInfoContent;
            }

            TextView getManagerNameView() {
                return managerNameView;
            }

            TextView getTotalView() {
                return totalView;
            }

            ProgressBar getTaskProgressBar() {
                return taskProgressBar;
            }

            TextView getDeleteBtn() {
                return deleteBtn;
            }

            SlidingMenu getSlidingMenu() {
                return slidingMenu;
            }
        }
    }
}
