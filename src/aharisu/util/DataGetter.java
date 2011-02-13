package aharisu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public final class DataGetter {
	
	public static String readHTML(Context context, int resRawId) throws IOException{
		InputStream in = context.getResources().openRawResource(resRawId);
		
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			StringBuilder builder = new StringBuilder();
			String s;
			while((s = reader.readLine()) != null) {
				builder.append(s);
				builder.append("\n");
			}
			
			return builder.toString();
	}

}
