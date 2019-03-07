package net.jsrbc.activity.task;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.client.DownloadClient;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblTask;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.frame.annotation.client.HttpClient;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnCheckedChange;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.frame.exception.NonAuthoritativeException;
import net.jsrbc.mapper.TaskMapper;
import net.jsrbc.service.DownloadService;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.SystemUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by ZZZ on 2017-12-02.
 */
@AndroidActivity(R.layout.activity_task_download)
@AndroidMenu(R.menu.search)
public class TaskDownloadActivity extends BaseActivity
        implements android.support.v7.widget.SearchView.OnQueryTextListener,
        CompoundButton.OnCheckedChangeListener, View.OnClickListener{

    /** 下载任务单的标识 */
    public static final String DOWNLOAD_TASK = UUID.randomUUID().toString();

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    private SearchView searchView;

    @AndroidView(R.id.rv_task_list)
    private RecyclerView taskListView;

    @AndroidView(R.id.cb_selectAll)
    @OnCheckedChange
    private CheckBox selectAllView;

    @AndroidView(R.id.tv_count)
    private TextView selectCountView;

    @AndroidView(R.id.tv_remain)
    private TextView remainView;

    @AndroidView(R.id.tv_download)
    @OnClick
    private TextView downloadBtn;

    @AndroidView(R.id.no_task_tips)
    private TextView noTaskTips;

    @HttpClient
    private DownloadClient downloadClient;

    @Mapper
    private TaskMapper taskMapper;

    private TaskListAdapter taskListAdapter;

    private List<TblTask> allTasks = new ArrayList<>();

    @Override
    protected void created() {
        setDefaultToolbar();
        setTaskListView();
        fetchTaskList();
    }

    /** 设置任务单视图 */
    public void setTaskListView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        taskListView.setLayoutManager(layoutManager);
        taskListAdapter = new TaskListAdapter(allTasks);
        taskListView.setAdapter(taskListAdapter);
    }

    /** 获取任务单列表 */
    public void fetchTaskList() {
        showProgressDialog();
        new Thread(()->{
            try {
                allTasks.clear();
                allTasks.addAll(downloadClient.getTaskList());
                AndroidConstant.HANDLER.post(()->{
                    hideProgressDialog();
                    taskListAdapter.notifyDataSetChanged();
                });
            }catch (IOException e) {
                hideProgressDialog();
                AndroidConstant.HANDLER.post(()->SystemUtils.prompt(TaskDownloadActivity.this, "网络异常，请稍后再试"));
            }catch (NonAuthoritativeException e) {
                hideProgressDialog();
                AndroidConstant.HANDLER.post(()->SystemUtils.sessionTimeout(TaskDownloadActivity.this));
            }finally {
                AndroidConstant.HANDLER.post(()->{
                    showNoTaskTips(taskListAdapter.getList().isEmpty());
                    updateView();
                });
            }
        }).start();
    }

    /** 是否显示无任务单的提示 */
    private void showNoTaskTips(boolean isShow) {
        if (isShow) noTaskTips.setVisibility(View.VISIBLE);
        else noTaskTips.setVisibility(View.INVISIBLE);
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
            case R.id.tv_download: {
                List<TblTask> selectedTasks = taskListAdapter.getList().stream().filter(TblTask::isChecked).collect(Collectors.toList());
                if (selectedTasks.isEmpty()) {
                    SystemUtils.prompt(this, "未选择需要下载的任务单");
                } else {
                    SystemUtils.confirm(this, String.format("确定下载选中的%s个任务单", selectedTasks.size()), (d, i) -> {
                        taskMapper.addOrReplaceTaskList(selectedTasks);
                        Intent intent = new Intent(TaskDownloadActivity.this, DownloadService.class);
                        intent.putExtra(DOWNLOAD_TASK, JsonUtils.toJson(selectedTasks));
                        startService(intent);
                        setResult(ActivityResultCode.SUCCESS.getCode());
                        finish();
                    });
                }
                break;
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (compoundButton.getId() == R.id.cb_selectAll) {
            taskListAdapter.getList().forEach(task->task.setChecked(isChecked));
            taskListAdapter.notifyDataSetChanged();
            updateView();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        taskListAdapter = new TaskListAdapter(allTasks.stream()
                .filter(t-> t.getName().contains(newText) || t.getManagerName().contains(newText))
                .collect(Collectors.toList()));
        taskListView.setAdapter(taskListAdapter);
        updateView();
        return false;
    }

    /** 更新所有视图 */
    private void updateView() {
        long count = taskListAdapter.getList().stream().filter(TblTask::isChecked).count();
        String selectCount = "已选中：" + count;
        String remainCount = "任务单总数：" + taskListAdapter.getItemCount();
        selectCountView.setText(selectCount);
        remainView.setText(remainCount);
    }

    /** 获取任务单列表适配器 */
    private class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

        private List<TblTask> taskList;

        TaskListAdapter(List<TblTask> taskList) {
            this.taskList = taskList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task_list_download, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblTask currentTask = taskList.get(position);
            String managerName = "负责人：" + currentTask.getManagerName();
            String total = "桥梁总数：" + currentTask.getTotal();
            holder.getTaskSelectView().setChecked(currentTask.isChecked());
            holder.getTaskNameView().setText(currentTask.getName());
            holder.getManagerNameView().setText(managerName);
            holder.getTotalView().setText(total);
            holder.getStartDateView().setText(currentTask.getStartDate());
            holder.getTaskRowView().setOnClickListener(view->{
                currentTask.setChecked(!currentTask.isChecked());
                taskListAdapter.notifyItemChanged(position);
                updateView();
            });
        }

        @Override
        public int getItemCount() {
            return taskList.size();
        }

        public List<TblTask> getList() {
            return this.taskList;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout taskRowView;

            private CheckBox taskSelectView;

            private TextView taskNameView;

            private TextView managerNameView;

            private TextView totalView;

            private TextView startDateView;

            ViewHolder(View view) {
                super(view);
                taskRowView = view.findViewById(R.id.ll_task_row);
                taskSelectView = view.findViewById(R.id.cb_task_select);
                taskNameView = view.findViewById(R.id.tv_task_name);
                managerNameView = view.findViewById(R.id.tv_manager_name);
                totalView = view.findViewById(R.id.tv_total);
                startDateView = view.findViewById(R.id.tv_start_date);
            }

            LinearLayout getTaskRowView() {
                return taskRowView;
            }

            CheckBox getTaskSelectView() {
                return taskSelectView;
            }

            TextView getTaskNameView() {
                return taskNameView;
            }

            TextView getManagerNameView() {
                return managerNameView;
            }

            TextView getTotalView() {
                return totalView;
            }

            TextView getStartDateView() {
                return startDateView;
            }
        }
    }
}
