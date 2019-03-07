package net.jsrbc.activity.bridge;

import android.view.View;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.activity.system.CameraActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridge;
import net.jsrbc.enumeration.PhotoType;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeMapper;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.view.PaletteView;

import java.io.File;

@AndroidActivity(R.layout.activity_bridge_preview)
public class BridgePreviewActivity extends BaseActivity
        implements View.OnClickListener{

    @AndroidView(R.id.pv_preview)
    private PaletteView previewView;

    @AndroidView(R.id.tv_cancel)
    @OnClick
    private TextView cancelBtn;

    @AndroidView(R.id.tv_save)
    @OnClick
    private TextView saveBtn;

    @Mapper
    private BridgeMapper bridgeMapper;

    private File currentFile;

    private TblBridge currentBridge;

    private PhotoType currentPhotoType;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                finish();
                break;
            case R.id.tv_save:
                save();
                break;
        }
        currentFile.delete();
    }

    @Override
    protected void created() {
        currentFile = (File)getIntent().getSerializableExtra(CameraActivity.PHOTO_FILE);
        currentBridge = (TblBridge) getIntent().getSerializableExtra(BridgeActivity.CURRENT_BRIDGE);
        currentPhotoType = (PhotoType) getIntent().getSerializableExtra(BridgeActivity.CURRENT_PHOTO_TYPE);
        previewView.setBitmap(currentFile.getPath(), AndroidConstant.PHOTO_SIZE.getWidth(), AndroidConstant.PHOTO_SIZE.getHeight());
    }

    /** 保存内容 */
    public void save() {
        showProgressDialog();
        new Thread(()->{
            previewView.save();
            File directory = new File(AndroidConstant.PHOTO_DIRECTORY, currentBridge.getName());
            File photoFile = new File(directory, StringUtils.createId()+".jpg");
            FileUtils.createDirectory(directory.getPath());
            FileUtils.copy(currentFile, photoFile);
            switch (currentPhotoType) {
                case FRONT:
                    currentBridge.setFrontPath(photoFile.getPath());
                    break;
                case SIDE:
                    currentBridge.setSidePath(photoFile.getPath());
                    break;
                case UPWARD:
                    currentBridge.setUpwardPath(photoFile.getPath());
                    break;
            }
            bridgeMapper.updateBridgePhoto(currentBridge);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }
}
