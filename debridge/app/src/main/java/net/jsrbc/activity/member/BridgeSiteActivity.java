package net.jsrbc.activity.member;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.side.BridgeSideActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.enumeration.ActivityRequestCode;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@AndroidActivity(R.layout.activity_bridge_site)
@AndroidMenu(R.menu.activity_bridge_site_menu)
public class BridgeSiteActivity extends BaseActivity {

    /** 传出去的参数KEY */
    public final static String CURRENT_SIDE = "CURRENT_SIDE";

    public final static String CURRENT_SITE = "CURRENT_SITE";

    public final static String CURRENT_PARTS_LIST = "CURRENT_PARTS_LIST";

    @AndroidView(R.id.rv_site_list)
    private RecyclerView siteListView;

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    private TblBridgeSide currentBridgeSide;

    private List<TblBridgeSite> bridgeSiteList = new ArrayList<>();

    private SiteListAdapter siteListAdapter;

    @Override
    protected void created() {
        this.currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeSideActivity.CURRENT_SIDE);
        setDefaultToolbar();
        if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentBridgeSide.getSideTypeName());
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchSiteData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        switch (item.getItemId()) {
            case R.id.mi_span_combination:
                intent = new Intent(this, SpanCombinationFormActivity.class);
                break;
            case R.id.mi_superstructure:
                intent = new Intent(this, SuperstructureFormActivity.class);
                break;
            case R.id.mi_pier:
                intent = new Intent(this, PierFormActivity.class);
                break;
            case R.id.mi_abutment:
                intent = new Intent(this, AbutmentFormActivity.class);
                break;
            case R.id.mi_riverbed:
                intent = new Intent(this, SiteRangeFormActivity.class);
                intent.putExtra(CURRENT_PARTS_LIST, JsonUtils.toJson(bridgePartsMapper.getBridgePartsListByRiverbed()));
                break;
            case R.id.mi_regulating:
                intent = new Intent(this, SiteRangeFormActivity.class);
                intent.putExtra(CURRENT_PARTS_LIST, JsonUtils.toJson(bridgePartsMapper.getBridgePartsListByRegulatingStructure()));
                break;
            case R.id.mi_deck:
                intent = new Intent(this, DeckFormActivity.class);
                break;
            case R.id.mi_expansion_joint:
                intent = new Intent(this, ExpansionJointFormActivity.class);
                break;
        }
        //传入的参数
        if (intent != null) {
            intent.putExtra(CURRENT_SIDE, currentBridgeSide);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityRequestCode.GENERAL_REQUEST.getCode()) {
            switch (ActivityResultCode.of(resultCode)) {
                case SUCCESS:
                    fetchSiteData();
                    siteListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    /** 是否显示列表为空的提示 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 设置列表视图 */
    private void setRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        siteListView.setLayoutManager(manager);
        siteListAdapter = new SiteListAdapter(bridgeSiteList);
        siteListView.setAdapter(siteListAdapter);
    }

    /** 获取孔跨数据 */
    private void fetchSiteData() {
        showProgressDialog();
        new Thread(()->{
            bridgeSiteList.clear();
            List<TblBridgeSite> temp = bridgeSiteMapper.getBridgeSiteListBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
            if (temp != null) bridgeSiteList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                showEmptyView(bridgeSiteList.isEmpty());
                siteListAdapter.notifyDataSetChanged();
            });
        }).start();
    }

    /** 孔跨列表适配器 */
    private class SiteListAdapter extends RecyclerView.Adapter<SiteListAdapter.ViewHolder> {

        private List<TblBridgeSite> siteList;

        private SiteListAdapter(List<TblBridgeSite> siteList) {
            this.siteList = siteList;
        }

        @Override
        public SiteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_site_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SiteListAdapter.ViewHolder holder, int position) {
            TblBridgeSite currentSite = siteList.get(position);
            String siteInfo = StringUtils.parseSiteName(currentSite.getJointNo(), currentSite.getSiteNo()) + "    " + (StringUtils.isEmpty(currentSite.getSuperstructureTypeName())?"":currentSite.getSuperstructureTypeName());
            String spanDesc = StringUtils.sizeToStr(currentSite.getSpan(), RoundingMode.HALF_UP) + "m";
            String materialTypeName = "上部结构材料类型：" + (StringUtils.isEmpty(currentSite.getSuperstructureMaterialTypeName())?"未知":currentSite.getSuperstructureMaterialTypeName());
            String memberCountName =  "构件总数：" + currentSite.getMemberCount();
            holder.getSiteInfoView().setText(siteInfo);
            holder.getSiteSpanView().setText(spanDesc);
            holder.getMaterialTypeName().setText(materialTypeName);
            holder.getMemberCountView().setText(memberCountName);
            holder.getSiteContent().setOnClickListener(v->{
                Intent intent = new Intent(BridgeSiteActivity.this, BridgePartsActivity.class);
                intent.putExtra(CURRENT_SITE, currentSite);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return siteList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private LinearLayout siteContent;

            private TextView siteInfoView;

            private TextView siteSpanView;

            private TextView materialTypeName;

            private TextView memberCountView;

            private ViewHolder(View view) {
                super(view);
                siteContent = view.findViewById(R.id.ll_site_content);
                siteInfoView = view.findViewById(R.id.tv_site_info);
                siteSpanView = view.findViewById(R.id.tv_site_span);
                materialTypeName = view.findViewById(R.id.tv_material_type_name);
                memberCountView = view.findViewById(R.id.tv_member_count);
            }

            LinearLayout getSiteContent() {
                return siteContent;
            }

            TextView getSiteInfoView() {
                return siteInfoView;
            }

            TextView getSiteSpanView() {
                return siteSpanView;
            }

            TextView getMaterialTypeName() {
                return materialTypeName;
            }

            TextView getMemberCountView() {
                return memberCountView;
            }
        }
    }
}
