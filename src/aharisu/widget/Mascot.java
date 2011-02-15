package aharisu.widget;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;


/**
 * 
 * ビュー内を自由に動き回るマスコットの動作を決定するクラス
 * 
 * @author aharisu
 *
 */
public class Mascot {
	private static final int DirectionFront = 0;
	private static final int DirectionLeft = 3;
	private static final int DirectionRight = 6;
	private static final int DirectionRear = 9;
	
	private static final int[][] DirectionStep = new int[][] {
		{0,1},
		{-1,0},
		{1,0},
		{0,-1},
	};
	
	private static final int StateStop = 0;
	private static final int StateWalkLeft = 1;
	private static final int StateWalkRight = 2;
	
	private static final int WalkStep = 3;
	
	private static final int Interval = 675;
	
	private static final int StopStateToWalkStateRate = 5;
	private static final int WalkStateToStopStateRate = 10;
	
	private static final int DirectionChangeRate = 5;
	
	
	private final Handler mHandler = new Handler();
	private final View mView;
	
	private final Bitmap[] mImages;
	
	private int mCurX = 0;
	private int mCurY = 0;
	
	private int mState = StateStop;
	private int mDirection = DirectionFront;
	
	private final Random mRand = new Random();
	
	
	private final Runnable mUpdate = new Runnable() {
		
		@Override public void run() {
			
			if(mIsStarted) {
				if(mView.getWidth() != 0 && mView.getHeight() != 0) {
					
					//止まる、歩くの状態をランダムに変更
					if(mState == StateStop) {
						mState = (mRand.nextInt() % StopStateToWalkStateRate) == 0 ?
								StateWalkLeft : 
								StateStop;
					} else {
						mState = (mRand.nextInt() % WalkStateToStopStateRate) == 0 ?
								StateStop :
								(mState % 2) + 1;
					}
					
					//向きをランダムに変更
					if(mRand.nextInt() % DirectionChangeRate == 0) {
						int direction;
						do {
							direction = mRand.nextInt(4)*3;
						}while(mDirection == direction);
						mDirection = direction;
					}
					
					int imgWidth = mImages[0].getWidth();
					int imgHeight = mImages[0].getHeight();
					if(mState != StateStop) {
					
						//x座標とy座標を決める
						int x = mCurX + (imgWidth / WalkStep) * DirectionStep[mDirection / 3][0];
						int y = mCurY + (imgHeight / WalkStep) * DirectionStep[mDirection / 3][1];
						
						int disableDirectionHorizontal = -1;
						if(x < 0) {
							disableDirectionHorizontal = DirectionLeft;
						} else if(x + imgWidth > mView.getWidth()) {
							disableDirectionHorizontal = DirectionRight;
						}
						
						int disableDirectionVertical = -1;
						if(y < 0) {
							disableDirectionVertical = DirectionRear;
						} else if(y + imgHeight > mView.getHeight()) {
							disableDirectionVertical = DirectionFront;
						}
						
						if(disableDirectionHorizontal != -1 || disableDirectionVertical != -1) {
							int direction;
							do {
								direction = mRand.nextInt(4) * 3;
							} while(direction == disableDirectionHorizontal ||
									direction == disableDirectionVertical);
							
							mDirection = direction;
							x = mCurX + (imgWidth / WalkStep) * DirectionStep[mDirection / 3][0];
							y = mCurY + (imgHeight / WalkStep) * DirectionStep[mDirection / 3][1];
						}
						
						
						int prevX = mCurX;
						int prevY = mCurY;
						mCurX = x;
						mCurY = y;
						int left, top, right, bottom;
						if(prevX < mCurX) {
							left = prevX;
							right = mCurX + imgWidth;
						} else {
							left = mCurX;
							right = prevX + imgWidth;
						}
						if(prevY < mCurY) {
							top = prevY;
							bottom = mCurY + imgHeight;
						} else {
							top = mCurY;
							bottom = prevY + imgHeight;
						}
						
						//再描画
						mView.invalidate(left, top, right, bottom);
					} else {
						mView.invalidate(mCurX, mCurY, mCurX + imgWidth, mCurY + imgHeight);
					}
					
					
				}
				
				mHandler.postDelayed(mUpdate, Interval);
			}
		}
	};
	
	private boolean mIsStarted = false;
	
	public Mascot(View view, Bitmap image) {
		this.mView = view;
		
		mImages = new Bitmap[3*4];
		splitImage(image);
	}
	
	private void splitImage(Bitmap image) {
		int width = image.getWidth() / 3;
		int height = image.getHeight() / 4;
		
		Rect srcRect = new Rect();
		Rect destRect = new Rect(0, 0, width, height);
		for(int y = 0;y < 4;++y) {
			srcRect.top = y * height;
			srcRect.bottom = srcRect.top + height;
			
			for(int x = 0;x < 3;++x) {
			
				Bitmap img = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
				Canvas canvas = new Canvas(img);
				
				srcRect.left = x * width;
				srcRect.right = srcRect.left + width;
				
				canvas.drawBitmap(image, srcRect, destRect, null);
				
				this.mImages[y*3+x] = img;
			}
		}
	}
	
	public void draw(Canvas canvas) {
		if(mIsStarted) {
			canvas.drawBitmap(this.mImages[mDirection +  mState], mCurX, mCurY, null);
		}
	}
	
	public void start() {
		
		mHandler.postDelayed(mUpdate, Interval);
		
		mIsStarted = true;
	}
	
	public void stop() {
		
		mIsStarted = false;
	}
	

}
