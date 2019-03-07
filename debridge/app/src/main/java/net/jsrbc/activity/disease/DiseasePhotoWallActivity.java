package net.jsrbc.activity.disease;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import net.jsrbc.R;
import net.jsrbc.constant.AndroidConstant;
import net.jsrbc.entity.TblBridgeDisease;
import net.jsrbc.entity.TblBridgeDiseasePhoto;
import net.jsrbc.frame.annotation.database.Mapper;
import net.jsrbc.frame.annotation.element.AndroidActivity;
import net.jsrbc.frame.annotation.element.AndroidMenu;
import net.jsrbc.frame.annotation.element.AndroidView;
import net.jsrbc.frame.base.BaseActivity;
import net.jsrbc.mapper.BridgeDiseasePhotoMapper;
import net.jsrbc.utils.FileUtils;
import net.jsrbc.utils.ImageUtils;
import net.jsrbc.utils.ListUtils;
import net.jsrbc.utils.SystemUtils;

import java.util.ArrayList;
import java.util.List;

@AndroidActivity(R.layout.activity_disease_photo_wall)
@AndroidMenu(R.menu.empty)
public class DiseasePhotoWallActivity extends BaseActivity {

    public final static String CURRENT_PHOTO = "CURRENT_PHOTO";

    @AndroidView(R.id.tv_empty)
    private TextView emptyView;

    @AndroidView(R.id.rv_photo)
    private RecyclerView photoListView;

    @Mapper
    private BridgeDiseasePhotoMapper bridgeDiseasePhotoMapper;

    private List<TblBridgeDiseasePhoto> photoList = new ArrayList<>();

    private PhotoListAdapter photoListAdapter;

    private TblBridgeDisease currentBridgeDisease;

    @Override
    protected void created() {
        currentBridgeDisease = (TblBridgeDisease) getIntent().getSerializableExtra(OverallSituationFormActivity.CURRENT_DISEASE);
        setDefaultToolbar();
        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPhotoData();
    }

    /** 设置列表视图 */
    private void setRecyclerView() {
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        photoListAdapter = new PhotoListAdapter(photoList);
        photoListView.setLayoutManager(manager);
        photoListView.setAdapter(photoListAdapter);
    }

    /** 获取照片数据 */
    private void fetchPhotoData() {
        showProgressDialog();
        new Thread(()->{
            photoList.clear();
            List<TblBridgeDiseasePhoto> temp = bridgeDiseasePhotoMapper.getDiseasePhotoList(currentBridgeDisease.getId());
            if (!ListUtils.isEmpty(temp)) photoList.addAll(temp);
            AndroidConstant.HANDLER.post(()->{
                hideProgressDialog();
                photoListAdapter.notifyDataSetChanged();
                showEmptyView(photoList.isEmpty());
            });
        }).start();
    }

    /** 是否显示空列表视图 */
    private void showEmptyView(boolean isShow) {
        if (isShow) emptyView.setVisibility(View.VISIBLE);
        else emptyView.setVisibility(View.GONE);
    }

    /** 照片列表适配器 */
    private class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.ViewHolder> {

        private List<TblBridgeDiseasePhoto> photoList;

        PhotoListAdapter(List<TblBridgeDiseasePhoto> photoList) {
            this.photoList = photoList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TblBridgeDiseasePhoto photo = photoList.get(position);
            holder.getPhotoView().setImageBitmap(ImageUtils.createCompressedBitmap(photo.getPath()));
            holder.getPhotoWrapper().setOnClickListener(v->{
                PopupMenu popupMenu = new PopupMenu(DiseasePhotoWallActivity.this, holder.getPhotoWrapper(), Gravity.CENTER);
                getMenuInflater().inflate(R.menu.photo_action, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(item->{
                    switch (item.getItemId()) {
                        case R.id.mi_view:
                            Intent intent = new Intent(DiseasePhotoWallActivity.this, DiseasePhotoPreviewActivity.class);
                            intent.putExtra(CURRENT_PHOTO, photo);
                            startActivity(intent);
                            break;
                        case R.id.mi_delete:
                            SystemUtils.confirm(DiseasePhotoWallActivity.this, "确定删除", (d, i)->{
                                bridgeDiseasePhotoMapper.deleteDiseasePhoto(photo.getId());
                                FileUtils.delete(photo.getPath());
                                photoList.remove(photo);
                                notifyItemRemoved(holder.getAdapterPosition());
                                showEmptyView(ListUtils.isEmpty(photoList));
                            });
                            break;
                    }
                    return true;
                });
                popupMenu.show();
            });
        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private CardView photoWrapper;

            private ImageView photoView;

            ViewHolder(View view) {
                super(view);
                photoWrapper = view.findViewById(R.id.cv_wrapper);
                photoView = view.findViewById(R.id.iv_photo);
            }

            CardView getPhotoWrapper() {
                return photoWrapper;
            }

            ImageView getPhotoView() {
                return photoView;
            }
        }
    }
}
