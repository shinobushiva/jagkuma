package aharisu.widget;

import aharisu.util.AsyncDataGetter;
import aharisu.util.Size;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * 
 * テキスト部分に画像を表示することのできるチェックボックス
 * 
 * @author aharisu
 *
 */
public class ImageCheckBox extends CheckBox{
	private static final int MaxImageSize = 200;
	
	private Bitmap mImage = null;
	
	public ImageCheckBox(Context context) {
		super(context);
	}
	
	public ImageCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ImageCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	public void setImage(Bitmap image) {
		this.mImage = image;
		
		requestLayout();
		forceLayout();
		invalidate();
	}
	
	public void setImageURL(String url) {
		AsyncDataGetter.getBitmap(url, new AsyncDataGetter.BitmapCallback() {
			
			@Override public void onGetData(Bitmap data) {
				mImage = data;
				
				requestLayout();
				forceLayout();
				invalidate();
			}
			
			@Override public Size getMaxImageSize() {
				return new Size(MaxImageSize, MaxImageSize);
			}
		});
	}
	
	
	@Override protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mImage != null) {
			canvas.drawBitmap(mImage, getPaddingLeft(), 0, null);
		}
	}
	
	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		
		if(mImage != null) {
			int left = getPaddingLeft();
			
			int width = getMeasuredWidth();
			int containImageWidth = left + mImage.getWidth();
			if(width < containImageWidth) {
				width = containImageWidth;
			}
			
			int height = getMeasuredHeight();
			int containImageHeight = left + mImage.getHeight();
			if(height < containImageHeight) {
				height = containImageHeight;
			}
			
			if(getMeasuredWidth() != width ||
					getMeasuredHeight() != height) {
				setMeasuredDimension(containImageWidth, containImageHeight);
			}
		}
	}
	

}
