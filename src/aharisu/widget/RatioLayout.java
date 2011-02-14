package aharisu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsoluteLayout;

/**
 * 
 * ビューを割合指定では位置するレイアウト
 * 
 * @author aharisu
 *
 */
@SuppressWarnings("deprecation")
public class RatioLayout extends AbsoluteLayout {
	
	public RatioLayout(Context context) {
		this(context, null);
	}
	
	public RatioLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public RatioLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	protected boolean checkLayoutParams(android.view.ViewGroup.LayoutParams p) {
		return p instanceof RatioLayout.LayoutParams;
	}
	
	@Override
	protected android.view.ViewGroup.LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(1.0f, 1.0f, 0, 0);
	}
	
	
	@Override public android.view.ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
		return new RatioLayout.LayoutParams(
			attrs.getAttributeFloatValue(null, RatioLayout.LayoutParams.RatioWidthAttrName, 1),
			attrs.getAttributeFloatValue(null, RatioLayout.LayoutParams.RatioHeightAttrName, 1),
			attrs.getAttributeFloatValue(null, RatioLayout.LayoutParams.RatioXAttrName, 0),
			attrs.getAttributeFloatValue(null, RatioLayout.LayoutParams.RatioYAttrName, 0));
	}
	
		
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed) {
			int w = r - l;
			int h = b - t;
			
			int childeCount = getChildCount();
			for(int i = 0;i < childeCount;++i) {
				View child = getChildAt(i);
				
				LayoutParams p = (LayoutParams)child.getLayoutParams();
				p.width = (int)(w * p.ratioWidth);
				p.height = (int)(h * p.ratioHeight);
				p.x = (int)(w * p.ratioX);
				p.y = (int)(h * p.ratioY);
				child.setLayoutParams(p);
			}
		}
		
		forceLayout();
		measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
		
		super.onLayout(changed, l, t, r, b);
	}
	
	public static class LayoutParams extends AbsoluteLayout.LayoutParams {
		private static final String RatioWidthAttrName = "layout_ratioWidth";
		private static final String RatioHeightAttrName = "layout_ratioHeight";
		private static final String RatioXAttrName = "layout_ratioX";
		private static final String RatioYAttrName = "layout_ratioY";
		
		public float ratioWidth;
		public float ratioHeight;
		public float ratioX;
		public float ratioY;
		
		public LayoutParams(float ratioWidth, float ratioHeight, float ratioX, float ratioY) {
			super(0, 0, 0, 0);
			
			this.ratioWidth = ratioWidth;
			this.ratioHeight = ratioHeight;
			this.ratioX = ratioX;
			this.ratioY = ratioY;
		}
		
	}
	
}
