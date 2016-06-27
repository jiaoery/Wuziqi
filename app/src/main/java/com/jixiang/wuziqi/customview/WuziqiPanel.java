package com.jixiang.wuziqi.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.jixiang.wuziqi.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/13.
 */
public class WuziqiPanel extends View {

    private int mPanelWidth;

    private float mLineHeight;
    //设置最大行数
    private int MAX_LINE=10;

    private int MAX_COUNT_IN_LINE=5;

    private Bitmap mWhitePiece;

    private  Bitmap mBlackPiece;

    private float ratioPiceOfLineHeight=3*1.0f/4;




    //白棋先手，当前轮到白棋
    private boolean isWhtie=true;

    private ArrayList<Point> mWhiteArray=new ArrayList<>();

    private ArrayList<Point> mBlackArray=new ArrayList<>();


//    是否获胜
    private boolean mIsGameOver;

//    是否白子赢了
    private boolean mIsWhteWinner;

    Paint mPaint=new Paint();

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    public WuziqiPanel(Context context) {
        super(context);
//        setBackgroundColor(0x44ff0000);
        init();
    }

    public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        setBackgroundColor(0x44ff0000);
        init();
    }


    private void init() {
        mPaint.setColor(0X88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece= BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);

        mBlackPiece=BitmapFactory.decodeResource(getResources(),R.drawable.stone_b1);

        int pieceWidth= (int) (mLineHeight*ratioPiceOfLineHeight);
    }


    /**
     * 触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mIsGameOver){
            return false;
        }
        int action=event.getAction();
        //监听UP动作，在于防止事件冲突（具有滑动效果的布局）
        if(action==MotionEvent.ACTION_UP){
            int x= (int) event.getX();

            int y= (int) event.getY();

            Point p=getValiadPoint(x,y);

            //判断当前点是否有棋子
            if(mWhiteArray.contains(p)||mBlackArray.contains(p)){
                return false;
            }
            if(isWhtie){
                mWhiteArray.add(p);
            }else {
                mBlackArray.add(p);
            }

            invalidate();

            isWhtie=!isWhtie;
        }
        return true;
    }

    private Point getValiadPoint(int x, int y) {
        return  new Point((int)(x/mLineHeight),(int)(y/mLineHeight));
    }


    /**
     * 测量控件的高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize=MeasureSpec.getSize(widthMeasureSpec);
        int widthMode=MeasureSpec.getMode(widthMeasureSpec);

        int heightSize=MeasureSpec.getSize(heightMeasureSpec);
        int heightMode=MeasureSpec.getMode(heightMeasureSpec);

        int width=Math.min(widthSize,heightSize);
        /**
         * 宽度类型或宽度为零时（加weight属性时）
         */
        if (widthMode==MeasureSpec.UNSPECIFIED){
            width=heightSize;
        }else if(heightMode==MeasureSpec.UNSPECIFIED){
            width=widthSize;
        }
        setMeasuredDimension(width,width);
    }

    /**
     * 当宽度和高度改变时调用此方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth=w;
        mLineHeight=mPanelWidth*1.0f/MAX_LINE;

        int pieceWidth= (int) (mLineHeight*ratioPiceOfLineHeight);
        //由于无法直接将棋子按照给定的图标直接绘制出来，所以需要根据给定宽度和高度的比例来绘制图标
        mWhitePiece=Bitmap.createScaledBitmap(mWhitePiece,pieceWidth,pieceWidth,false);
        mBlackPiece=Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);






    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        draBoard(canvas);
        drawPieces(canvas);

        checkGameOver();
    }

    /**
     * 检查是否获胜
     */
    private void checkGameOver() {
       boolean whiteWin= checkFiveInLine(mWhiteArray);
        boolean blackWin=checkFiveInLine(mBlackArray);
        if(whiteWin||blackWin){
            mIsGameOver=true;
            mIsWhteWinner=whiteWin;

            String text=mIsWhteWinner?"白棋胜利":"黑棋胜利";
            Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
        }
    }


    //重新开始游戏
     public void start(){
          mBlackArray.clear();
         mWhiteArray.clear();
         isWhtie=false;
         mIsWhteWinner=false;
         mIsGameOver=false;
         invalidate();
     }


    /**
     * 检测一方是否已经赢了
     * @param points
     * @return
     */
    private boolean checkFiveInLine(List<Point> points) {
        for(Point p:points){
            int x=p.x;
            int y=p.y;

            boolean win=checkHorizontal(x,y,points);
            if(win)return true;
            win=checkVertical(x,y,points);
            if(win)return true;
            win=checkLeftDiagonal(x,y,points);
            if(win)return true;
            win=checkRightDiagonal(x,y,points);
            if(win)return true;
        }
        return false;
    }

    /**
     * 根据x,y坐标，判断横向是否有五个子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count=1;
        //左边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //右边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y))){
                count++;
            }else{
                break;
            }
        }

        if(count==MAX_COUNT_IN_LINE) return true;
        return false;
    }


    /**
     * 根据x,y坐标，判断纵向是否有五个子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count=1;
        //左边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //右边
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x,y+i))){
                count++;
            }else{
                break;
            }
        }

        if(count==MAX_COUNT_IN_LINE) return true;
        return false;
    }


    /**
     * 根据x,y坐标，判断左斜是否有五个子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count=1;
        //上半部分
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //下版部分
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y+i))){
                count++;
            }else{
                break;
            }
        }

        if(count==MAX_COUNT_IN_LINE) return true;
        return false;
    }


    /**
            * 根据x,y坐标，判断左斜是否有五个子
    * @param x
    * @param y
    * @param points
    * @return
            */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count=1;
        //上半部分
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x-i,y-i))){
                count++;
            }else{
                break;
            }
        }
        if(count==MAX_COUNT_IN_LINE) return true;

        //下版部分
        for(int i=1;i<MAX_COUNT_IN_LINE;i++){
            if(points.contains(new Point(x+i,y+i))){
                count++;
            }else{
                break;
            }
        }

        if(count==MAX_COUNT_IN_LINE) return true;
        return false;
    }


    private void drawPieces(Canvas canvas) {
        for(int i=0,n=mWhiteArray.size();i<n;i++){
            Point whitePoint=mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+(1-ratioPiceOfLineHeight)/2)*mLineHeight,
                    (whitePoint.y+(1-ratioPiceOfLineHeight)/2)*mLineHeight,null);



        }

        for(int i=0,n=mBlackArray.size();i<n;i++){
            Point blackPoint=mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x+(1-ratioPiceOfLineHeight)/2)*mLineHeight,
                    (blackPoint.y+(1-ratioPiceOfLineHeight)/2)*mLineHeight,null);

        }
    }

    private void draBoard(Canvas canvas) {
        int w=mPanelWidth;

        float lineHeght=mLineHeight;

        for(int i=0;i<MAX_LINE;i++){
            int startX= (int) (lineHeght/2);
            int endX= (int) (w-lineHeght/2);

            int y= (int) ((0.5+i)*lineHeght);

            canvas.drawLine(startX,y,endX,y,mPaint);

            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }


    private static String INSATANCE="instance";

    private static String INSATANCE_GAMEOVER="instance_game_over";

    private static String INSATANCE_WHITE_ARRAY="instance_white_array";

    private static String INSATANCE_BLACK_ARRAY="instance_black_array";
    /**
     * 保存需要的值不被删除
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle=new Bundle();
        bundle.putParcelable(INSATANCE,super.onSaveInstanceState());
        bundle.putBoolean(INSATANCE_GAMEOVER,mIsGameOver);
        bundle.putParcelableArrayList(INSATANCE_WHITE_ARRAY,mWhiteArray);
        bundle.putParcelableArrayList(INSATANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle){
            Bundle bundle= (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(INSATANCE));
            mIsGameOver=bundle.getBoolean(INSATANCE_GAMEOVER);
            mWhiteArray=bundle.getParcelableArrayList(INSATANCE_WHITE_ARRAY);
            mBlackArray=bundle.getParcelableArrayList(INSATANCE_BLACK_ARRAY);

            return;
        }
        super.onRestoreInstanceState(state);
    }
}
