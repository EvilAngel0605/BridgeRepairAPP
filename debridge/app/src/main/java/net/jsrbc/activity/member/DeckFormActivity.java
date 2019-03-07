package net.jsrbc.activity.member;

import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.MemberType;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.enumeration.PartsType;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_deck_form)
@AndroidMenu(R.menu.save)
public class DeckFormActivity extends BaseActivity {

    @AndroidView(R.id.et_site_range)
    private EditText siteRangeInput;

    @AndroidView(R.id.sp_material_type)
    private Spinner materialTypeSelector;

    @AndroidView(R.id.cb_side_walk_left)
    private CheckBox sideWalkLeftCheck;

    @AndroidView(R.id.cb_side_walk_right)
    private CheckBox sideWalkRightCheck;

    @AndroidView(R.id.cb_handrail_left)
    private CheckBox handrailLeftCheck;

    @AndroidView(R.id.cb_handrail_right)
    private CheckBox handrailRightCheck;

    @AndroidView(R.id.cb_guardrail_left)
    private CheckBox guardrailLeftCheck;

    @AndroidView(R.id.cb_guardrail_right)
    private CheckBox guardrailRightCheck;

    @AndroidView(R.id.cb_drainage)
    private CheckBox drainageCheck;

    @AndroidView(R.id.cb_illumination)
    private CheckBox illuminationCheck;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    @Mapper
    private BridgePartsMapper bridgePartsMapper;

    @Mapper
    private BridgeMemberMapper bridgeMemberMapper;

    @Mapper
    private BaseDataMapper baseDataMapper;

    private List<TblBridgeParts> deckPartsList;

    private TblBridgeSide currentBridgeSide;

    @Override
    protected void created() {
        setDefaultToolbar();
        setMaterialTypeSelector();
        currentBridgeSide = (TblBridgeSide)getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
        deckPartsList = bridgePartsMapper.getBridgePartsListByDeck();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save:
                if (validate()) addDeck();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /** 添加桥面系构件 */
    private void addDeck() {
        showProgressDialog();
        new Thread(()->{
            List<TblBridgeParts> bridgePartsList = new ArrayList<>();
            //桥面铺装
            TblParamType materialType = (TblParamType)materialTypeSelector.getSelectedItem();
            List<TblBridgeParts> deckPavementParts = deckPartsList.stream().filter(p->p.getMemberTypeName().equals(MemberType.DECK_PAVEMENT.getValue())).collect(Collectors.toList());
            deckPavementParts.forEach(p->{
                p.setMaterialTypeId(materialType.getId());
                p.setMaterialTypeName(materialType.getName());
            });
            bridgePartsList.addAll(deckPavementParts);
            //其他checkbox选择的构件
            bridgePartsList.addAll(getBridgePartsFromCheckBox(MemberType.SIDE_WALK, sideWalkLeftCheck, sideWalkRightCheck));
            bridgePartsList.addAll(getBridgePartsFromCheckBox(MemberType.HANDRAIL, handrailLeftCheck, handrailRightCheck));
            bridgePartsList.addAll(getBridgePartsFromCheckBox(MemberType.GUARDRAIL, guardrailLeftCheck, guardrailRightCheck));
            bridgePartsList.addAll(getBridgePartsFromCheckBox(MemberType.DRAINAGE, drainageCheck));
            bridgePartsList.addAll(getBridgePartsFromCheckBox(MemberType.ILLUMINATION, illuminationCheck));
            //构件表单对象
            String siteRange = siteRangeInput.getText().toString();
            BridgePartsForm bridgePartsForm = new BridgePartsForm.Builder()
                    .bridgeId(currentBridgeSide.getBridgeId())
                    .sideTypeId(currentBridgeSide.getSideTypeId())
                    .siteRange(siteRange)
                    .partsList(bridgePartsList)
                    .build();
            HashSet<Integer> siteNoSet = MemberUtils.parseSiteRange(bridgePartsForm.getSiteRange());
            if (siteNoSet == null) return;
            //清除0孔
            siteNoSet.remove(0);
            //补充一个桥面铺装进去
            Optional<TblBridgeParts> optional = bridgePartsForm.getPartsList().stream().filter(item->item.getMemberTypeName().equals(MemberType.DECK_PAVEMENT.getValue())).findAny();
            optional.ifPresent(bridgeParts -> bridgeParts.setHorizontalCount(1));
            //孔跨解析出来后，添加部构件
            MemberUtils.addBridgeParts(bridgePartsForm, siteNoSet.stream(),
                    ()-> siteNoSet.forEach(siteNo->{
                        bridgePartsMapper.deleteDeckPartsRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                        bridgeMemberMapper.deleteDeckMemberRangeOf(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), siteNo);
                    }),
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

    /** 设置材料类型选择器 */
    private void setMaterialTypeSelector() {
        List<TblParamType> materialTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.MATERIAL_TYPE);
        materialTypeList.add(0, TblParamType.empty());
        ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, materialTypeList);
        materialTypeSelector.setAdapter(adapter);
    }

    /** 设置桥面系选择的东西 */
    private List<TblBridgeParts> getBridgePartsFromCheckBox(MemberType memberType, CheckBox... checkBoxes) {
        List<TblBridgeParts> partsList = deckPartsList.stream().filter(p->p.getMemberTypeName().equals(memberType.getValue())).collect(Collectors.toList());
        if (partsList == null) return Collections.emptyList();
        StringBuilder builder = new StringBuilder();
        for (CheckBox cb : checkBoxes) if (cb.isChecked()) builder.append(cb.getText()).append(",");
        if (builder.length() > 0) builder.deleteCharAt(builder.length()-1);
        partsList.forEach(p->p.setSpecialSection(builder.toString()));
        return partsList;
    }

    /** 校验输入的内容 */
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
            siteRangeInput.setError(String.format("超出最大孔号：%s", siteCount));
            return false;
        }

        if (materialTypeSelector.getSelectedItem() == TblParamType.empty()) {
            SystemUtils.prompt(this, "桥面铺装材料类型未选择");
            return false;
        }
        return true;
    }
}
