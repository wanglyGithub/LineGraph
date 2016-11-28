package com.wangly.linegraph.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.wangly.linegraph.MainActivity;
import com.wangly.linegraph.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 绘制X轴、Y轴的直线可以抽取出来
 * 绘制点、Path(曲线路径)可以抽取出来
 */
public class MoveDemoView extends View {

	// Y轴坐标值
	private List<String> mYanixValues;
	// x轴坐标值
	private List<String> mXanixValues;
	// 步行数据
	private List<Integer> mDatas;

	protected Paint mPaint;// 画笔
	protected Paint mOuterCiclePaint;// 画笔
	protected Paint mInnerCiclePaint;// 画笔
	private Path mPath;
	private Rect mRect;
	private int mOuterCicleColor = Color.WHITE;// 外圆颜色
	private int mInnerCicleColor = 0xFFff6600;// 内圆颜色  
	private int mTextMonthGray = 0xFFce1600;
	private int mTextDay = 0xFFa1a1a1;
	private float mLineSizeStroke = 1.0f;
	private float mLineCenterSizeStroke = 1.4f;
	private String mLineColor = "#999999";
	private int mEndLine = 0xFFffe073;
	/**上下边界蓝色横线绘制时要预留的长度*/ 
	private float xoffset = 0;
	/** 整个View距右侧的距离*/
	private int marginRight = 19;
	/** x轴向左预留的距离 */ 
	private int leftoffset = 22;

	// x轴坐标值的高
	private int mXAnixValueHight = 40;
	private int mBackgroudColor = 0xFFFFFFFF;
	/** 纵轴线的条数 */
	private int mYLineCount = 30;

	/**连线Path 的颜色值*/
	private int mPathColor = 0xFFff6600;

	private int mYTitleWitdh = 50; // Y轴文字对应的宽度
	private float mXTextSize = 12;
	private float mXTextSizeBlue = 18;
	private float mXTextSizeRed = 13;
	protected int mDistance = 100;// 绘制点之间的x轴距离
	private int yDistance;
	// 上限为20000
	private float totalnum = 20000;
	// 基准线的步数
	private float standardnum = 10000;
	private int width;
	private int mContentMarginTop = 30; // 内容距离顶部的距离
	private int mExtraCouter = 6;
	private int mOuerCicleRadius = 5;
	private int mInnerCicleRadius = 7;
	private float mTrendLineSize = 1.2f; // 趋势图背景颜色
	private float mAnixTextSize = 8;
	private int scrollX;
	private int height;

	/**是否可见*/
	boolean isVisible = false;

	/** 控制滑动的线的画笔 */
	private Paint linePaint;

	/**设置标注文字的字体大小*/
	private float centerTextSize = 12;

	/** 标注显示的图片 */
	private Bitmap mDegree;

	/**横竖屏切换图片*/
	private Bitmap mScreenBitmap;

	/**标注图片中的文本颜色*/
	private int mTaggColor =0xFF7ED321 ;
	
	/** 默认中间线位置 */
	public float moveDistance = 500;

	/** 中间线点击位置变化控制 */
	private boolean isTouched = false;

	/** 由于存储各个数据点X轴的距离 */
	private int[] paintValue;

	/** 是否绘制移动的直线，在OnTouchEvent 事件控制 */
	private boolean mCanvasMoveLine = false;

	/**绘制弹出的标注提示*/
	private boolean mCanvasTaggDrawable = false;

	/**移动的状态，用于控制两点范围的时候移动的中心线不绘制*/
	private boolean moveState = false;

	/**移动的中心点控制弹性滑动*/
	private int moveCenter = 0;

	/** 显示的步数、日期*/
	private String stepValue ="02/25 步数:2500";



	public MoveDemoView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	public MoveDemoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MoveDemoView(Context context) {
		this(context, null);
	}



