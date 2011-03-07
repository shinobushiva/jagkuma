package aharisu.mascot;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path.Direction;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * 
 * マスコットからでる吹き出し用のテキストビュー
 * 
 * @author aharisu
 *
 */
public class TextBalloonView extends TextView{
	private static final int PaddingTop = 1;//単位はdip
	private static final int PaddingBottom = 15;//単位はdip
	private static final int PaddingLeft = 6;//単位はdip
	private static final int PaddingRight = 6;//単位はdip
	
	private final float mScaledDensity;
	
	private Rect mMascotBounds = new Rect();
	
	public TextBalloonView(Context context) {
		super(context);
		
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE))
			.getDefaultDisplay().getMetrics(metrics);
		
		mScaledDensity = metrics.scaledDensity;
		
		init();
	}
	
	public TextBalloonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE))
			.getDefaultDisplay().getMetrics(metrics);
		
		mScaledDensity = metrics.scaledDensity;
		
		init();
	}
	
	public TextBalloonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE))
			.getDefaultDisplay().getMetrics(metrics);
		
		mScaledDensity = metrics.scaledDensity;
		
		init();
	}
	
	private void init() {
		setPadding(
				(int)(PaddingLeft * mScaledDensity),
				(int)(PaddingTop * mScaledDensity),
				(int)(PaddingRight * mScaledDensity),
				(int)(PaddingBottom * mScaledDensity));
	}
	
	public void setMascotPosition(Rect mascotBounds) {
		mMascotBounds.set(mascotBounds);
	}
	
	@Override protected void onDraw(Canvas canvas) {
		int paddingBottom = getPaddingBottom();
		RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight() - paddingBottom);
		
		Rect bounds = new Rect();
		getGlobalVisibleRect(bounds);
		int mascotLeft = mMascotBounds.left - bounds.left;
		int mascotRight = mMascotBounds.right - bounds.left;
		int mascotCenterX = mascotLeft + (mascotRight - mascotLeft) / 2;
		
		//吹き出しからマスコットを指す矢印
		int startArrowPosX;
		int endArrowPosX;
		int arrowTipPosX;
		if(mascotCenterX < rect.centerX()) {
			//マスコットの右側に矢印
			startArrowPosX = mascotRight + (int)(8 * mScaledDensity);
			endArrowPosX = startArrowPosX + (int)(20 * mScaledDensity);
			arrowTipPosX = mascotRight - (int)(7 * mScaledDensity);
		} else {
			//マスコットの左側に矢印
			endArrowPosX = mascotLeft - (int)(8 * mScaledDensity);
			startArrowPosX = endArrowPosX - (int)(20 * mScaledDensity);
			arrowTipPosX = mascotLeft + (int)(7 * mScaledDensity);
		}
		
		
		//線の太さ
		float lineWidth = 2 * mScaledDensity;
		
		//背景塗りは線の太さを考慮して少し小さくする
		rect.inset(lineWidth / 2.5f, lineWidth / 2.5f);
		
		Path path = new Path();
		path.addRoundRect(rect, 20, 20, Direction.CW);
		path.moveTo(startArrowPosX, rect.bottom);
		path.lineTo(arrowTipPosX, rect.bottom + paddingBottom);
		path.lineTo(endArrowPosX, rect.bottom);
		
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		canvas.clipPath(path);
		
		//背景色描画
		canvas.drawColor(0xffffffff);
		
		//矢印を接続する部分は縁取りを塗らない
		rect.inset(-lineWidth / 2.5f, -lineWidth / 2.5f);
		path.reset();
		path.moveTo(startArrowPosX, rect.bottom - lineWidth);
		path.lineTo(arrowTipPosX, rect.bottom + paddingBottom);
		path.lineTo(endArrowPosX, rect.bottom - lineWidth);
		path.close();
		path.setFillType(Path.FillType.INVERSE_WINDING);
		
		canvas.restore();
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		canvas.clipPath(path);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeCap(Cap.ROUND);
		paint.setStrokeJoin(Join.ROUND);
		paint.setColor(0xff000000);
		paint.setStrokeWidth(lineWidth);
		paint.setStyle(Style.STROKE);
		
		//吹き出しのふち塗り
		canvas.drawRoundRect(rect, 20, 20, paint);
		
		//クリップをなしの状態に戻す
		canvas.restore();
		
		//矢印描画
		paint.setStrokeWidth(lineWidth * 1.2f);
		canvas.drawLines(new float[] {
				startArrowPosX, rect.bottom,
				arrowTipPosX, rect.bottom + paddingBottom,
				arrowTipPosX, rect.bottom + paddingBottom,
				endArrowPosX, rect.bottom,
			}, paint);
		
		
		super.onDraw(canvas);
	}

}
