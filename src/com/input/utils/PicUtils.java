package com.input.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class PicUtils {

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static void saveToSDcard(Bitmap bm, String path) {

		File file = new File(path);
		if (!file.exists()) {
			file.mkdir();
		}
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bm.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public static void savePicToSdcard(Bitmap bitmap, String path, String fileName) {
		String filePath = "";
		if (bitmap == null) {
			return ;
		} else {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}
			filePath = path + fileName;
			File destFile = new File(filePath);
			OutputStream os = null;
			try {
				os = new FileOutputStream(destFile);
				bitmap.compress(CompressFormat.JPEG, 100, os);
				os.flush();
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	// 缩放图片
		public static Bitmap zoomImg(Bitmap bm, int max, int angle) {
			try {
				// 获得图片的宽高
				int width = bm.getWidth();
				int height = bm.getHeight();
				// 计算缩放比例
				float scaleWidth = ((float) max) / width;
				float scaleHeight = ((float) max) / height;
				float finalScale;
				if (scaleWidth > scaleHeight) {
					finalScale = scaleHeight;
				} else {
					finalScale = scaleHeight;
				}
				// 取得想要缩放的matrix参数
				Matrix matrix = new Matrix();
				if (angle != 0){
					matrix.postRotate(angle);//是否需要进行翻转
				}
				matrix.postScale(finalScale, finalScale);
				// 得到新的图片
				return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
			} catch (OutOfMemoryError err) {
			}
			return null;
		}
		
		public static Bitmap deFileBitmap(Context context, String path,
				String fileName) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			//创建位图
			opts.inPreferredConfig = Config.ALPHA_8;
			// inPurgeable为True的话表示,使用BitmapFactory创建的Bitmap,用于存储Pixel的内存空间在系统内存不足时可以被回收 
			opts.inPurgeable = true;
			//为true 位图能够共享一个指向数据源的引用，或者是进行一份拷贝
			opts.inInputShareable = true;
			InputStream is = null;
			try {
				is = new FileInputStream(path + fileName);
				return BitmapFactory.decodeStream(is, null, opts);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (OutOfMemoryError err) {
			}
			return null;
		}
		
	public static Bitmap createImageThumbnail(String filePath, int length) {
		Bitmap bitmap = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		BitmapFactory.decodeFile(filePath, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, length*length);
		opts.inJustDecodeBounds = false;
		try {
			bitmap = BitmapFactory.decodeFile(filePath, opts);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return bitmap;
	}

		public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		    int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		    int roundedSize;
		    if (initialSize <= 8) {
		        roundedSize = 1;
		        while (roundedSize < initialSize) {
		            roundedSize <<= 1;
		        }
		    } else {
		        roundedSize = (initialSize + 7) / 8 * 8;
		    }
		    return roundedSize;
		}

		private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
		    double w = options.outWidth;
		    double h = options.outHeight;
		    int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		    int upperBound = (minSideLength == -1) ? 128 :(int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		    if (upperBound < lowerBound) {
		        // return the larger one when there is no overlapping zone.
		        return lowerBound;
		    }
		    if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
		        return 1;
		    } else if (minSideLength == -1) {
		        return lowerBound;
		    } else {
		        return upperBound;
		    }
		}
}
