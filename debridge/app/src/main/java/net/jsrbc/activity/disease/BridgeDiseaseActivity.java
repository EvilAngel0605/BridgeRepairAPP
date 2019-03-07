package net.jsrbc.activity.disease;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.bridge.BridgeActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.AnimationType;
import net.jsrbc.enumeration.InspectionDirection;
import net.jsrbc.enumeration.PositionType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.annotation.event.OnQueryText;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeDiseaseMapper;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.NameGenerator;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;
import net.jsrbc.view.SlidingMenu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_bridge_disease)
@AndroidMenu(R.menu.search)
public class BridgeDiseaseActivity extends BaseActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener{

    /** 当前分幅KEY */
    public final static String CURRENT_SIDE = "CURRENT_SIDE";

    /** 当前孔跨KEY */
    public final static String CURRENT_SITE = "CURRENT_SITE";

    /** 当前病害 */
    public final static String CURRENT_DISEASE = "CURRENT_DISEASE";

    /** 部位类型 */
    public final static String POSITION_TYPE = "POSITION_TYPE";

    /** 孔跨集合 */
    public final static String SITE_LIST = "SITE_LIST";

    @AndroidActionView(R.id.action_search)
    @OnQueryText
    private SearchView searchView;

    @AndroidView(R.id.rv_bridge_list)
    private RecyclerView diseaseListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @AndroidView(R.id.tv_button_area)
    private TextView buttonArea;

    @AndroidView(R.id.fab_add)
    @OnClick
    private FloatingActionButton addBtn;

    @AndroidView(R.id.fab_deck)
    @OnClick
    private FloatingActionButton deckBtn;

    @AndroidView(R.id.fab_sub)
    @OnClick
    private FloatingActionButton subBtn;

    @AndroidView(R.id.fab_super)
    @OnClick
    private FloatingActionButton superBtn;

    @AndroidView(R.id.btn_siteNo)
    @OnClick
    private Button siteNoBtn;

    @AndroidView(R.id.fab_next_site)
    @OnClick
    private FloatingActionButton nextSiteBtn;

    @AndroidView(R.id.fab_prev_site)
    @OnClick
    private FloatingActionButton prevSiteBtn;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgeDiseaseMapper bridgeDiseaseMapper;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    /** 检查方向 */
    private InspectionDirection direction;

    /** 当前分幅 */
    private TblBridgeSide currentSide;

    /** 病害集合 */
    private List<TblBridgeDisease> diseaseList = new ArrayList<>();

    /** 病害集合适配器 */
    private DiseaseListAdapter diseaseListAdapter;

    /** 总跨数 */
    private List<TblBridgeSite> bridgeSiteList;

    /** 当前孔跨 */
    private int currentSiteNo;

    /** 是否显示操作按钮 */
    private boolean showActionBtn = false;

    @Override
    protected void created() {
        //初始化数据
        direction = DataUtils.get(this, AndroidConstant.INSPECTION_DIRECTION, InspectionDirection.class);
        if (direction == null) direction = InspectionDirection.LEFT_TO_RIGHT;
        currentSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeActivity.CURRENT_SIDE);
        //设置控件
        setDefaultToolbar();
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(String.format("%s(%s)", currentSide.getBridgeName(), currentSide.getSideTypeName()));
        setRecyclerView();
        initSiteNo();
        fetchDiseaseData();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_add: {
                toggleActionBtn();
                break;
            }
            case R.id.btn_siteNo: {
                showSiteNoPicker();
                break;
            }
            case R.id.fab_next_site: {
                toNextSiteNo();
                break;
            }
            case R.id.fab_prev_site: {
                toPrevSiteNo();
                break;
            }
            case R.id.fab_super: {
                toDiseaseForm(PositionType.SUPER);
                break;
            }
            case R.id.fab_sub: {
                toDiseaseForm(PositionType.SUB);
                break;
            }
            case R.id.fab_deck: {
                toDiseaseForm(PositionType.DECK);
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        searchView.setQueryHint("多个关键字用空格隔开...");
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {return false;}

    @Override
    public boolean onQueryTextChange(String newText) {
        diseaseListAdapter = new DiseaseListAdapter(diseaseList.stream()
                .filter(d->{
                    boolean result = true;
                    String memberDesc = String.format("第%s孔%s    %s(标度%s)", d.getSiteNo(),
                            NameGenerator.generateMemberName(d, direction), d.getDiseaseTypeName(),
                            d.getDeductionScale());
                    String diseaseDesc = NameGenerator.generateDiseaseName(d);
                    for (String str : newText.split(" ")) {
                        if (!memberDesc.contains(str) && !diseaseDesc.contains(str)) result = false;
                    }
                    return result;
                }).collect(Collectors.toList()));
        diseaseListView.setAdapter(diseaseListAdapter);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityRequestCode.GENERAL_REQUEST.getCode() && resultCode == ActivityResultCode.SUCCESS.getCode()) {
            TblBridgeDisease bridgeDisease = (TblBridgeDisease) data.getSerializableExtra(BridgeDiseaseFormActivity.CURRENT_DISEASE);
            bridgeDisease.setDiseasePhotoList(bridgeDiseasePhotoMapper.getDiseasePhotoList(bridgeDisease.getId()));
            int index = diseaseList.indexOf(bridgeDisease);
            if (index < 0) {
                diseaseList.add(0, bridgeDisease);
                diseaseListAdapter.notifyItemInserted(0);
            } else {
                diseaseList.set(index, bridgeDisease);
                diseaseListAdapter.notifyItemChanged(index);
            }
            showEmptyView(ListUtils.isEmpty(diseaseList));
        }
    }

