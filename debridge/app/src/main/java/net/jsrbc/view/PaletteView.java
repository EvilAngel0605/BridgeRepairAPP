package net.jsrbc.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import net.jsrbc.R;
import net.jsrbc.utils.ImageUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;

/**
 * Created by ZZZ on 2016/6/24.
 */
public class PaletteView extends View {

    /**
     * 上下文环境
     */
    private Context context;

    /**
     * 保存绘制的路径
     */
    private LinkedList<Path> pathList = new LinkedList<>();

    /**
     * 保存被删除的路径数据
     */
    private LinkedList<Path> restoreList = new LinkedList<>();

    /**
     * 用来显示的path,用完之后重置
     */
    private Path drawPath = new Path();

    /**
     * 画笔
     */
    private Paint mPaint = new Paint();

    /**
     * 图片处理矩阵
     */
    private Matrix matrix = new Matrix();

    /**
     * 源图
     */
    private Bitmap sourceBitmap;

    /**
     * 源图路径
     */
    private String sourceImagePath;

    /**
     * 初始加载时图片的缩放比例
     */
    private float initRatio;

    /**
     * 初始加载时图片的偏移量
     */
    private float initTranslateX;

    /**
     * 初始加载时图片的偏移量
     */
    private float initTranslateY;

    /**
     * 空间宽度
     */
    private float width;

    /**
     * 空间高度
     */
    private float height;

    /**
     * 移动的位置
     */
    private PointF lastMovePoint;

    /**
     * 手指中点
     */
    private PointF fingerCenterPoint;

    /**
     * 当前bitmap的宽度
     */
    private float currentCanvasWidth;

    /**
     * 当前画布的高度
     */
    private float currentCanvasHeight;

    /**
     * 总共移动的距离，X方向
     */
    private float totalTranslateX;

    /**
     * 总共移动的距离，Y方向
     */
    private float totalTranslateY;

    /**
     * 总共缩放的比例
     */
    private float totalRatio = 1;

    /**
     * 最近一次手指间距
     */
    private double lastFingerDis;

    /**
     * 矩形左上角的起点
     */
    private PointF startPoint;

    /**
     * 回调对象
     */
    private Callback callback;

    /**
     * 绘画的类型
     */
    private PaintType paintType = PaintType.NONE;

    /**
     * 形状
     */
    public enum PaintType{
        NONE,LINE,RECT,OVAL
    }

    /**
     * 回调接口
     */
    public interface Callback {
        void onPaint();
        void onZoom();
    }

    /**
     * 构造函数
     * @param context
     */
    public PaletteView(Context context) {
        super(context);
        initialize(context);
    }

    /**
     * 构造函数
     * @param context
     * @param attrs
     */
    public PaletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    /**
     * 设置绘图事件发生时的回调方法
     * @param callback   图片处理事件回调
     */
    public void addCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 设置bitmap
     * @param bitmapPath 图片文件路径
     * @param width      图片像素宽度
     * @param height     图片像素高度
     */
    public void setBitmap(String bitmapPath, int width, int height) {
        this.sourceImagePath = bitmapPath;
        this.sourceBitmap = ImageUtils.createCompressedBitmap(sourceImagePath, width, height);
        invalidate();
    }

    /**
     * 设置绘画类型
     * @param type 类别
     */
    public void changePaintType(PaintType type) {
        this.paintType = type;
    }

    /**
     * 是否能前进
     * @return
     */
    public boolean canRedo() {
        return !restoreList.isEmpty();
    }

    /**
     * 是否能后退
     * @return
     */
    public boolean canUndo() {
        return !pathList.isEmpty();
    }

    /**
     * 恢复上一次
     */
    public void redo() {
        if (restoreList.size()>0) {
            drawPath.reset();
            pathList.add(restoreList.pollLast());
            if (!pathList.isEmpty()) drawPath.addPath(pathList.getLast());
            invalidate();
        }
    }

    /**
     * 回退
     */
    public void undo() {
        if (pathList.size() > 0) {
            drawPath.reset();
            restoreList.add(pathList.pollLast());
            if (!pathList.isEmpty()) drawPath.addPath(pathList.getLast());
            invalidate();
        }
    }

    /**
     * 清理画布
     */
    public void clear() {
        restoreList.addAll(pathList);
        pathList.clear();
        drawPath.reset();
        invalidate();
    }

