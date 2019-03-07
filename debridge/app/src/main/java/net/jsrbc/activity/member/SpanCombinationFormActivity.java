package net.jsrbc.activity.member;

import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.entity.TblBridgeSite;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeSideMapper;
import net.jsrbc.mapper.BridgeSiteMapper;
import net.jsrbc.pojo.Result;
import net.jsrbc.utils.JsonUtils;
import net.jsrbc.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.jsrbc.pojo.Result.Status.ERROR;
import static net.jsrbc.pojo.Result.Status.SUCCESS;
import static net.jsrbc.utils.UnitConverter.metreToMillimetre;

@AndroidActivity(R.layout.activity_span_combination_form)
@AndroidMenu(R.menu.save)
public class SpanCombinationFormActivity extends BaseActivity {

    @AndroidView(R.id.et_span_combination)
    private EditText spanCombinationInput;

    @AndroidView(R.id.tv_result)
    private TextView resultView;

    @Mapper
    private BridgeSideMapper bridgeSideMapper;

    @Mapper
    private BridgeSiteMapper bridgeSiteMapper;

    private TblBridgeSide currentBridgeSide;

    @Override
    protected void created(){
        setDefaultToolbar();
        this.currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(BridgeSiteActivity.CURRENT_SIDE);
        if (!StringUtils.isEmpty(currentBridgeSide.getBridgeSpanCombination())) spanCombinationInput.setText(currentBridgeSide.getBridgeSpanCombination());
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_save: {
                String spanCombination = spanCombinationInput.getText().toString();
                Result result = validateSpanCombination(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId(), spanCombination);
                if (result.getStatus() == ERROR) {
                    spanCombinationInput.setBackground(getDrawable(R.drawable.shape_edit_text_error));
                    resultView.setBackground(getDrawable(R.drawable.shape_edit_text_error));
                    resultView.setTextColor(getColor(R.color.red_light));
                    resultView.setText(result.getMessage());
                } else if (result.getStatus() == SUCCESS) {
                    List<TblBridgeSite> siteList = (List<TblBridgeSite>)result.getExtra();
                    siteList.forEach(s->s.setUpload(UploadStatus.NEED_UPLOAD.getCode()));
                    bridgeSiteMapper.deleteBridgeSiteBySide(currentBridgeSide.getBridgeId(), currentBridgeSide.getSideTypeId());
                    bridgeSiteMapper.addOrReplaceBridgeSiteList(siteList);
                    currentBridgeSide.setBridgeSpanCombination(spanCombination);
                    bridgeSideMapper.updateBridgeSpanCombination(currentBridgeSide);
                    setResult(ActivityResultCode.SUCCESS.getCode());
                    finish();
                }
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /** 校验跨径组合是否合法 */
    public Result validateSpanCombination(String bridgeId, String sideTypeId, String bridgeSpanCombination) {
        Result.Builder result = new Result.Builder();
        //先判断是否含有非法字符
        if (Pattern.compile("[^0-9.()*+]").matcher(bridgeSpanCombination).find()) return result.status(ERROR).message("包含非法字符，跨径组合只能用(数字、英文括号、+号和*号)组合").build();
        //区别是否含有分联符号
        if (bridgeSpanCombination.contains("(") || bridgeSpanCombination.contains(")")) {
            if (!bridgeSpanCombination.matches("^(\\d+\\*)?\\(\\d+(\\*\\d+)?(\\+\\d+(\\*\\d+)?)*\\)(\\+(\\d+\\*)?\\(\\d+(\\*\\d+)?(\\+\\d+(\\*\\d+)?)*\\))*$")) {
                return result.status(ERROR).message("格式错误，多联格式：(孔数*跨径)+(孔数*跨径)...").build();
            }
        } else {
            if (!bridgeSpanCombination.matches("^\\d+(\\*\\d+)?(\\+\\d+(\\*\\d+)?)*$")) {
                return result.status(ERROR).message("格式错误，不分联格式：孔数*跨径+孔数*跨径...").build();
            }
        }
        return result.status(SUCCESS).extra(splitBridgeSpanCombination(bridgeId, sideTypeId, bridgeSpanCombination)).build();
    }

    /** 将分联的跨径组合拆解成孔跨对象集合 */
    private List<TblBridgeSite> splitBridgeSpanCombination(String bridgeId, String sideTypeId, String bridgeSpanCombination) {
        List<TblBridgeSite> bridgeSiteList = new ArrayList<>();
        //区别分不分联的情况
        if (bridgeSpanCombination.contains("(")) {
            Matcher matcher = Pattern.compile("((\\d+)\\*)?\\((.*?)\\)").matcher(bridgeSpanCombination);
            int jointNo = 0;
            TblBridgeSite paramSite = new TblBridgeSite().bridgeId(bridgeId).sideTypeId(sideTypeId).siteNo(0);
            while (matcher.find()) {
                //遍历各联的情况
                if (StringUtils.isEmpty(matcher.group(2))) {
                    jointNo++;
                    splitNoJointSpanCombinationToCollection(bridgeSiteList, paramSite.jointNo(jointNo), matcher.group(3));
                } else {
                    for (int i=0; i<StringUtils.toInt(matcher.group(2)); i++) {
                        jointNo++;
                        splitNoJointSpanCombinationToCollection(bridgeSiteList, paramSite.jointNo(jointNo), matcher.group(3));
                    }
                }
            }
        } else {
            splitNoJointSpanCombinationToCollection(bridgeSiteList, new TblBridgeSite().bridgeId(bridgeId).sideTypeId(sideTypeId).siteNo(0).jointNo(0),bridgeSpanCombination);
        }
        return bridgeSiteList;
    }

    /** 加不分联的跨径组合解析至集合 */
    private void splitNoJointSpanCombinationToCollection(Collection<TblBridgeSite> targetList, TblBridgeSite bridgeSite, String bridgeSpanCombination) {
        //遍历集合
        for (String comb : bridgeSpanCombination.split("\\+")) {
            //这一步开始遍历各孔
            String[] combs = comb.split("\\*");
            if (combs.length > 1) {
                for (int i = 0; i < StringUtils.toInt(combs[0]); i++) {
                    bridgeSite.setSiteNo(bridgeSite.getSiteNo()+1);
                    targetList.add(new TblBridgeSite().id(StringUtils.createId()).
                            bridgeId(bridgeSite.getBridgeId()).
                            sideTypeId(bridgeSite.getSideTypeId()).
                            jointNo(bridgeSite.getJointNo()).
                            siteNo(bridgeSite.getSiteNo()).
                            span(metreToMillimetre(StringUtils.toFloat(combs[1]))));
                }
            } else {
                bridgeSite.setSiteNo(bridgeSite.getSiteNo()+1);
                targetList.add(new TblBridgeSite().id(StringUtils.createId())
                        .bridgeId(bridgeSite.getBridgeId())
                        .sideTypeId(bridgeSite.getSideTypeId())
                        .jointNo(bridgeSite.getJointNo())
                        .siteNo(bridgeSite.getSiteNo())
                        .span(metreToMillimetre(StringUtils.toFloat(combs[0]))));
            }
        }
    }
}
