# 五子棋app 
##使用了自定义View WuziqiPanel实现棋盘和棋子的绘制，并且使用了onSaveInstanceState()和onRestoreInstanceState方法做数据存贮和恢复。</br>
之后在此基础上增加一些关于gradle打包 新功能的开发。希望各位同道多多指点</br>


该项目参考了慕课网关于五子棋相关的课程：</br>
    
  http://www.imooc.com/video/11601
  
  在此特别感谢视频的作者相关的课程指导，也希望希望看懂项目中的技术，但是基础一般的开发者们去看看相关的视频讲解。</br>


##联系方式:
 
 邮箱：jix@03199.com
 
##演示效果
###Apk的下载地址

[debug Apk的下载地址](https://github.com/jixiang52002/Wuziqi/blob/master/app/%E4%BA%94%E5%AD%90%E6%A3%8Bwzq_debug_1.0.apk)

[release apk的下载地址](https://github.com/jixiang52002/Wuziqi/blob/master/app/%E4%BA%94%E5%AD%90%E6%A3%8Bwzq_release_1.0.apk)
 
##1.开发功能解析
 
###(1).先建立WuziqiPanel extends View
 
 
 ```Java
 /**
 *实现父类的构造方法
 **/
  public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    public WuziqiPanel(Context context) {
        super(context);
        setBackgroundColor(0x44ff0000);
        init();
    }

    public WuziqiPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundColor(0x44ff0000);
        init();
    }

 ```
 
 
 这里实现三种构造方法主要在于防止view在不同的初始化会出现无法显示效果的情况
 
 
 ```Java
 
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

    /**
     * 绘制棋盘和棋子
     */
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
 ```
 