    /**
     * 保存图片
     * @return
     */
    public boolean save() {
        boolean flag = true;
        OutputStream os = null;
        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(sourceBitmap, 0, 0, null);
        Matrix pathMatrix = new Matrix();
        pathMatrix.postTranslate(-initTranslateX, -initTranslateY);
        pathMatrix.postScale(1F/initRatio, 1F/initRatio);
        drawPath.transform(pathMatrix);
        canvas.drawPath(drawPath, mPaint);
        try {
            os = new FileOutputStream(sourceImagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
        }catch (FileNotFoundException e){
            e.printStackTrace();
            flag = false;
        }finally {
            try {
                if (os != null) os.close();
            }catch (IOException e){
                e.printStackTrace();
                flag = false;
            }
            bitmap.recycle();
        }
        return flag;
    }

    /**
     * 获取布局的宽度及高度
     * @param changed
     * @param l
     * @param t
     * @param r
     * @param b
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
        if (changed) {
            width = currentCanvasWidth = getWidth();
            height = currentCanvasHeight = getHeight();
        }
    }

    /**
     * 绘制
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(ContextCompat.getColor(context, R.color.colorPrimary));
        canvas.translate(totalTranslateX, totalTranslateY);
        canvas.scale(totalRatio, totalRatio);
        initBitmap(canvas);
        canvas.drawPath(drawPath, mPaint);
    }

    /**
     * 触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                switch (paintType) {
                    case LINE:
                        drawPath.moveTo((event.getX()-totalTranslateX)/totalRatio, (event.getY()-totalTranslateY)/totalRatio);
                        break;
                    case RECT:
                    case OVAL:
                        startPoint = new PointF((event.getX()-totalTranslateX)/totalRatio, (event.getY()-totalTranslateY)/totalRatio);
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2)  {
                    if (callback != null) callback.onZoom();
                    lastFingerDis = getFingerDistance(event);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 1) {
                    switch (paintType) {
                        case LINE:
                            drawPath.lineTo((event.getX()-totalTranslateX)/totalRatio, (event.getY()-totalTranslateY)/totalRatio);
                            break;
                        case RECT:
                            removeLastPath();
                            drawPath.addRect(startPoint.x, startPoint.y, (event.getX()-totalTranslateX)/totalRatio, (event.getY()-totalTranslateY)/totalRatio, Path.Direction.CW);
                            break;
                        case OVAL:
                            removeLastPath();
                            drawPath.addOval(startPoint.x, startPoint.y, (event.getX()-totalTranslateX)/totalRatio, (event.getY()-totalTranslateY)/totalRatio, Path.Direction.CW);
                            break;
                        default:
                            switchToMove(event);
                            break;
                    }
                    if (paintType != PaintType.NONE) invalidate();
                }else if (event.getPointerCount() == 2){
                    switchToZoom(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                reset();
                break;
            case MotionEvent.ACTION_UP:
                reset();
                addPath();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 清除路径上一次的状态
     */
    private void removeLastPath() {
        drawPath.reset();
        if (!pathList.isEmpty()) drawPath.addPath(pathList.getLast());
    }

    /**
     * 手指抬起后重置一些参数
     */
    private void reset() {
        lastMovePoint.set(-1, -1);
        startPoint.set(-1, -1);
    }

    /**
     * 结束触摸事件
     */
    private void addPath() {
        //保存路径的状态
        if (paintType != PaintType.NONE) {
            restoreList.clear();
            Path path = new Path();
            path.set(drawPath);
            pathList.add(path);
            if (callback != null) callback.onPaint();
        }
    }

    /**
     * 初始化
     * @param context
     */
    private void initialize(Context context) {
        this.context = context;
        //两指中心点
        fingerCenterPoint = new PointF(-1, -1);
        //手指最近移动的点
        lastMovePoint = new PointF(-1, -1);
        //画矩形的起点
        startPoint = new PointF(-1, -1);
        //设置画笔
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5F);
    }

