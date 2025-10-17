package com.superfiretruck.sketchabit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.util.*;

public class SketchView extends View{
	
	Canvas foreverCanvas;
	Bitmap foreverBitmap; 
	Bitmap revertBitmap;
	Matrix matrix;
	Paint paint;
	float lastX;
	float lastY;
	final int ALPHA = 48;

	public SketchView(Context context, AttributeSet attrs) {
		super(context, attrs);
		matrix = new Matrix();
		importBitmap(Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888)); //size doesnt actually matter here
		paint = new Paint();
		paint.setARGB(ALPHA, 128, 128, 128);
		paint.setAntiAlias(true);
	}
	
	public void updateDisplayMatrix(){
		if (getWidth() != 0 && getHeight() != 0){
			float widthRatio = (float)getWidth()/foreverBitmap.getWidth();
			float heightRatio = (float)getHeight()/foreverBitmap.getHeight();
			matrix.setScale(widthRatio, heightRatio);
		}
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		updateDisplayMatrix();
	}
	
	public void onDraw(Canvas canvas){
		if (foreverBitmap != null){
			canvas.drawBitmap(foreverBitmap, matrix, null);
		}
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if (foreverCanvas == null){
			return false;
		}
		
		float currX = event.getX()/getWidth();
		float currY = event.getY()/getHeight();
		
		float w = foreverCanvas.getWidth();
		float h = foreverCanvas.getHeight();
		
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
		case MotionEvent.ACTION_UP:
			paint.setStrokeWidth(Math.max(Math.abs(lastX-currX), Math.abs(lastY-currY))*w);
			foreverCanvas.drawLine(lastX*w, lastY*h, currX*w, currY*h, paint);
			break;
		}
		lastX = currX;
		lastY = currY;
		
		invalidate();
		return true;
	}
	
	public void clear(){
		foreverCanvas.drawBitmap(revertBitmap, 0, 0, null);
		invalidate();
	}
	
	public void useLighterColor(){
		paint.setARGB(ALPHA, 255,255,255);
	}
	
	public void useDarkerColor(){
		paint.setARGB(ALPHA, 0,0,0);
	}
	
	public void importBitmap(Bitmap bitmap){
		foreverCanvas = new Canvas(bitmap);
		foreverBitmap = bitmap;
		revertBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
		updateDisplayMatrix();
		postInvalidate();
	}
	
	public Bitmap exportBitmap(){
		return foreverBitmap;
	}
	
	
	
}