package jag.kumamoto.apps.gotochi;

import java.util.ArrayList;

import android.content.Context;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InfoBarView extends LinearLayout {

	/**
	 * 固定表示
	 */
	private TextView m_FixedView;

	/**
	 * フロー表示
	 */
	private TextView m_FlowView;

	/**
	 * フロー表示文字列キュー
	 */
	private ArrayList<String> m_FlowTexts;

	/**
	 * フロー表示更新タスク
	 */
	private TextScrollTask m_ScrollTask;

	/**
	 * 移動速度
	 */
	private int m_FlowSpeed;

	private Handler m_Handler;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public InfoBarView(Context context) {
		super(context);

		m_FlowSpeed = 1;
		m_Handler = new Handler();
		m_FlowTexts = new ArrayList<String>();

		m_FixedView = new TextView(context);
		m_FlowView = new TextView(context);

		m_FixedView.setWidth(LayoutParams.FILL_PARENT);
		m_FlowView.setWidth(LayoutParams.FILL_PARENT);

		m_FixedView.setVisibility(VISIBLE);
		m_FlowView.setVisibility(VISIBLE);

		m_FlowView.setLines(1);
		setOrientation(VERTICAL);
		addView(m_FixedView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		addView(m_FlowView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
	}

	/**
	 * 移動速度を設定する。
	 * @param spd 移動速度
	 */
	public void setFlowSpeed(int spd)
	{
		if(spd <= 0)
		{
			return;
		}
		m_FlowSpeed = spd;
	}

	/**
	 * 移動速度を取得する
	 * @return
	 */
	public int getFlowSpeed()
	{
		return m_FlowSpeed;
	}

	/**
	 * フロー表示文字列を追加する
	 * @param msg
	 */
	public void addFlowMessage(String msg)
	{
		synchronized(m_FlowTexts){
			m_FlowTexts.add(msg);			
		}
	}

	/**
	 * 表示状態になったらタスクを立ち上げる
	 */
	@Override
	protected void onWindowVisibilityChanged(int visibility) {
		super.onWindowVisibilityChanged(visibility);
		
		switch(visibility)
		{
		case VISIBLE:{
			if(m_ScrollTask == null)
			{
				m_ScrollTask = new TextScrollTask(this);
				m_ScrollTask.execute();
			}
			break;
		}
		case INVISIBLE:{
			if(m_ScrollTask != null)
			{
				m_ScrollTask.cancel(true);
				m_ScrollTask = null;
			}
			break;
		}
		}
	}

	/**
	 * フロー表示用文字列を取得する
	 * @return ない場合はnull。
	 */
	public String popFlowMessage()
	{
		synchronized(m_FlowTexts){
			if(m_FlowTexts.isEmpty())
			{
				return null;
			}
			String msg = m_FlowTexts.remove(0);
			return msg;
		}
	}

	/**
	 * 固定表示用文字列を設定する。
	 * @param msg
	 */
	public void setFixedMessage(final String msg)
	{
		m_Handler.post(new Runnable(){
			@Override
			public void run() {
				m_FixedView.setText(msg);
				m_FixedView.invalidate();
			}			
		});
	}

	/**
	 * フロー表示ウィンドウ用文字列を設定する。
	 * @param msg
	 * @return 表示文字列の表示サイズ(pixel)
	 */
	public float setFlowMessage(final String msg)
	{
		m_Handler.post(new Runnable(){
			@Override
			public void run() {
				m_FlowView.setText(msg);
				m_FlowView.invalidate();
			}			
		});
		setFlowOffset((int)m_FlowView.getWidth());
		return m_FlowView.getTextSize() * msg.length();
	}

	/**
	 * 表示オフセットを設定する。タスクで利用する。
	 * @param offset
	 */
	public void setFlowOffset(final int offset)
	{
		m_Handler.post(new Runnable(){
			@Override
			public void run() {
				m_FlowView.setPadding(offset, 0, 0, 0);
			}
		});
	}
}
