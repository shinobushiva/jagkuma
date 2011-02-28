package jag.kumamoto.apps.StampRally;

import android.os.AsyncTask;


public class TextScrollTask extends AsyncTask<Void, Void, Void> {

	private InfoBarView m_View;

	public TextScrollTask(InfoBarView view)
	{
		m_View = view;
	}

	/**
	 * アニメーション処理
	 */
	@Override
	protected Void doInBackground(Void... params) {
		while(true)
		{
			String msg = m_View.popFlowMessage();
			if(msg != null)
			{
				int wsize = m_View.getWidth();
				int tsize = (int)m_View.setFlowMessage(msg);
				for(int i = wsize; i > -tsize; --i)
				{
					m_View.setFlowOffset(i);
					android.os.SystemClock.sleep(20);
				}
			}
			m_View.setFlowMessage("");
		}
	}

}
