package jag.kumamoto.apps.StampRally;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

public class FlowingTextView extends TextView{
	
	private class TextScrollTask extends AsyncTask<Void, Void, Void> {
		boolean mCancel = false;
		
		@Override protected void onCancelled() {
			super.onCancelled();
			
			mCancel = true;
		}
		
		@Override protected Void doInBackground(Void... params) {
			while(!mCancel) {
				String msg = popFlowMessage();
				if(msg != null) {
					int wsize = getWidth();
					float tsize = setFlowMessage(msg);
					for(float i = wsize;i > -tsize; i -= mFlowSpeed) {
						setFlowOffset((int)i);
						if(mCancel) {
							break;
						}
						android.os.SystemClock.sleep(15);
					}
				} else {
					if(mFixedText != null) {
						mHandler.post(new Runnable() {
							@Override public void run() {
								setText(mFixedText);
								mFixedText = null;
								
								setGravity(mGravity);
								setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
							}
						});
					}
					android.os.SystemClock.sleep(500);
				}
			}
			
			return null;
		}
	};
	private TextScrollTask mScrollTask;
	
	private CharSequence mFixedText;
	
	private int mGravity;
	private int mPaddingTop;
	private int mPaddingBottom;
	private int mPaddingLeft;
	private int mPaddingRight;
	
	/**
	 * フロー表示文字列キュー
	 */
	private final Queue<String> mFlowTexts = new LinkedBlockingQueue<String>();
	
	/**
	 * 移動速度
	 */
	private float mFlowSpeed = 1.2f;
	
	private final Handler mHandler = new Handler();
	
	public FlowingTextView(Context context) {
		super(context);
		
		setSingleLine(true);
	}
	
	public FlowingTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		setSingleLine(true);
	}
	
	public FlowingTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
		setSingleLine(true);
	}
	
	@Override public void setText(CharSequence text, BufferType type) {
		if(text == null) {
			text = "";
		}
		
		super.setText(text, type);
	}
	
	/**
	 * 移動速度を設定する。
	 * @param spd 移動速度
	 */
	public void setFlowSpeed(float speed) {
		mFlowSpeed = speed;
	}
	
	/**
	 * 移動速度を取得する
	 * @return
	 */
	public float getFlowSpeed(){
		return mFlowSpeed;
	}
	
	public void addFlowMessage(String msg) {
		if(msg == null)
			msg = "";
		
		synchronized (mFlowTexts) {
			mFlowTexts.offer(msg);
		}
	}
	
	@Override protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		
		switch(visibility) {
		case VISIBLE:
			if(mScrollTask == null) {
				mScrollTask = new TextScrollTask();
				mScrollTask.execute();
			}
			break;
		case INVISIBLE:
		case GONE:
			if(mScrollTask != null) {
				Log.i("TEST", "Invisible");
				mScrollTask.cancel(true);
				mScrollTask = null;
			}
			break;
		}
	}
	
	/**
	 * フロー表示用文字列を取得する
	 * @return ない場合はnull。
	 */
	public String popFlowMessage() {
		synchronized (mFlowTexts) {
			return mFlowTexts.poll();
		}
	}
	
	public float setFlowMessage(final String msg) {
		mHandler.post(new Runnable() {
			@Override public void run() {
				if(mFixedText == null) {
					mFixedText = getText();
					
					mGravity = getGravity();
					mPaddingTop = getPaddingTop();
					mPaddingBottom = getPaddingBottom();
					mPaddingLeft = getPaddingLeft();
					mPaddingRight = getPaddingRight();
					
					setGravity(Gravity.CENTER_VERTICAL);
				}
				
				setText(msg);
				invalidate();
			}
		});
		
		setFlowOffset((int)getWidth());
		return getTextSize()* msg.length();
	}
	
	public void setFlowOffset(final int offset) {
		mHandler.post(new Runnable() {
			@Override public void run() {
				setPadding(offset, 0, 0, 0);
			}
		});
	}

}
