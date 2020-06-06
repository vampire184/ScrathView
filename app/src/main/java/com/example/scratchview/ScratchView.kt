package com.example.scratchview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.random.Random

class ScratchView : View {

    //画笔
    private lateinit var mPaint: Paint
    //画布的宽高
    private var mWidth: Int = 0
    private var mHeight: Int = 0
    //刮奖层
    private lateinit var mSrcBitmap: Bitmap
    //奖品
    private var mAward = ""
    //奖品文字宽度
    private var mWordsWidth: Float = 0.0f
    //奖品文字绘制的基线位置
    private var mTextBaseLine: Float = 0.0f
    //刮奖手势层
    private lateinit var mDscCanvas: Canvas
    private lateinit var mDscBitmap: Bitmap
    private lateinit var mSrcOut: Xfermode
    //保存手势路径
    private var mPath: Path = Path()
    //触摸开始的位置
    private var mStarX:Float = 0.0f
    private var mStarY:Float = 0.0f
    /**
     * 中奖概率：
     * 一等奖：1%
     * 二等奖：4%
     * 三等奖：10%
     * 四等奖：15%
     * 五等奖：20%
     * 未中奖：50%
     */
    private val mAwardList: List<String> = listOf(
        "恭喜您：获得一台iWatch",
        "恭喜您：获得一台乐视TV",
        "恭喜您：获得一个网易抱枕",
        "恭喜您：获得50个兔兔币",
        "恭喜您：获得50积分",
        "谢谢惠顾")

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    private fun init(context: Context) {
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.textSize = 32.0f
        //线段起始、末尾为圆形
        mPaint.strokeCap = Paint.Cap.ROUND
        //线段连接处为圆形
        mPaint.strokeJoin = Paint.Join.ROUND

        mPaint.color = ContextCompat.getColor(context, R.color.red)

        mSrcBitmap = BitmapFactory.decodeResource(context.resources, R.mipmap.award_src)
        mWidth = mSrcBitmap.width
        mHeight = mSrcBitmap.height

        mDscBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        mDscCanvas = Canvas(mDscBitmap)

        generateAward()
        mTextBaseLine = mHeight / 2.0f

        mSrcOut = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)

        //关闭硬件加速
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //画背景
        setBackgroundColor(ContextCompat.getColor(context, android.R.color.white))

        mPaint.style = Paint.Style.FILL
        mPaint.strokeWidth = 2.0f
        //画中奖信息
        val left = (mWidth - mWordsWidth) / 2
        canvas!!.drawText(mAward, left, mTextBaseLine, mPaint)

        val layerId = canvas!!.saveLayer(0.0f, 0.0f, mWidth.toFloat(), mHeight.toFloat(), mPaint)

        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 32.0f
        mDscCanvas.drawPath(mPath, mPaint)
        //画dst(手指触摸区域)
        canvas!!.drawBitmap(mDscBitmap, 0.0f, 0.0f, mPaint)

        mPaint.xfermode = mSrcOut

        //画src（刮奖图片）
        canvas!!.drawBitmap(mSrcBitmap, 0.0f, 0.0f, mPaint)
        mPaint.xfermode = null

        canvas!!.restoreToCount(layerId)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {
                mPath.reset()
                mStarX = event.x
                mStarY = event.y
                mPath.moveTo(mStarX,mStarY)
            }
            MotionEvent.ACTION_MOVE -> {
                val endX = event.x
                val endY = event.y
                val controlX = (endX-mStarX)/2+mStarX
                val controlY = (endY-mStarY)/2+mStarY
                mPath.quadTo(controlX,controlY,endX,endY)
                mStarX = endX
                mStarY = endY
            }
        }
        invalidate()
        return true
    }

    /**
     * 生成随机奖品
     */
    private fun generateAward(){

        when(Random.nextInt(100)){
            in 0..1 -> {
                mAward = mAwardList[0]
            }
            in 1..5 ->{
                mAward = mAwardList[1]
            }
            in 5..15 ->{
                mAward = mAwardList[2]
            }
            in 15..30 ->{
                mAward = mAwardList[3]
            }
            in 30..50 ->{
                mAward = mAwardList[4]
            }
            in 50..100 ->{
                mAward = mAwardList[5]
            }
        }
        mWordsWidth = mPaint.measureText(mAward)
    }

    /**
     * 重置
     */
    fun resetView(){
        mPath.reset()
        mDscBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888)
        mDscCanvas = Canvas(mDscBitmap)
        generateAward()
        invalidate()
    }

}