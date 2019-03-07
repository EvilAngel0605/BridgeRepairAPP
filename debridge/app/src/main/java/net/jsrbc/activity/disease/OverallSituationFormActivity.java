package net.jsrbc.activity.disease;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.system.CameraActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.entity.TblBridgeSide;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeDiseaseMapper;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.utils.DataUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.StringUtils;

import java.util.Date;

@AndroidActivity(R.layout.activity_overall_situation_form)
@AndroidMenu(R.menu.empty)
public class OverallSituationFormActivity extends BaseActivity
        implements View.OnClickListener{

    /** 当前病害 */
    public final static String CURRENT_DISEASE = "CURRENT_DISEASE";

    @AndroidView(R.id.iv_photo_header)
    private ImageView photoHeaderView;

    @AndroidView(R.id.fab)
    @OnClick
    private FloatingActionButton fab;

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @AndroidView(R.id.et_notes)
    private EditText notesView;

    @AndroidView(R.id.iv_photo)
    private ImageView photoView;

    @AndroidView(R.id.rl_photo_wrapper)
    @OnClick
    private LinearLayout photoWrapper;

    @Mapper
    private BridgeDiseaseMapper bridgeDiseaseMapper;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    private TblBridgeSide currentBridgeSide;

    private TblBridgeDisease currentBridgeDisease;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentBridgeSide = (TblBridgeSide) getIntent().getSerializableExtra(OverallSituationActivity.CURRENT_BRIDGE_SIDE);
        currentBridgeDisease = (TblBridgeDisease) getIntent().getSerializableExtra(OverallSituationActivity.CURRENT_BRIDGE_DISEASE);
        if (currentBridgeDisease == null) currentBridgeDisease = new TblBridgeDisease.Builder().id(StringUtils.createId()).build();
        setDefaultValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TblBridgeDiseasePhoto photo = bridgeDiseasePhotoMapper.getLatestPhoto(currentBridgeDisease.getId());
        if (photo != null) {
            photoHeaderView.setImageBitmap(ImageUtils.createCompressedBitmap(photo.getPath()));
            photoView.setImageBitmap(ImageUtils.createThumbnailBitmap(photo.getPath()));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_save: {
                if (validator()) {
                    showProgressDialog();
                    new Thread(() -> {
                        currentBridgeDisease.setTaskId(currentBridgeSide.getTaskId());
                        currentBridgeDisease.setBridgeId(currentBridgeSide.getBridgeId());
                        currentBridgeDisease.setSideTypeId(currentBridgeSide.getSideTypeId());
                        currentBridgeDisease.setNotes(notesView.getText().toString());
                        currentBridgeDisease.setRecordUser(DataUtils.getCurrentToken(this).getContent().getId());
                        currentBridgeDisease.setRecordUserName(DataUtils.getCurrentToken(this).getContent().getName());
                        currentBridgeDisease.setRecordDate(StringUtils.fromDate(new Date()));
                        currentBridgeDisease.setUpload(UploadStatus.NEED_UPLOAD.getCode());
                        bridgeDiseaseMapper.addOrReplaceDisease(currentBridgeDisease);
                        AndroidConstant.HANDLER.post(() -> {
                            hideProgressDialog();
                            finish();
                        });
                    }).start();
                }
                break;
            }
            case R.id.tv_cancel: {
                finish();
                break;
            }
            case R.id.fab: {
                Intent intent = new Intent(this, CameraActivity.class);
                intent.putExtra(CameraActivity.PREVIEW_ACTIVITY, DiseasePhotoPreviewActivity.class);
                intent.putExtra(CURRENT_DISEASE, currentBridgeDisease);
                startActivity(intent);
                break;
            }
            case R.id.rl_photo_wrapper: {
                Intent intent = new Intent(this, DiseasePhotoWallActivity.class);
                intent.putExtra(CURRENT_DISEASE, currentBridgeDisease);
                startActivity(intent);
            }
        }
    }

    /** 校验输入的内容 */
    private boolean validator() {
        if (StringUtils.isEmpty(notesView.getText().toString())) {
            notesView.requestFocus();
            notesView.setError("请输入桥梁总体情况！");
            return false;
        }
        return true;
    }

    /** 设置默认值 */
    private void setDefaultValue() {
        if (currentBridgeDisease != null) {
            notesView.setText(currentBridgeDisease.getNotes());
            if (!ListUtils.isEmpty(currentBridgeDisease.getDiseasePhotoList()))
                photoView.setImageBitmap(ImageUtils.createThumbnailBitmap(currentBridgeDisease.getDiseasePhotoList().get(0).getPath()));
        }
    }
}
