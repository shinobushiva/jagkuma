package aharisu.widget;

import jag.kumamoto.apps.gotochi.R;
import aharisu.util.ImageUtill;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


/**
 * 
 * Mascotクラスのインスタンスを表示するためのビュークラス
 * 
 * @author aharisu
 *
 */
public class MascotView extends View{
	
	private final Mascot mMascot;
	
	public MascotView(Context context) {
		super(context);
		
		
		mMascot = new Mascot(this, ImageUtill.loadImage(
				context.getResources().openRawResource(R.raw.kumamon)
				, 1024, 1024));
	}
	
	public MascotView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		mMascot = new Mascot(this, ImageUtill.loadImage(
				context.getResources().openRawResource(R.raw.kumamon)
				, 1024, 1024));
	}
	
	public MascotView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		mMascot = new Mascot(this, ImageUtill.loadImage(
				context.getResources().openRawResource(R.raw.kumamon)
				, 1024, 1024));
	}
	
	@Override protected void onFinishInflate() {
		super.onFinishInflate();
		
		mMascot.start();
	}
	
	
	@Override public void draw(Canvas canvas) {
		super.draw(canvas);
		
		mMascot.draw(canvas);
	}
	

}
