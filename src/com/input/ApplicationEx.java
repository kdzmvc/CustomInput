/**
 * 
 */
package com.input;

import com.input.utils.FaceConversionUtil;

import android.app.Application;

public class ApplicationEx extends Application {

	public ApplicationEx() {
	}

	@Override
	public void onCreate() {
		super.onCreate();
		new Thread(new Runnable() {
			@Override
			public void run() {
				FaceConversionUtil.getInstace().getFileText(ApplicationEx.this);
			}
		}).start();
	}
}
