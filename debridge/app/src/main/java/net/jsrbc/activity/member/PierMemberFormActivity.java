package net.jsrbc.activity.member;

import android.content.Intent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.entity.TblBridgeParts;
import net.jsrbc.entity.TblParamType;
import net.jsrbc.enumeration.ActivityResultCode;
import net.jsrbc.enumeration.ParamGroupField;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BaseDataMapper;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.List;
import java.util.stream.Collectors;

@AndroidActivity(R.layout.activity_pier_member_form)
public class PierMemberFormActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @AndroidView(R.id.sp_material_type)
    private Spinner materialTypeSelector;

    @AndroidView(R.id.et_horizontal_count)
    private EditText horizontalCountInput;

    @Mapper
    private BaseDataMapper baseDataMapper;

    private List<TblParamType> materialTypeList;

    private TblBridgeParts currentBridgeParts;

    @Override
    protected void created() {
        currentBridgeParts = (TblBridgeParts)getIntent().getSerializableExtra(PierFormActivity.CURRENT_BRIDGE_PARTS);
        setMaterialTypeSelector();
        setDefaultValue();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel: {
                finish();
                break;
            }
            case R.id.tv_save: {
                if (validate()) save();
                break;
            }
        }
    }

    @Override
    protected void onPause() {
        SystemUtils.hideSoftInputFromWindow(saveBtn, this);
        super.onPause();
    }

    /** 保存并返回 */
    private void save() {
        TblParamType materialType = (TblParamType)materialTypeSelector.getSelectedItem();
        int horizontalCount = StringUtils.toInt(horizontalCountInput.getText().toString());
        currentBridgeParts.setMaterialTypeId(materialType.getId());
        currentBridgeParts.setMaterialTypeName(materialType.getName());
        currentBridgeParts.setHorizontalCount(horizontalCount);
        //返回
        Intent intent = new Intent();
        intent.putExtra(PierFormActivity.CURRENT_BRIDGE_PARTS, currentBridgeParts);
        setResult(ActivityResultCode.SUCCESS.getCode(), intent);
        finish();
    }

    /** 设置材料类型选择器 */
    private void setMaterialTypeSelector() {
        materialTypeList = baseDataMapper.getParamTypeListByGroupField(ParamGroupField.MATERIAL_TYPE);
        materialTypeList.add(0, TblParamType.empty());
        ArrayAdapter<TblParamType> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, materialTypeList);
        materialTypeSelector.setAdapter(adapter);
    }

    /** 设置默认值 */
    private void setDefaultValue() {
        if (!StringUtils.isEmpty(currentBridgeParts.getMaterialTypeId())) {
            materialTypeSelector.setSelection(materialTypeList.stream().map(TblParamType::getId).collect(Collectors.toList()).indexOf(currentBridgeParts.getMaterialTypeId()));
        }
        if (currentBridgeParts.getHorizontalCount() != 0) horizontalCountInput.setText(String.valueOf(currentBridgeParts.getHorizontalCount()));
    }

    /** 校验输入的内容 */
    private boolean validate() {
        if (materialTypeSelector.getSelectedItem() == TblParamType.empty()) {
            SystemUtils.prompt(this, "请选择材料类型");
            return false;
        }
        return true;
    }
}
