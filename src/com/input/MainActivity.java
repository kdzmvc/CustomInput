package com.input;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import com.input.utils.PicUtils;
import com.input.utils.TimeUtils;
import com.input.view.MessageInputLayout;
import com.input.view.MessageInputLayout.OnOperationListener;
import com.kdz.input.R;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MainActivity extends Activity {
	private static final String _picpath = Environment.getExternalStorageDirectory() + "/androidInput/pic/";
	private static String _picMaxName = "";
	private static String _picMinName = "";
	private MessageInputLayout _inputLayout;
	private Bitmap _maxbm = null;
	private Bitmap _minbm = null;
	private static final int _resultPic = 88;
	private static final int _resultCamera = 99;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_im);
		_inputLayout = (MessageInputLayout) findViewById(R.id.messageInputLayout);
		_inputLayout.setOnOperationListener(new OnOperationListener() {
			@Override
			//选择图库处理请求
			public void selectPic() {
				Intent intent = new Intent(Intent.ACTION_GET_CONTENT);   
				 intent.setType("image/*");  
				 startActivityForResult(intent, _resultPic);  
			}
			//选择摄像头处理请求
			@Override
			public void selectCamera() {
				startCamera(_picpath);
			}
			@Override
			public void selectLocation() {
				
			}
			//发送消息处理
			@Override
			public void sendTextMessage(String content) {
				
			}

			@Override
			public void sendVocieMessage(String patch, int duration) {
				
			}
		});
	}

	public void desBitmap(Bitmap bitmap){  
        // 销毁时调用  
        if(bitmap != null && !bitmap.isRecycled()){  
        	bitmap.recycle();  
        	bitmap = null;  
        } 
        System.gc();
    }
	
	private void startCamera(String spath) {
		File file = new File(spath);
		if (!file.exists()) {
			file.mkdirs();
		}
		_picMaxName = TimeUtils.getCurrentName();
		_picMinName = TimeUtils.getCurrentName();
		File picMaxFile = new File(spath + _picMaxName);
		if (!picMaxFile.exists()) {
			file.mkdir();
		}
		Intent myintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		myintent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picMaxFile));
		myintent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(myintent, _resultCamera);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case _resultCamera:
			//从流中读取bitmap,为了避免oom,不调用Bitmap.createBitmap(),BitmapFactory.decodeStream是底层调用Jni
			_maxbm = PicUtils.deFileBitmap(MainActivity.this, _picpath,
					_picMaxName);
			int degree = PicUtils.readPictureDegree(new File(_picpath+_picMaxName).getAbsolutePath());
			if (_maxbm != null) {
				//大图显示不超过960*960,小图不超过160*160
				if (_maxbm.getWidth() > 960 || _maxbm.getHeight() > 960) {
					//机型适配统一处理翻转并缩放
					_maxbm = PicUtils.zoomImg(_maxbm, 960,degree);
					_minbm = PicUtils.zoomImg(_maxbm, 160,degree);
				} else if (_maxbm.getWidth() > 160 || _maxbm.getHeight() > 160) {
					_minbm = PicUtils.zoomImg(_maxbm, 160,degree);
					
				} else {
					_minbm = _maxbm;
				}
				if (_minbm != null && _maxbm != null) {
					//把图片都保存到sd卡
					PicUtils.savePicToSdcard(_maxbm, _picpath, _picMaxName);
					PicUtils.savePicToSdcard(_minbm, _picpath, _picMinName);
					desBitmap(_maxbm);
				}
			}
			break;
		case _resultPic:
			Bitmap bm = null;
			ContentResolver resolver = getContentResolver();
			Uri uri = data.getData();
			if (uri != null) {
				if (uri != null) {
					try {
						bm = MediaStore.Images.Media.getBitmap(resolver, uri);
						if (bm.getWidth() > 960 || bm.getHeight() > 960) {
							_maxbm = PicUtils.zoomImg(bm, 960,0);
							_minbm = PicUtils.zoomImg(bm, 160,0);
						} else if (bm.getWidth() > 160 || bm.getHeight() > 160) {
							_maxbm = bm;
							_minbm = PicUtils.zoomImg(_maxbm, 160,0);
						} else {
							_maxbm = bm;
							_minbm = bm;
						}
						if (_minbm != null && _maxbm != null) {
							_picMaxName = TimeUtils.getCurrentName();
							_picMinName = TimeUtils.getCurrentName();
							PicUtils.savePicToSdcard(_maxbm, _picpath,
									_picMaxName);
							PicUtils.savePicToSdcard(_minbm, _picpath,
									_picMinName);
							desBitmap(bm);
							desBitmap(_maxbm);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			break;
		default:
			break;
		}
	}
}
