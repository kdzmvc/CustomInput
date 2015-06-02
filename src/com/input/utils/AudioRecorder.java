package com.input.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.input.utils.TimeUtils;

import android.media.MediaRecorder;
import android.os.Environment;

public class AudioRecorder {

	private MediaRecorder _recorder;
	private String _fileName;
	private String _voicePath = Environment.getExternalStorageDirectory().getPath() + "/uxiang/audio/";
	private boolean _isRecording = false;

	/**
	 * 在这里进行录音准备工作，重置录音文件名等
	 */
	public void ready() {
		File file = new File(_voicePath);
		if (!file.exists()) {
			file.mkdir();
		}
		_fileName = TimeUtils.getCurrentName();
		_recorder = new MediaRecorder();
		_recorder.setOutputFile(_voicePath + _fileName + ".amr");
		_recorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置MediaRecorder的音频源为麦克风
		_recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);// 设置MediaRecorder录制的音频格式
		_recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);// 设置MediaRecorder录制音频的编码为amr
	}


	/**
	 * 开始录音
	 */
	public void start() {
		// TODO Auto-generated method stub
		if (!_isRecording) {
			try {
				_recorder.prepare();
				_recorder.start();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			_isRecording = true;
		}

	}

	/**
	 * 录音结束
	 */
	public void stop() {
		// TODO Auto-generated method stub
		if (_isRecording) {
			_recorder.stop();
			_recorder.release();
			_isRecording = false;
		}

	}

	/**
	 * 录音失败时删除原来的旧文件
	 */
	public void deleteOldFile() {
		File file = new File(_voicePath  + _fileName + ".amr");
		file.delete();
	}
	
	/**
	 * 获取录音音量的大小
	 */
	public double getAmplitude() {
		if (!_isRecording) {
			return 0;
		}
		return _recorder.getMaxAmplitude();
	}

	public String getFileName() {
		return _fileName;
	}
	
	public String getVoicePath() {
		return _voicePath;
	}
}
