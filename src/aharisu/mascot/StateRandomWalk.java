package aharisu.mascot;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Rect;

/**
 * 
 * ランダム歩行を行う基本ステートクラス
 * 
 * @author aharisu
 *
 */
public class StateRandomWalk extends MascotState{
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
	//private static final int StateWalkRight = 2;
	
	private static final int WalkStep = 3;
	
	private static final int StopStateToWalkStateRate = 5;
	private static final int WalkStateToStopStateRate = 10;
	
	private static final int DirectionChangeRate = 5;
	
	
	
	private int mCurX = 0;
	private int mCurY = 0;
	
	private int mState = StateStop;
	private int mDirection = DirectionFront;
	
	private final Bitmap[] mImages;
	private Bitmap mCurImage;
	
	private final Random mRand = new Random();
	
	public StateRandomWalk(IMascot mascot, Bitmap image) {
		super(mascot);
		
		mImages = new Bitmap[3*4];
		splitImage(image, mImages, 3, 4);
	}
	
	@Override public void entry(Rect bounds) {
		mCurImage = mImages[0];
		
		centering(bounds, mCurImage.getWidth(), mCurImage.getHeight());
		
		mCurX = bounds.left;
		mCurY = bounds.top;
	}
	
	@Override public boolean update() {
		
		if(mMascot.getViewWidth() != 0 && mMascot.getViewHeight() != 0) {
			
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
				} else if(x + imgWidth > mMascot.getViewWidth()) {
					disableDirectionHorizontal = DirectionRight;
				}
				
				int disableDirectionVertical = -1;
				if(y < 0) {
					disableDirectionVertical = DirectionRear;
				} else if(y + imgHeight > mMascot.getViewHeight()) {
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
				
				mCurImage = mImages[mDirection +  mState];
				//再描画
				mMascot.redraw(left, top, right, bottom);
			} else {
				mCurImage = mImages[mDirection +  mState];
				mMascot.redraw(mCurX, mCurY, mCurX + imgWidth, mCurY + imgHeight);
			}
			
		}
		
		return true;
	}

	@Override public Bitmap getImage() {
		return mCurImage;
	}
	
	@Override public void getBounds(Rect outRect) {
		if(mCurImage != null) {
			outRect.set(mCurX, mCurY, mCurX + mCurImage.getWidth(), mCurY + mCurImage.getHeight());
		} else {
			outRect.setEmpty();
		}
	}
	
	

}