    /** 跳转到病害表单界面 */
    private void toDiseaseForm(PositionType positionType) {
        if (currentSiteNo < 1 || currentSiteNo > bridgeSiteList.size()) {
            SystemUtils.prompt(this, "请跳转到具体孔跨");
        }else {
            Intent intent = new Intent(this, BridgeDiseaseFormActivity.class);
            intent.putExtra(CURRENT_SIDE, currentSide);
            intent.putExtra(CURRENT_SITE, bridgeSiteList.get(currentSiteNo - 1));
            intent.putExtra(POSITION_TYPE, positionType);
            intent.putExtra(SITE_LIST, JsonUtils.toJson(bridgeSiteList));
            startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
        }
    }

    /** 设置病害列表 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        diseaseListAdapter = new DiseaseListAdapter(diseaseList);
        diseaseListView.setLayoutManager(manager);
        diseaseListView.setAdapter(diseaseListAdapter);
    }

    /** 获取病害数据 */
    private void fetchDiseaseData() {
        showProgressDialog();
        new Thread(()->{
            diseaseList.clear();
            List<TblBridgeDisease> temp = bridgeDiseaseMapper.getDiseaseList(currentSide.getTaskId(), currentSide.getBridgeId(), currentSide.getSideTypeId());
            if (temp != null) diseaseList.addAll(temp);
            diseaseList.forEach(d->d.setDiseasePhotoList(bridgeDiseasePhotoMapper.getDiseasePhotoList(d.getId())));
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                showEmptyView(ListUtils.isEmpty(diseaseList));
                diseaseListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 显示孔跨选择器 */
    private void showSiteNoPicker() {
        if (ListUtils.isEmpty(bridgeSiteList)) {
            SystemUtils.prompt(this, "未发现孔跨信息，请前往构件管理添加孔跨");
        } else {
            NumberPicker siteNoPicker = new NumberPicker(this);
            siteNoPicker.setMinValue(1);
            siteNoPicker.setMaxValue(bridgeSiteList.size());
            siteNoPicker.setValue(currentSiteNo);
            //构建并显示对话框
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("跳转到")
                    .setView(siteNoPicker)
                    .setPositiveButton("跳转", (d, i) -> {
                        currentSiteNo = siteNoPicker.getValue();
                        setSiteNoBtn(bridgeSiteList.get(currentSiteNo-1));
                    })
                    .setNegativeButton("取消", null)
                    .create();
            dialog.show();
        }
    }

    /** 初始化孔号 */
    private void initSiteNo() {
        new Thread(()->{
            bridgeSiteList = bridgeSiteMapper.getBridgeSiteListBySide(currentSide.getBridgeId(), currentSide.getSideTypeId());
            AndroidConstant.HANDLER.post(()->{
                if (ListUtils.isEmpty(bridgeSiteList)) {
                    siteNoBtn.setText("无孔跨");
                } else {
                    currentSiteNo = bridgeSiteList.get(0).getSiteNo();
                    setSiteNoBtn(bridgeSiteList.get(0));
                }
            });
        }).start();
    }

    /** 跳转到下一孔跨 */
    private void toNextSiteNo() {
        if (ListUtils.isEmpty(bridgeSiteList)) {
            SystemUtils.prompt(this, "未发现孔跨信息，请前往构件管理添加孔跨");
            return;
        }
        TblBridgeSite bridgeSite = currentSiteNo >= bridgeSiteList.size() ? bridgeSiteList.get(0) : bridgeSiteList.get(currentSiteNo);
        currentSiteNo = bridgeSite.getSiteNo();
        setSiteNoBtn(bridgeSite);
    }

    /** 回退到前一孔跨 */
    private void toPrevSiteNo() {
        if (ListUtils.isEmpty(bridgeSiteList)) {
            SystemUtils.prompt(this, "未发现孔跨信息，请前往构件管理添加孔跨");
            return;
        }
        TblBridgeSite bridgeSite = currentSiteNo <= 1 ? bridgeSiteList.get(bridgeSiteList.size()-1) : bridgeSiteList.get(currentSiteNo-2);
        currentSiteNo = bridgeSite.getSiteNo();
        setSiteNoBtn(bridgeSite);
    }

    /** 设置最大孔号按钮 */
    private void setSiteNoBtn(TblBridgeSite bridgeSite) {
        StringBuilder builder = new StringBuilder();
        if (bridgeSite.getJointNo() > 0) builder.append(bridgeSite.getJointNo()).append("-");
        builder.append(bridgeSite.getSiteNo()).append("/").append(bridgeSiteList.size());
        siteNoBtn.setText(String.valueOf(builder.toString()));
    }

    /** 空列表视图 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 切换添加按钮状态 */
    private void toggleActionBtn() {
        showActionBtn = !showActionBtn;
        if (showActionBtn) {
            showActionBtn();
            buttonArea.setClickable(true);
        } else {
            buttonArea.setClickable(false);
        }
        List<Animator> animators = new ArrayList<>();
        View[] views = new View[] {superBtn, subBtn, deckBtn, nextSiteBtn, prevSiteBtn, siteNoBtn};
        int startAngle = showActionBtn?-20:20;
        int endAngle = showActionBtn?315:-270;
        int startScale = showActionBtn?0:1;
        int endScale = showActionBtn?1:0;
        ObjectAnimator addBtnAnimator = ObjectAnimator.ofFloat(addBtn, AnimationType.ROTATION.getValue(), startAngle, 0, endAngle);
        ObjectAnimator btnAreaAnimator = ObjectAnimator.ofFloat(buttonArea, AnimationType.ALPHA.getValue(), startScale, endScale);
        animators.add(addBtnAnimator);
        animators.add(btnAreaAnimator);
        for (View view : views) {
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(view, AnimationType.SCALE_X.getValue(),startScale,endScale);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(view, AnimationType.SCALE_Y.getValue(),startScale,endScale);
            animators.add(animator1);
            animators.add(animator2);
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(animators);
        animatorSet.start();
    }

    /** 显示操作按钮 */
    private void showActionBtn() {
        buttonArea.setVisibility(View.VISIBLE);
        superBtn.setVisibility(View.VISIBLE);
        subBtn.setVisibility(View.VISIBLE);
        deckBtn.setVisibility(View.VISIBLE);
        nextSiteBtn.setVisibility(View.VISIBLE);
        prevSiteBtn.setVisibility(View.VISIBLE);
        siteNoBtn.setVisibility(View.VISIBLE);
    }

    /** 病害列表 */
    private class DiseaseListAdapter extends RecyclerView.Adapter<DiseaseListAdapter.ViewHolder> {

        private List<TblBridgeDisease> diseaseList;

        private HashSet<SlidingMenu> prevSlidingMenus = new HashSet<>();

        DiseaseListAdapter(List<TblBridgeDisease> diseaseList) {
            this.diseaseList = diseaseList;
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disease_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridgeDisease bridgeDisease = diseaseList.get(position);
            if (ListUtils.isEmpty(bridgeDisease.getDiseasePhotoList())) {
                holder.getNoPhotoView().setVisibility(View.VISIBLE);
                holder.getPhotoView().setVisibility(View.GONE);
            } else {
                holder.getNoPhotoView().setVisibility(View.GONE);
                holder.getPhotoView().setVisibility(View.VISIBLE);
                loadThumbnail(holder.getPhotoView(), bridgeDisease.getDiseasePhotoList().get(0).getPath());
            }
            String memberDesc = String.format("第%s孔%s    %s(标度%s)", bridgeDisease.getSiteNo(),
                    NameGenerator.generateMemberName(bridgeDisease, direction), bridgeDisease.getDiseaseTypeName(),
                    bridgeDisease.getDeductionScale());
            holder.getMemberDescView().setText(memberDesc);
            holder.getDiseaseDescView().setText(NameGenerator.generateDiseaseName(bridgeDisease));
            holder.getRecordDateView().setText(bridgeDisease.getRecordDate());

            //对构件缺失的病害打上标记
            if (StringUtils.isEmpty(bridgeDisease.getBridgeMemberId())) holder.getWrapper().setBackgroundResource(R.drawable.ripple_member_missing);
            else holder.getWrapper().setBackgroundResource(R.drawable.ripple_white);

            holder.getDeleteBtn().setOnClickListener(v-> SystemUtils.confirm(BridgeDiseaseActivity.this, "确定删除？", (d, i)->{
                bridgeDiseaseMapper.deleteDisease(bridgeDisease.getId());
                diseaseList.remove(bridgeDisease);
                notifyItemRemoved(holder.getAdapterPosition());
                showEmptyView(ListUtils.isEmpty(diseaseList));
                closeOpenedMenu(null);
            }));

            holder.getCopyBtn().setOnClickListener(v->{
                Intent intent = new Intent(BridgeDiseaseActivity.this, BridgeDiseaseFormActivity.class);
                TblBridgeDisease disease = bridgeDisease.clone();
                disease.setId(StringUtils.createId());
                intent.putExtra(CURRENT_SIDE, currentSide);
                intent.putExtra(CURRENT_SITE, bridgeSiteList.get(currentSiteNo - 1));
                intent.putExtra(POSITION_TYPE, PositionType.of(disease.getPositionTypeName()));
                intent.putExtra(CURRENT_DISEASE, disease);
                intent.putExtra(SITE_LIST, JsonUtils.toJson(bridgeSiteList));
                startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
                closeOpenedMenu(null);
            });

            holder.getWrapper().setOnClickListener(v->{
                if (!prevSlidingMenus.isEmpty()) {
                    closeOpenedMenu(null);
                } else {
                    Intent intent = new Intent(BridgeDiseaseActivity.this, BridgeDiseaseFormActivity.class);
                    intent.putExtra(CURRENT_SIDE, currentSide);
                    intent.putExtra(CURRENT_SITE, bridgeSiteList.get(currentSiteNo - 1));
                    intent.putExtra(POSITION_TYPE, PositionType.of(bridgeDisease.getPositionTypeName()));
                    intent.putExtra(CURRENT_DISEASE, bridgeDisease);
                    intent.putExtra(SITE_LIST, JsonUtils.toJson(bridgeSiteList));
                    startActivityForResult(intent, ActivityRequestCode.GENERAL_REQUEST.getCode());
                }
            });

            holder.getSlidingMenu().setOnMenuSlidingListener(m->{
                closeOpenedMenu(m);
                prevSlidingMenus.add(m);
            });
        }

        @Override
        public int getItemCount() {
            return diseaseList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private SlidingMenu slidingMenu;

            private LinearLayout wrapper;

            private ImageView photoView;

            private TextView noPhotoView;

            private TextView memberDescView;

            private TextView recordDateView;

            private TextView diseaseDescView;

            private TextView copyBtn;

            private TextView deleteBtn;

            ViewHolder(View view) {
                super(view);
                slidingMenu = view.findViewById(R.id.sliding_menu);
                wrapper = view.findViewById(R.id.ll_wrapper);
                photoView = view.findViewById(R.id.iv_disease_photo);
                noPhotoView = view.findViewById(R.id.tv_no_photo);
                memberDescView = view.findViewById(R.id.tv_member_desc);
                recordDateView = view.findViewById(R.id.tv_record_date);
                diseaseDescView = view.findViewById(R.id.tv_disease_desc);
                copyBtn = view.findViewById(R.id.tv_copy_btn);
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

            TextView getMemberDescView() {
                return memberDescView;
            }

            TextView getRecordDateView() {
                return recordDateView;
            }

            TextView getDiseaseDescView() {
                return diseaseDescView;
            }

            TextView getCopyBtn() {
                return copyBtn;
            }

            TextView getDeleteBtn() {
                return deleteBtn;
            }
        }
    }
}
