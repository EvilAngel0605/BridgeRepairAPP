package net.jsrbc.activity.member;

import android.view.MenuItem;
import android.widget.EditText;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.pojo.BridgePartsForm;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.StringUtils;

import java.util.List;
import java.util.stream.Stream;

@AndroidActivity(R.layout.activity_site_range_form)
@AndroidMenu(R.menu.save)
public class SiteRangeFormActivity extends BaseActivity {

    @AndroidView(R.id.et_start_site_no)
    private EditText startSiteNoInput;

    @AndroidView(R.id.et_end_site_no)
    private EditText endSiteNoInput;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private List<TblBridgeParts> currentBridgePartsList;

    private TblBridgeSide currentBridgeSide;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentBridgePartsList = JsonUtils.toList(getIntent().getStringExtra(BridgeSiteActivity.CURRENT_PARTS_LIST), TblBridgeParts.class);
        currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save:
                if (validate()) addSiteRange();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** 添加孔跨范围的部构件 */
    private void addSiteRange() {
        showProgressDialog();
        new Thread(()->{
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .startSiteNo(StringUtils.toInt(startSiteNoInput.getText().toString()))
                    .endSiteNo(StringUtils.toInt(endSiteNoInput.getText().toString()))
                    .partsList(currentBridgePartsList)
                    .build();
            if (bridgePartsForm.getEndSiteNo() == 0 || bridgePartsForm.getPartsList().size() == 0) return;
            if (bridgePartsForm.getStartSiteNo() == 0) bridgePartsForm.setStartSiteNo(1);
            for (TblBridgeParts bridgeParts : bridgePartsForm.getPartsList()) {
                bridgeParts.setHorizontalCount(1);
            }
            MemberUtils.addBridgeParts(bridgePartsForm, Stream.iterate(bridgePartsForm.getStartSiteNo(), i->++i).limit(bridgePartsForm.getEndSiteNo()-bridgePartsForm.getStartSiteNo()+1),
                    ()->{
                        bridgePartsMapper.deletePartsRangeOf(bridgePartsForm.getBridgeId(), bridgePartsForm.getSideTypeId(), bridgePartsForm.getPartsList().get(0).getPartsTypeId(), bridgePartsForm.getStartSiteNo(), bridgePartsForm.getEndSiteNo());
                        bridgeMemberMapper.deleteMemberRangeOf(bridgePartsForm.getBridgeId(), bridgePartsForm.getSideTypeId(), bridgePartsForm.getPartsList().get(0).getPartsTypeId(), bridgePartsForm.getStartSiteNo(), bridgePartsForm.getEndSiteNo());
                    },
                    (pList, mList)->{
                        bridgePartsMapper.addOrReplaceBridgePartsList(pList);
                        bridgeMemberMapper.addOrReplaceBridgeMemberList(mList);
                    });
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }

    /** 校验 */
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
        return true;
    }
}
