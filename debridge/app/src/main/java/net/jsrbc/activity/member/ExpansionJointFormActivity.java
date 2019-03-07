package net.jsrbc.activity.member;

import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblParamStructureType;
import net.jsrbc.enumeration.StakeSide;
import net.jsrbc.enumeration.StructureGroupField;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.mapper.BridgeMemberMapper;
import net.jsrbc.mapper.BridgePartsMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.pojo.BridgePartsForm;
import net.jsrbc.utils.MemberUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.stream.Stream;

import static net.jsrbc.utils.MemberUtils.parseSiteRange;

@AndroidActivity(R.layout.activity_expansion_joint_form)
@AndroidMenu(R.menu.save)
public class ExpansionJointFormActivity extends BaseActivity {

    @AndroidView(R.id.et_site_range)
    private EditText siteRangeInput;

    @AndroidView(R.id.sp_expansion_joint_type)
    private Spinner expansionJointTypeSelector;

    @Mapper
    private BaseDataMapper baseDataMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    private TblBridgeSide currentBridgeSide;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
        setExpansionJointTypeSelector();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save:
                if (validate()) addExpansionJoint();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** 添加伸缩缝部构件 */
    private void addExpansionJoint() {
        showProgressDialog();
        new Thread(()->{
            List<TblBridgeParts> bridgePartsList = bridgePartsMapper.getBridgePartsListByExpansionJoint();
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .siteRange(siteRangeInput.getText().toString())
                    .structureTypeId(((TblParamStructureType)expansionJointTypeSelector.getSelectedItem()).getId())
                    .partsList(bridgePartsList)
                    .build();
            HashSet<Integer> siteNoSet = parseSiteRange(bridgePartsForm.getSiteRange());
            if (siteNoSet == null) return;
            //补充一下构件及结构类型
            bridgePartsForm.getPartsList().forEach(item->{
                item.setStructureTypeId(bridgePartsForm.getStructureTypeId());
                item.setHorizontalCount(1);
            });
            //考虑桥梁只有一孔的情况，对称添加
            if (siteNoSet.contains(0) && siteNoSet.contains(1)) {
                //先添加1孔的情况
                bridgePartsForm.getPartsList().forEach(item->item.setStakeSide(StakeSide.BOTH_SIDE.getCode()));
                MemberUtils.addBridgeParts(bridgePartsForm, Stream.of(1),
                        ()-> siteNoSet.forEach(siteNo->{
                            bridgePartsMapper.deleteExpansionJointPartsRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                            bridgeMemberMapper.deleteExpansionJointMemberRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                        }),
                        (pList, mList)->{
                            bridgePartsMapper.addOrReplaceBridgePartsList(pList);
                            bridgeMemberMapper.addOrReplaceBridgeMemberList(mList);
                        });
                //再移除第1孔情况
                siteNoSet.remove(0);
                siteNoSet.remove(1);
                bridgePartsForm.getPartsList().forEach(item->item.setStakeSide(StakeSide.LARGER_SIDE.getCode()));
            }
            //再考虑其他孔的情况
            if (siteNoSet.size() > 0) {
                MemberUtils.addBridgeParts(bridgePartsForm, siteNoSet.stream(),
                        ()-> siteNoSet.forEach(siteNo->{
                            bridgePartsMapper.deleteExpansionJointPartsRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                            bridgeMemberMapper.deleteExpansionJointMemberRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                        }),
                        (pList, mList)->{
                            bridgePartsMapper.addOrReplaceBridgePartsList(pList);
                            bridgeMemberMapper.addOrReplaceBridgeMemberList(mList);
                        });
            }
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }

    /** 设置伸缩缝类型 */
    private void setExpansionJointTypeSelector() {
        List<TblParamStructureType> expansionJointTypeList = baseDataMapper.getStructureTypeByGroupField(StructureGroupField.EXPANSION_JOINT_TYPE);
        expansionJointTypeList.add(0, TblParamStructureType.empty());
        ArrayAdapter<TblParamStructureType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, expansionJointTypeList);
        expansionJointTypeSelector.setAdapter(adapter);
    }

    /** 校验 */
    private boolean validate() {
        //判断输入的是否匹配规则
        String siteRange = siteRangeInput.getText().toString();
        if (!siteRange.matches("(\\d+(,\\d+|(-\\d+))?,?)+")) {
            siteRangeInput.requestFocus();
            siteRangeInput.setError("格式错误,只能包含数字、英文逗号及'-'");
            return false;
        }

        //判断输入孔跨是否为空
        HashSet<Integer> siteNos = MemberUtils.parseSiteRange(siteRange);
        if (siteNos == null) {
            siteRangeInput.requestFocus();
            siteRangeInput.setError("输入的孔号不能为空");
            return false;
        }

        //判断孔号有没有超出范围
        IntSummaryStatistics statistics = siteNos.stream().mapToInt(s->s).summaryStatistics();
        if (statistics.getMin() < 0) {
            siteRangeInput.requestFocus();
            siteRangeInput.setError("必须为不小于0的整数");
            return false;
        }
        int siteCount = bridgeSiteMapper.getSiteCountBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
        if (statistics.getMax() > siteCount) {
            siteRangeInput.requestFocus();
            siteRangeInput.setError(String.format("超出最大墩台号：%s", siteCount));
            return false;
        }

        if (expansionJointTypeSelector.getSelectedItem() == TblParamStructureType.empty()) {
            SystemUtils.prompt(this, "伸缩缝类型未选择");
            return false;
        }
        return true;
    }
}