    /**
     * 切换到移动模式
     * @param event
     */
    private void switchToMove(MotionEvent event) {
        PointF currentPoint = new PointF(event.getX(), event.getY());
        if (lastMovePoint.x == -1 && lastMovePoint.y == -1) {
            lastMovePoint.set(currentPoint.x, currentPoint.y);
        }
        float movedDistanceX = currentPoint.x - lastMovePoint.x;
        float movedDistanceY = currentPoint.y - lastMovePoint.y;
        //检查边界，防止图片滑出
        if(totalTranslateX + movedDistanceX > 0) {
            movedDistanceX = 0;
        }else if (width - (totalTranslateX+movedDistanceX) > currentCanvasWidth) {
            movedDistanceX = 0;
        }
        if (totalTranslateY + movedDistanceY > 0) {
            movedDistanceY = 0;
        }else if (height - (totalTranslateY+movedDistanceY) > currentCanvasHeight) {
            movedDistanceY = 0;
        }
        totalTranslateX += movedDistanceX;
        totalTranslateY += movedDistanceY;
        invalidate();
        lastMovePoint.set(currentPoint.x, currentPoint.y);
    }

    /**
     * 切换到缩放模式
     * @param event
     */
    private void switchToZoom(MotionEvent event) {
        fingerCenterPoint = getFingerCenterPoint(event);
        double fingerDis = getFingerDistance(event);
        float scaleRatio = (float)(fingerDis/lastFingerDis);
        totalRatio *= scaleRatio;
        //缩放程度不能小于初始缩放比，不能大于4倍的初始缩放比
        if (totalRatio > 4) {
            totalRatio = 4;
        }else if (totalRatio<1) {
            totalRatio = 1;
        }else{
            totalTranslateX = totalTranslateX * scaleRatio + fingerCenterPoint.y * (1 - scaleRatio);
            totalTranslateY = totalTranslateY * scaleRatio + fingerCenterPoint.y * (1 - scaleRatio);
        }
        currentCanvasWidth = width * totalRatio;
        currentCanvasHeight = height * totalRatio;
        //检查边界，保证图片不会偏移屏幕
        if (totalTranslateX > 0) {
            totalTranslateX = 0;
        } else if (width - totalTranslateX > currentCanvasWidth) {
            totalTranslateX = width - currentCanvasWidth;
        }
        //检查边界，保证图片不会偏移屏幕
        if (totalTranslateY > 0) {
            totalTranslateY = 0;
        } else if (height - totalTranslateY > currentCanvasHeight){
            totalTranslateY = height - currentCanvasHeight;
        }
        //记录图片的参数
        invalidate();
        lastFingerDis = fingerDis;
    }

    /**
     * 初始化图片
     * @param canvas
     */
    private void initBitmap(Canvas canvas) {
        if (sourceBitmap != null) {
            matrix.reset();
            float scaleWidth = sourceBitmap.getWidth();
            float scaleHeight = sourceBitmap.getHeight();
            //居中显示图片
            if (scaleWidth > width || scaleHeight > height) {
                float ratio;
                if ((scaleWidth*1f/width)>(scaleHeight*1f/height)) {
                    ratio = initRatio = width*1f/scaleWidth;
                    matrix.postScale(ratio, ratio);
                    float translateY = initTranslateY = (height - scaleHeight*ratio)/2f;
                    matrix.postTranslate(0, translateY);
                }else{
                    ratio = initRatio = height*1f/scaleHeight;
                    matrix.postScale(ratio,ratio);
                    float translateX = initTranslateX = (width-scaleWidth*ratio)/2f;
                    matrix.postTranslate(translateX, 0);
                }
            }else{
                float translateX = initTranslateX = (width - sourceBitmap.getWidth())/2f;
                float translateY = initTranslateY = (height - sourceBitmap.getHeight())/2f;
                matrix.postTranslate(translateX, translateY);
            }
            canvas.drawBitmap(sourceBitmap, matrix, null);
        }
    }

    /**
     * 获取手指之间的距离
     * @param event
     * @return
     */
    private double getFingerDistance(MotionEvent event) {
        float x = event.getX(0)-event.getX(1);
        float y = event.getY(0)-event.getY(1);
        return Math.sqrt(x*x+y*y);
    }

    /**
     * 获取手指中点的坐标
     * @param event
     */
    private PointF getFingerCenterPoint(MotionEvent event) {
        float x = event.getX(0)+event.getX(1);
        float y = event.getY(0)+event.getY(1);
        return new PointF(x/2, y/2);
    }
}