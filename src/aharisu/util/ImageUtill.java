package aharisu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.provider.MediaStore;


/**
 * 
 * 画像の保存とロードを扱うユーティリティクラス
 * 
 * @author aharisu
 *
 */
public final class ImageUtill {
	private static final int ThumbStride = 40;
	
	
	private static String genUniquePath(String directoryPath) {
		String name = new StringBuilder()
			.append(directoryPath)
			.append(new SimpleDateFormat("yy_MM_dd").format(new Date()))
			.toString();
		
		String baseName = name;
		int number = 2;
		do{
			String filePath = name + ".jpg";
			
			//重複しているかの確認は排他的にしなくてもいいのかな？
			if(!new File(filePath).exists())
				return filePath;
			
			name = new StringBuffer(baseName)
				.append('(').append(number).append(')').toString();
			++number;
		}while(true);
		
	}
	
	public static Bitmap loadImage(InputStream is, int width, int height) {
		Bitmap image = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(is, null, options);
		options.inJustDecodeBounds = false;
		
		Size size = fitting(options.outWidth, options.outHeight, width, height);
		
		Bitmap tmp = null;
		float factor = options.outWidth / (float)size.width;
		if(factor >= 2) {
			options.inSampleSize = (int)factor;
			tmp = BitmapFactory.decodeStream(is, null, options);
		} else {
			tmp = BitmapFactory.decodeStream(is);
		}
		
		if(tmp == null)
			return null;
		
		if(factor != (int)factor) {
			image = Bitmap.createScaledBitmap(tmp, size.width, size.height, true);
			tmp.recycle();
			tmp = null;
		} else {
			image = tmp;
		}
		
		return image;
	}
	
	public static Bitmap loadImage(byte[] buffer, int width, int height) {
		final int MaxStride = 1024;
		
		Bitmap image = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
		options.inJustDecodeBounds = false;
		if(options.outWidth > MaxStride || options.outHeight > MaxStride)
			return null;
		
		Size size = fitting(options.outWidth, options.outHeight, width, height);
		
		Bitmap tmp = null;
		float factor = options.outWidth / (float)size.width;
		if(factor >= 2) {
			options.inSampleSize = (int)factor;
			tmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length, options);
		} else {
			tmp = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
		}
		
		if(tmp == null) {
			return null;
		}
		
		if(factor != (int)factor) {
			image = Bitmap.createScaledBitmap(tmp, size.width, size.height, true);
			tmp.recycle();
			tmp = null;
		} else {
			image = tmp;
		}
		