	/**
	 * 重写onMeasure()来设置view的宽高属性
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension((int) widthMeasureSpec, (int) heightMeasureSpec);
	}


	private void init(Context context) {
		mLineSizeStroke = (int) sp2px(context, mLineSizeStroke);
		mLineCenterSizeStroke = (int) sp2px(context, mLineCenterSizeStroke);
		mYTitleWitdh = (int) dp2px(context, mYTitleWitdh);
		mOuerCicleRadius = (int) dp2px(context, mOuerCicleRadius);
		mInnerCicleRadius = (int) dp2px(context, mInnerCicleRadius);
		mTrendLineSize = (int) dp2px(context, mTrendLineSize);
		mAnixTextSize = sp2px(context, mAnixTextSize);
		mXTextSize = sp2px(context, mXTextSize);
		mXTextSizeBlue = sp2px(context, mXTextSizeBlue);
		mXTextSizeRed = sp2px(context, mXTextSizeRed);
		mXAnixValueHight = (int) dp2px(context, mXAnixValueHight);
		mContentMarginTop = (int) dp2px(context, mContentMarginTop);
		marginRight = (int) dp2px(context, marginRight);
		centerTextSize = sp2px(context, centerTextSize);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setStyle(Style.STROKE);
		mPaint.setTextSize(mAnixTextSize);

		/**
		 * 标注中心线的 Paint
		 */
		linePaint = new Paint();
		linePaint.setAntiAlias(true);
		linePaint.setStyle(Style.STROKE);
		linePaint.setColor(Color.parseColor("#BBE9F9"));
		linePaint.setStrokeWidth(4.0f);
		linePaint.setStyle(Style.FILL);// 设置填满


		mRect = new Rect();
		mPath = new Path();

		/**
		 * 外部圆点Paint
		 */
		mOuterCiclePaint = new Paint();
		mOuterCiclePaint.setTextAlign(Align.CENTER);
		mOuterCiclePaint.setAntiAlias(true);
		mOuterCiclePaint.setColor(mOuterCicleColor);

		/**
		 * 内部圆点的Paint
		 */
		mInnerCiclePaint = new Paint();
		mInnerCiclePaint.setTextAlign(Align.CENTER);
		mInnerCiclePaint.setAntiAlias(true);
		mInnerCiclePaint.setColor(mInnerCicleColor);
		mYanixValues = new ArrayList<String>();
		mYanixValues.add("");
		mYanixValues.add("");
		mYanixValues.add("10000");
		mYanixValues.add("");
		mYanixValues.add("");

		mDegree = BitmapFactory.decodeResource(getResources(),
				R.drawable.energy_degree);

		mScreenBitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.icon_screen_rote);

	}

	/**
	 * 添加x轴和y轴的数值
	 * 
	 * @param mXanixValues
	 *            x轴上的日期值
	 * @param mDatas
	 *            步行数据
	 */
	public void addXYDates(List<String> mXanixValues, List<Integer> mDatas,
			int position) {
		if (this.mXanixValues == null || this.mXanixValues == null) {
			this.mXanixValues = new LinkedList<String>();
			this.mDatas = new LinkedList<Integer>();
		}
		this.mXanixValues.clear();
		this.mDatas.clear();
		points.clear();
		for (int i = 0; i < mXanixValues.size(); i++) {
			this.mXanixValues.add(0, mXanixValues.get(i));
			this.mDatas.add(0, mDatas.get(i));
		}
		paintValue = new int[position];
		this.mYLineCount = position;
		updateStepData(mYLineCount, mDatas);
		invalidate();

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		width = getWidth();
		width = width - 2 * mInnerCicleRadius - leftoffset - marginRight;
		// 计算出x轴的单位距离
		mDistance = (width - mYTitleWitdh) / (mYLineCount - 1);

		height = getHeight();
		scrollX = getScrollX();
		float orgStroke = mPaint.getStrokeWidth();
		float orgStrokeCicle = mOuterCiclePaint.getStrokeWidth();
		// y轴的真实距离
		yDistance = height - mXAnixValueHight - mContentMarginTop;
		float yLen = yDistance / totalnum;
		if (mYanixValues != null && mYanixValues.size() != 0) {
			// 将yDistance变成y轴上的单位距离
			yDistance = yDistance / (mYanixValues.size() - 1);
		}
		drawableMoveLine(canvas, height);
		drawableVertical(canvas, height,true);
		mPaint.setColor(mEndLine);
		mPaint.setStyle(Style.FILL_AND_STROKE);
		mPaint.setStrokeWidth(3f);
		// 绘制横向的灰色直线
		for (int i = 0; i < mYanixValues.size(); i++) {
			if (i == 0) {
				// 上边界
				mPaint.setColor(mBackgroudColor);
				mPaint.setStrokeWidth(1f);
				canvas.drawLine(scrollX + mYTitleWitdh - xoffset,
						mContentMarginTop, scrollX + getWidth(),
						mContentMarginTop, mPaint);
			} else if (i == 2) {
				// 10000步的统计线
				if (totalnum != 20000) {
					mPaint.setColor(Color.parseColor(mLineColor));
					mPaint.setStyle(Style.STROKE);
					mPaint.setStrokeWidth(1f);
					canvas.drawLine(scrollX + mYTitleWitdh - xoffset,
							mContentMarginTop + yDistance * i, scrollX
							+ getWidth(), mContentMarginTop + yDistance
							* i, mPaint);
				}
			} else if(i == 4){
				mPaint.setColor(Color.parseColor(mLineColor));
				mPaint.setStrokeWidth(1f);
				canvas.drawLine(scrollX + mYTitleWitdh - xoffset,
						mContentMarginTop + yDistance * i,
						scrollX + getWidth(),
						mContentMarginTop + yDistance * i, mPaint);
			} else {

				showOrientationLine(canvas,i);
				// 绘制10000步的绿线
				mPaint.setColor(mTaggColor);
				mPaint.setStyle(Style.FILL_AND_STROKE);
				mPaint.setStrokeWidth(3f);
				int startY = (int) (height - (yLen * 10000 + mXAnixValueHight) - 2);
				canvas.drawLine(scrollX + mYTitleWitdh + leftoffset, startY,
						scrollX + getWidth(), startY, mPaint);
			}
		}
		mRect.set(scrollX, 0, scrollX + mYTitleWitdh + leftoffset, height);
		mPaint.setColor(mBackgroudColor);  //mBackgroudColor
		canvas.drawRect(mRect, mPaint);
		mPaint.setColor(Color.parseColor(mLineColor));
		// draw path
		if (mDatas != null && mDatas.size() >= 0) {

			int startPosition = scrollX / mDistance - mExtraCouter;

			int endPosition = scrollX / mDistance + mYLineCount + mExtraCouter;

			if (startPosition < 0) {
				startPosition = 0;
			}
			if (endPosition > mDatas.size()) {
				endPosition = mDatas.size();
			}

			// 绘制折线图中点与点的连接线
			mPaint.setColor(mPathColor);
			mPaint.setStyle(Style.STROKE);
			mPaint.setStrokeWidth(mTrendLineSize);
			for (int i = startPosition; i < endPosition; i++) {
				int startX = scrollX + mYTitleWitdh + mDistance * i
						+ leftoffset;
				int startY = (int) (height
						- (yLen * mDatas.get(i) + mXAnixValueHight) - 2);
				if (i == startPosition) {
					mPath.moveTo(startX, startY);
				} else {
					mPath.lineTo(startX, startY);
				}

			}
			canvas.drawPath(mPath, mPaint);
			canvas.save();
			mPaint.setStrokeWidth(orgStroke);
			mPath.reset();
			// 绘制节点圆
			mOuterCiclePaint.setColor(mOuterCicleColor);
			mOuterCiclePaint.setStyle(Style.FILL);
			mOuterCiclePaint.setStrokeWidth(mTrendLineSize);
			mInnerCiclePaint.setStrokeWidth(mTrendLineSize);
			for (int i = startPosition; i < endPosition; i++) {
				int startX = scrollX + mYTitleWitdh + mDistance * i
						+ leftoffset;
				int startY = (int) (height - (yLen * mDatas.get(i) + mXAnixValueHight));

				canvas.drawCircle(startX, startY, mInnerCicleRadius,
						mInnerCiclePaint);
				// 超过10000万的画空心圆
				if (mDatas.get(i) >= standardnum) {
					canvas.drawCircle(startX, startY, mOuerCicleRadius,
							mOuterCiclePaint);
				}
				paintValue[i] = startX;
				PointF point = new PointF(startX, startY);
				points.add(point);	

			}
			mOuterCiclePaint.setStrokeWidth(orgStrokeCicle);
			mInnerCiclePaint.setStrokeWidth(orgStrokeCicle);

			drawGradientColor(canvas);


			// 绘制x轴坐标轴下标数值
			mPaint.setColor(mTextDay);
			mPaint.setStyle(Style.FILL);
			mPaint.setStrokeWidth(1f);
			mPaint.setTextSize(mXTextSize);
			float textBaseY_x_blow = getHeight() - mXAnixValueHight / 4;
			for (int i = startPosition; i < endPosition; i++) {
				int startX = scrollX + mDistance * (i - 1);
				int endY = scrollX + mDistance * (i + 1);
				mRect.set(startX, height - mXAnixValueHight, endY, height);
				// 带有“月”标记的日期需要变为红色
				if (mXanixValues.get(i).contains("月")) {
					mPaint.setTextSize(mXTextSizeRed);
					mPaint.setColor(mTextMonthGray);
				} else {
					mPaint.setTextSize(mXTextSize);
					mPaint.setColor(mTextDay);
				}
				if (mYLineCount==7) {//七天数据的 (日期分段) 

					if (i == 0 || i == 3 || i == 6) {
						if (i == 0) {
							canvas.drawText(mXanixValues.get(i), mDistance * i + leftoffset
									+ mYTitleWitdh+(mXTextSize/2)+(leftoffset/2), textBaseY_x_blow, mPaint);
						}else {
							canvas.drawText(mXanixValues.get(i), mDistance * i + leftoffset
									+ mYTitleWitdh, textBaseY_x_blow, mPaint);
						}
					}
				} else { //三十天数据的(日期分段)
					if (i == 0 || i == 10 || i ==19 || i == 29 ) {
						if (i == 0) {
							canvas.drawText(mXanixValues.get(i), mDistance * i + leftoffset
									+ mYTitleWitdh+(mXTextSize/2)+(leftoffset/2), textBaseY_x_blow, mPaint);
						} else {
							canvas.drawText(mXanixValues.get(i), mDistance * i + leftoffset
									+ mYTitleWitdh, textBaseY_x_blow, mPaint);
						}
					}
				}

			}
		}
		// 绘制Y轴与坐标轴下标数值
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Style.FILL);
		mPaint.setStrokeWidth(1f);
		mPaint.setTextSize(mAnixTextSize);
		mOuterCiclePaint.setColor(mBackgroudColor);
		mRect.set(scrollX, 0, scrollX + mYTitleWitdh - 10, height);
		canvas.drawRect(mRect, mOuterCiclePaint);

		if (mYanixValues != null && mYanixValues.size() > 0) {
			for (int i = 0; i < mYanixValues.size(); i++) {
				mPaint.setColor(mTaggColor);
				mPaint.setStrokeWidth(1f);
				int startY = (int) (height - (yLen * 10000 + mXAnixValueHight - xoffset));
				canvas.drawText(mYanixValues.get(i), scrollX + mYTitleWitdh / 4
						* 3, startY, mPaint);

			}
		}
		if (mCanvasTaggDrawable) {
			drawableTaggValue(canvas);
			drawableTaggBitmap(canvas);
		}
		mPaint.setStrokeWidth(orgStroke);
	}


	/**
	 * 绘制折线图的填充颜色
	 * @param canvas
	 */
	private void drawGradientColor(Canvas canvas) {
		Paint pathPaint=new Paint();
		//		LinearGradient gradient = new LinearGradient(100, 0, 100, 100, Color.RED, Color.YELLOW, Shader.TileMode.MIRROR);  
		//		pathPaint.setShader(gradient);  
		pathPaint.setColor(Color.parseColor("#F77226"));
		pathPaint.setAlpha(51);

		/* 裁剪出一个需要的矩阵图 */
		Path Rcpath = new Path();
		PointF point = null;
		coordinateMarginLeft = scrollX + mYTitleWitdh  + leftoffset;
		mTotalHeight = height -  mXAnixValueHight;

		Rcpath.moveTo(coordinateMarginLeft, mTotalHeight);	//原点
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			Rcpath.lineTo(point.x, point.y);
		}
		Rcpath.lineTo(point.x, mTotalHeight);
		Rcpath.close();
		canvas.drawPath(Rcpath, pathPaint);
	}
	private ArrayList<PointF> points = new ArrayList<PointF>(); // 各个点
	private float coordinateMarginLeft;	//坐标系距离左边的距离(px)
	private float mTotalHeight; // Y轴的长度（只是数据的高度，不包括上方和下方的留空）


	/**
	 * 绘制纵向的直线
	 * @param canvas
	 * @param height
	 * @param isVisible 是否可见
	 */
	private void drawableVertical(Canvas canvas, int height,boolean isVisible) {
		mPaint.setStrokeWidth(2.5f);
		mPaint.setColor(Color.parseColor("#999999"));
		mPaint.setStyle(Style.STROKE);
		// 绘制竖直的灰色直线
		if (isVisible) {
			for (int i = 0; i < mYLineCount; i++) {
				int startX = scrollX + mYTitleWitdh + mDistance * i + leftoffset;
				if (i == 0 ) {
					canvas.drawLine(startX, mContentMarginTop, startX, height
							- mXAnixValueHight, mPaint);
				}else {
//					canvas.drawLine(startX, mContentMarginTop, startX, height
//												- mXAnixValueHight, mPaint);
				}
			}
		}
	}

	/**
	 * 是否显示横向的线
	 * @param canvas
	 * @param i 下标
	 */
	public void showOrientationLine(Canvas canvas,int i){

		mPaint.setColor(Color.parseColor(mLineColor));
		mPaint.setStyle(Style.STROKE);
		mPaint.setStrokeWidth(1f);
		if (isVisible) {
			canvas.drawLine(scrollX + mYTitleWitdh - xoffset,
					mContentMarginTop + yDistance * i,
					scrollX + getWidth(),
					mContentMarginTop + yDistance * i, mPaint);
		}
	}


	/**
	 * 绘制标注的图片
	 * @param canvas
	 */
	public void drawableTaggBitmap(Canvas canvas){
		// 顶部的标注显示框
		canvas.drawBitmap(mDegree, moveDistance - (mDegree.getWidth() / 2),
				(mContentMarginTop - mDegree.getHeight()), mPaint);// 绘制的图片和之前的一模一样
	}


	/**
	 * 绘制标注上的文字 value
	 * 
	 * @param canvas
	 */
	private void drawableTaggValue(Canvas canvas) {
		mPaint.setColor(Color.parseColor("#20AAF3"));
		mPaint.setStrokeWidth(2.5f);
		mPaint.setTextSize(centerTextSize);
		canvas.drawText(stepValue, moveDistance, mContentMarginTop
				- (mDegree.getHeight() / 2), mPaint);
	}

	/**
	 * 绘制能够移动的(标注)线
	 * 
	 * @param canvas
	 * @param height
	 */
	private void drawableMoveLine(Canvas canvas, int height) {
		if (mCanvasMoveLine) {
			// 中间竖直线--
			canvas.drawLine(moveDistance, mContentMarginTop, moveDistance, height
					- mXAnixValueHight, linePaint);
		}
	}



	/**
	 * 采用标准来格式化数据，更新出一屏能展示出来的的数据（标准祥见类注释说明：步行数据y轴变化标准）
	 * 
	 * @param days
	 *            一屏展示7天数据，此参数填6(这个参数代表的是下标)
	 * @return
	 */
	private void updateStepData(int days, List<Integer> mDatas) {
		int maxStep = 0;
		List<Integer> childList = new ArrayList<Integer>();
		if (days <= 7) {
			childList.addAll(mDatas);
		} else {
			childList.addAll(mDatas);
		}
		maxStep = getYMax(childList);
		if (maxStep > 60000) {
			// 所有大于10万的数据变为10万
			for (int i = 0; i < mDatas.size(); i++) {
				if (childList.get(i) >= 100000) {
					mDatas.set(i, 100000);
				}
			}
			// 按照10万规则来设置y轴
			totalnum = 100000;

		} else if (maxStep > 20000 && maxStep <= 60000) {
			// 所有大于6万的数据变为6万
			for (int i = 0; i < mDatas.size(); i++) {
				if (childList.get(i) >= 60000) {
					mDatas.set(i, 60000);
				}
			}
			// 按照6万规则来设置y轴
			totalnum = 60000;
		} else {

			for (int i = 0; i < mDatas.size(); i++) {
				if (childList.get(i) >= 20000) {
					mDatas.set(i, 20000);
				}
			}
			// 正常2万 规则来设置y轴
			totalnum = 20000;
		}

	}

	/**
	 * 获取集合最大值
	 * 
	 * @param stepList
	 * @return
	 */
	private int getYMax(List<Integer> stepList) {
		int maxStep = Collections.max(stepList);
		return maxStep;
	}

	float localX = -1;

	
	
	/**
	 * 手势监听事件。
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			Log.i("action-down", " action-down");
			moveDistance = event.getX();
			if (moveDistance >= paintValue[0] && moveDistance <= paintValue[paintValue.length-1]) { // 滑动的边界控制
				mCanvasMoveLine = true;
				isTouched = true;
				invalidate();
			}

			break;
		case MotionEvent.ACTION_MOVE:
			mCanvasMoveLine = true;
			if (! MainActivity.doubleClick)
				if (isTouched) {
					moveDistance = event.getX();// 将此时手势x坐标记录下来, 根据此x重绘中间线、
					Log.d("wangly", "Move--- " + (int) moveDistance);
					int index = getDistanceIndex((int)moveDistance,paintValue);
					Log.d("wangly", "getDistanceIndex index:" + index);
					if(index != -1){
						moveState = true;
						moveDistance +=paintValue[index]- moveDistance;
						moveCenter = paintValue[index];

						stepValue = mXanixValues.get(index)+"  海拔:"+mDatas.get(index);
						mCanvasTaggDrawable = true;	
						invalidate();
					}else{
						mCanvasTaggDrawable = false;
					}
				}
			break;
		case MotionEvent.ACTION_UP:
			Log.i("action-up", " action-up");
			if (!MainActivity.doubleClick) {
				if (isTouched) {
					moveDistance = event.getX();// 记录当下位置坐标
					isTouched = false;
					mCanvasMoveLine = false;
					invalidate();
				}
			}
			mCanvasTaggDrawable = false;
			mCanvasMoveLine = false;
			invalidate();
			break;
		}

		return true;// 将焦点传递给父控件。
	}


	public int getDistanceIndex(final int distance , int[] Values){
		if (distance < Values[0] || distance > Values[Values.length-1]) {
			return -1;
		}
		int index = Values.length -1;
		int start = Values.length / 2;
		int value = Values[start];

		Log.d("YYYY", "索引---"+ index+"  "+"开始  ---"+ start +"数值---- "+value);

		int formerValue = 0, afterValue = 0;
		if(value > distance){
			for (int i = start - 1; i >= 0; i--) {
				value = Values[i];
				if(value < distance){
					index = i;
					break;
				}
			}
			formerValue = value;
			if (index == 7) { // 有时候会出现 角标越界问题，所以在此追加判断
				index = 6;
			}
			afterValue = Values[index + 1];
		}else{
			for (int i = start + 1; i < Values.length; i++) {
				value = Values[i];
				if(value > distance){
					index = i - 1;
					break;
				}
			}
			formerValue = Values[index];
			afterValue = value;
		}

		int formerDiff = distance - formerValue;
		int midelDiff = (afterValue - formerValue) / 2;
		if(formerDiff > midelDiff){
			return index + 1;
		}else{
			return index;
		}
	}


	public float sp2px(Context context, float spValue) {
		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (spValue * fontScale + 0.5f);
	}

	public float dp2px(Context context, float dpValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (dpValue * scale + 0.5f);
	}

}
