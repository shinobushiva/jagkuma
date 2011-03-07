package jag.kumamoto.apps.StampRally;

import jag.kumamoto.apps.gotochi.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

public class ThanksActivity extends Activity {

	private TextView row1;
	private TextView row2;
	private TextView row3;

	private TextView row1b;
	private TextView row2b;
	private TextView row3b;

	private Timer timer;

	private AlphaAnimation appear = new AlphaAnimation(0, 1);
	private AlphaAnimation disappear = new AlphaAnimation(1, 0);

	private boolean first = true;

	private List<Key> keys;
	private int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.thanks);

		appear.setRepeatCount(0);
		appear.setFillAfter(true);
		disappear.setRepeatCount(0);
		disappear.setFillAfter(true);

		row1 = (TextView) findViewById(R.id_thanks.row1);
		row2 = (TextView) findViewById(R.id_thanks.row2);
		row3 = (TextView) findViewById(R.id_thanks.row3);

		row1b = (TextView) findViewById(R.id_thanks.row1b);
		row2b = (TextView) findViewById(R.id_thanks.row2b);
		row3b = (TextView) findViewById(R.id_thanks.row3b);

		try {
			InputStream in = getResources().openRawResource(R.raw.thanks);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "UTF-8"));

			keys = new ArrayList<Key>();
			keys.add(new Key("0,,,,"));
			String s;
			while ((s = reader.readLine()) != null) {
				Key key = new Key(s);
				keys.add(key);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		timer = new Timer(true);
		final android.os.Handler handler = new android.os.Handler();
		TimerTaskExtension timerTask = new TimerTaskExtension(handler);
		timer.schedule(timerTask, 0);
	}

	private final class TimerTaskExtension extends TimerTask {
		private final Handler handler;

		private TimerTaskExtension(Handler handler) {
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.post(new Runnable() {
				public void run() {

					Key k1 = keys.get(index);
					index = (index + 1) % keys.size();
					Key k2 = keys.get(index);

					appear.setDuration(k2.duration);
					disappear.setDuration(k2.duration / 2);

					if (first) {
						row1.setText(k2.r1);
						row2.setText(k2.r2);
						row3.setText(k2.r3);
						row1b.setText(k1.r1);
						row2b.setText(k1.r2);
						row3b.setText(k1.r3);

						row1.startAnimation(appear);
						row2.startAnimation(appear);
						row3.startAnimation(appear);
						row1b.startAnimation(disappear);
						row2b.startAnimation(disappear);
						row3b.startAnimation(disappear);

					} else {
						row1.setText(k1.r1);
						row2.setText(k1.r2);
						row3.setText(k1.r3);
						row1b.setText(k2.r1);
						row2b.setText(k2.r2);
						row3b.setText(k2.r3);

						row1b.startAnimation(appear);
						row2b.startAnimation(appear);
						row3b.startAnimation(appear);
						row1.startAnimation(disappear);
						row2.startAnimation(disappear);
						row3.startAnimation(disappear);
					}
					first = !first;

					TimerTaskExtension timerTask = new TimerTaskExtension(
							handler);
					timer.schedule(timerTask, k2.duration);
				}
			});
		}
	}

	private static class Key {
		public long duration = 0;
		public String r1 = "";
		public String r2 = "";
		public String r3 = "";

		public Key(String s) {
			try {
				String[] split = s.split(",");
				duration = Long.parseLong(split[0].trim());
				r1 = split[1].trim();
				r2 = split[2].trim();
				r3 = split[3].trim();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