		return image;
	}
	
	public static Bitmap loadImage(String filePath, int width, int height) {
		Bitmap image = null;
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inJustDecodeBounds = false;
		
		Size size = fitting(options.outWidth, options.outHeight, width, height);
		
		Bitmap tmp = null;
		float factor = options.outWidth / (float)size.width;
		if(factor >= 2) {
			options.inSampleSize = (int)factor;
			tmp = BitmapFactory.decodeFile(filePath, options);
		} else {
			tmp = BitmapFactory.decodeFile(filePath);
		}
		
		if(tmp == null)
			return null;
		
		if(factor != (int)factor) {
			image = Bitmap.createScaledBitmap(tmp, size.width, size.height, true);
			tmp.recycle();
			tmp = null;
		} else {
			image = tmp;
		}
		
		return image;
	}
	
	public static Size fitting(int srcWidth, int srcHeight, int destWidth, int destHeight) {
		int rectWidth, rectHeight;
		
		if(destWidth < srcWidth || destHeight < srcHeight) {
			
			if(srcWidth < srcHeight) {
				if(destWidth < destHeight) {
					int height = (int)(destWidth * (srcHeight / (float) srcWidth));
					rectHeight = (height > destHeight) ? destHeight : height;
					rectWidth = (int)(rectHeight * (srcWidth / (float)srcHeight));
				} else {
					rectWidth = (int)(destHeight * (srcWidth / (float)srcHeight));
					rectHeight = (int)(rectWidth * (srcHeight / (float)srcWidth));
				}
			} else {
				if(destWidth < destHeight) {
					rectHeight = (int)(destWidth * (srcHeight / (float)srcWidth));
					rectWidth = (int)(rectHeight * (srcWidth / (float)srcHeight));
				} else {
					int width = (int)(destHeight * (srcWidth / (float)srcHeight));
					rectWidth = (width > destWidth) ? destWidth : width;
					rectHeight = (int)(rectWidth * (srcHeight / (float)srcWidth));
				}
			}
			
		} else {
			rectWidth = srcWidth;
			rectHeight = srcHeight;
		}
		
		return new Size(rectWidth, rectHeight);
	}
	
	public static String saveAutoName(String directoryPath, Bitmap image, Activity activity) {
		String filePath = genUniquePath(directoryPath);
		
		if(save(filePath, image, activity))
			return filePath;
		else
			return null;
	}
	
	public static boolean save(String path, Bitmap image, Activity activity) {
		File saveFile = new File(path);
		long now = System.currentTimeMillis();
		
		//TODO どの場所で撮ったものかも保存する?
		ContentValues cv = new ContentValues();
		cv.put(MediaStore.Images.Media.TITLE, saveFile.getName());
		cv.put(MediaStore.Images.Media.DISPLAY_NAME, saveFile.getName());
		cv.put(MediaStore.Images.Media.DATA, path);
		cv.put(MediaStore.Images.Media.DESCRIPTION, "PictDiary");
		cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		cv.put(MediaStore.Images.Media.DATE_MODIFIED, now);
		
		if(saveFile.exists()) {
			//上書き保存の場合はデータベースの情報をいったん消す
			
			String[] proj = {
					MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA,
					MediaStore.Images.Media.DATE_ADDED,
					MediaStore.Images.Media.DATE_TAKEN,
					MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
					MediaStore.Images.Media.BUCKET_ID,
				};
			
			Cursor cursor = activity.managedQuery(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					proj,
					MediaStore.Images.Media.DATA + " = ?",
					new String[] { path },
					null);
			
			if(cursor.getCount() != 0) {
				cursor.moveToFirst();
				
				
				Uri saveUri = ContentUris.appendId(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon(),
						cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media._ID))).build();
				activity.getContentResolver().delete(saveUri, null, null);
				
				//消す前の情報を追加していく
				int addedIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
				if(!cursor.isNull(addedIndex)) {
					cv.put(MediaStore.Images.Media.DATE_ADDED, cursor.getLong(addedIndex));
				}
				
				int takenIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN);
				if(!cursor.isNull(takenIndex)) {
					cv.put(MediaStore.Images.Media.DATE_TAKEN,cursor.getLong(takenIndex));
				}
				
				int bucketIdIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID);
				if(!cursor.isNull(bucketIdIndex)) {
					cv.put(MediaStore.Images.Media.BUCKET_ID, cursor.getString(bucketIdIndex));
				}
				
				int bucketNameIndex = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
				if(!cursor.isNull(bucketNameIndex)) {
					cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, cursor.getString(bucketNameIndex));
				}
			}
		}
		
		if(!cv.containsKey(MediaStore.Images.Media.DATE_ADDED)) {
			cv.put(MediaStore.Images.Media.DATE_ADDED, now);
		}
		if(!cv.containsKey(MediaStore.Images.Media.DATE_TAKEN)) {
			cv.put(MediaStore.Images.Media.DATE_TAKEN, now);
		}
		if(!cv.containsKey(MediaStore.Images.Media.BUCKET_ID)) {
			cv.put(MediaStore.Images.Media.BUCKET_ID, 
					saveFile.getParentFile().toString().toLowerCase().hashCode());
		}
		if(!cv.containsKey(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)) {
			cv.put(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, 
					saveFile.getParentFile().getName().toLowerCase());
		}

		//画像を保存する
		OutputStream out = null;
		try {
			Uri newuri = activity.getContentResolver().insert(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					cv);
			
			out = activity.getContentResolver().openOutputStream(newuri);
			image.compress(CompressFormat.JPEG, 90, out);
			out.flush();
		}catch(IOException e) {
			return false;
			
		}finally {
			if(out != null) {
				try{
					out.close();
				}catch(Exception e) {}
			}
		}
		
		return true;
	}
	
	public static String genThumbnailAndSave(String directoryPath, String originalImagePath) {
		int stride = ThumbStride;
		
		return genThumbnailAndSave(directoryPath, loadImage(originalImagePath, stride, stride));
	}

	public static String genThumbnailAndSave(String directoryPath, Bitmap image) {
		String filePath = genUniquePath(directoryPath);
		
		File directory = new File(directoryPath);
		if(!directory.exists()) {
			directory.mkdirs();
		}
		
		Rect frame = calcThumbnailFrame(image.getWidth(), image.getHeight());
		int stride = ThumbStride;
		Bitmap thumbnail = Bitmap.createBitmap(stride, stride, Config.RGB_565);
		Canvas canvas = new Canvas(thumbnail);
		Paint paint = new Paint();
		paint.setDither(true);
		paint.setFilterBitmap(true);
		canvas.drawBitmap(image, frame, new Rect(0, 0, stride, stride), paint);
		
		//TODO エラー処理のポリシーを考えないと
		OutputStream os = null;
		try{
			os = new FileOutputStream(filePath);
			boolean success = thumbnail.compress(CompressFormat.JPEG, 80, os);
			os.flush();
			
			if(!success)
				return null;
			
		} catch (IOException e) {
			return null;
		}finally {
			if(os != null) {
				try{
					os.close();
				}catch(IOException e) {}
			}
		}
		
		thumbnail.recycle();
		
		return filePath;
	}
	
	private static Rect calcThumbnailFrame(int srcWidth, int srcHeight) {
		if(srcWidth < srcHeight) {
			int x = 0;
			int y = (srcHeight - srcWidth) / 2;
			
			return new Rect(x, y, x + srcWidth-1, y + srcWidth - 1);
		} else {
			int y = 0;
			int x = (srcWidth - srcHeight) / 2;
			
			return new Rect(x, y, x + srcHeight - 1, y + srcWidth - 1);
		}
	}
	
	public static Size fitting(int width, int height, int stride) {

		int retWidth, retHeight;
		if (stride < width || stride < height) {
			int scalingWidth, scalingHeight;
			if (width < height) {
				scalingWidth = (int) (stride * (width / (float) height));
				scalingHeight = (int) (scalingWidth * (height / (float) width));
			} else {
				scalingHeight = (int) (stride * (height / (float) width));
				scalingWidth = (int) (scalingHeight * (width / (float) height));
			}

			retWidth = scalingWidth;
			retHeight = scalingHeight;
		} else {
			retWidth = width;
			retHeight = height;
		}
		
		return new Size(retWidth, retHeight);
	}
		
}
