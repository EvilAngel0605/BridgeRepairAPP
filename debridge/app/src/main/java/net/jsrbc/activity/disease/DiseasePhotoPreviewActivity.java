package net.jsrbc.activity.disease;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import net.jsrbc.R;
import net.jsrbc.activity.system.CameraActivity;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.enumeration.UploadStatus;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActionView;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.annotation.event.OnClick;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.StringUtils;
import net.jsrbc.view.PaletteView;

import java.io.File;

/**
 * Created by ZZZ on 2017-12-22.
 */
@AndroidActivity(R.layout.activity_disease_preview)
@AndroidMenu(R.menu.palette_menu)
public class DiseasePhotoPreviewActivity extends BaseActivity
        implements View.OnClickListener{

    public final static String CURRENT_DISEASE = "CURRENT_DISEASE";

    @AndroidView(R.id.pv)
    private PaletteView paletteView;

    @AndroidView(R.id.iv_line)
    @OnClick
    private ImageView lineBtn;

    @AndroidView(R.id.iv_rect)
    @OnClick
    private ImageView rectBtn;

    @AndroidView(R.id.iv_oval)
    @OnClick
    private ImageView ovalBtn;

    @AndroidActionView(R.id.mi_save)
    private MenuItem saveAction;

    @AndroidActionView(R.id.mi_back)
    private MenuItem backAction;

    @AndroidActionView(R.id.mi_forward)
    private MenuItem forwardAction;

    @AndroidActionView(R.id.mi_clear)
    private MenuItem clearAction;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    private TblBridgeDisease currentDisease;

    private TblBridgeDiseasePhoto currentPhoto;

    private File currentPhotoFile;

    @Override
    protected void created() {
        setDefaultToolbar();
        currentDisease = (TblBridgeDisease) getIntent().getSerializableExtra(CURRENT_DISEASE);
        currentPhotoFile = (File)getIntent().getSerializableExtra(CameraActivity.PHOTO_FILE);
        currentPhoto = (TblBridgeDiseasePhoto)getIntent().getSerializableExtra(DiseasePhotoWallActivity.CURRENT_PHOTO);
        if (currentPhoto == null) currentPhoto = new TblBridgeDiseasePhoto(currentDisease.getId(), currentPhotoFile.getPath());
        paletteView.setBitmap(currentPhoto.getPath(), AndroidConstant.PHOTO_SIZE.getWidth(), AndroidConstant.PHOTO_SIZE.getHeight());
        paletteView.addCallback(new PaletteView.Callback() {
            @Override
            public void onPaint() {
                initMenu();
            }

            @Override
            public void onZoom() {
                unSelectAll();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_back:
                paletteView.undo();
                initMenu();
                break;
            case R.id.mi_forward:
                paletteView.redo();
                initMenu();
                break;
            case R.id.mi_clear:
                paletteView.clear();
                initMenu();
                break;
            case R.id.mi_save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        //是否禁用
        boolean isSelected = !view.isSelected();
        unSelectAll();
        view.setSelected(isSelected);
        //判断点击的按钮
        switch (view.getId()) {
            case R.id.iv_line: {
                if (isSelected) paletteView.changePaintType(PaletteView.PaintType.LINE);
                break;
            }
            case R.id.iv_rect: {
                if (isSelected) paletteView.changePaintType(PaletteView.PaintType.RECT);
                break;
            }
            case R.id.iv_oval: {
                if (isSelected) paletteView.changePaintType(PaletteView.PaintType.OVAL);
                break;
            }
        }
    }

    /** 保存图片 */
    private void save() {
        showProgressDialog();
        new Thread(()->{
            paletteView.save();
            File directory = new File(AndroidConstant.PHOTO_DIRECTORY, currentPhoto.getDiseaseId());
            File photoFile = new File(directory, StringUtils.createId() + ".jpg");
            FileUtils.createDirectory(directory.getPath());
            FileUtils.copy(new File(currentPhoto.getPath()), photoFile);
            currentPhoto.setPath(photoFile.getPath());
            currentPhoto.setUpload(UploadStatus.NEED_UPLOAD.getCode());
            bridgeDiseasePhotoMapper.addOrReplaceDiseasePhoto(currentPhoto);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                finish();
            });
        }).start();
    }

    /** 全不选 */
    private void unSelectAll() {
        paletteView.changePaintType(PaletteView.PaintType.NONE);
        lineBtn.setSelected(false);
        rectBtn.setSelected(false);
        ovalBtn.setSelected(false);
    }

    /** 初始化菜单项 */
    private void initMenu() {
        clearAction.setEnabled(paletteView.canUndo());
        backAction.setEnabled(paletteView.canUndo());
        forwardAction.setEnabled(paletteView.canRedo());
    }
}
