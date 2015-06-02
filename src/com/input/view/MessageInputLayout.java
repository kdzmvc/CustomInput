package com.input.view;

import java.util.ArrayList;
import java.util.List;
import com.input.utils.AudioRecorder;
import com.input.view.RecordButton;
import com.input.view.MessageInputLayout.OnCorpusSelectedListener;
import com.input.view.MessageInputLayout.OnOperationListener;
import com.input.view.RecordButton.RecordListener;
import com.input.adapter.FaceAdapter;
import com.input.adapter.ViewPagerAdapter;
import com.input.moder.ChatEmoji;
import com.input.utils.FaceConversionUtil;
import com.kdz.input.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @author kongdezhi
 *
 */
public class MessageInputLayout extends RelativeLayout implements View.OnClickListener,OnItemClickListener{
	private ImageView _iv_face, _iv_more, _iv_voice;
	private Button _btn_send;
	private RecordButton _btn_voice;
	private RelativeLayout _bottomHideLayout;
	private LinearLayout _moreLayout, _faceLayout;
	private LinearLayout _edit_layout;
	private LinearLayout _ll_pic, _ll_camera, _ll_location;
	private Context context;
	private EditText _edit;
	private OnOperationListener _oListener;
	/** 表情页的监听事件 */
	private OnCorpusSelectedListener _mListener;

	/** 显示表情页的viewpager */
	private ViewPager _vp_face;

	/** 表情页界面集合 */
	private ArrayList<View> _pageViews;

	/** 游标显示布局 */
	private LinearLayout _layout_point;

	/** 游标点集合 */
	private ArrayList<ImageView> _pointViews;

	/** 表情集合 */
	private List<List<ChatEmoji>> _emojis;

	/** 表情数据填充器 */
	private List<FaceAdapter> _faceAdapters;

	/** 当前表情页 */
	private int current = 0;

	public MessageInputLayout(Context context) {
		super(context);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_im_input, this);
	}

	public MessageInputLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_im_input, this);
	}

	public MessageInputLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		LayoutInflater.from(context).inflate(R.layout.view_im_input, this);
	}

	public void setOnCorpusSelectedListener(OnCorpusSelectedListener listener) {
		_mListener = listener;
	}

	/**
	 * 表情选择监听
	 * 
	 */
	public interface OnCorpusSelectedListener {
		void onCorpusSelected(ChatEmoji emoji);

		void onCorpusDeleted();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		_emojis = FaceConversionUtil.getInstace().emojiLists;
		onCreate();
	}

	private void onCreate() {
		initView();
		initViewPager();
		initPoint();
		initData();
	}

	private void initView() {
		_edit = (EditText) findViewById(R.id._edit);
		_btn_send = (Button) findViewById(R.id.btn_send);
		_iv_face = (ImageView) findViewById(R.id.iv_face);
		_iv_more = (ImageView) findViewById(R.id.iv_more);
		_iv_voice = (ImageView) findViewById(R.id.iv_voice);
		_btn_voice = (RecordButton) findViewById(R.id.btn_voice);
		_bottomHideLayout = (RelativeLayout) findViewById(R.id.bottomHideLayout);
		_edit_layout = (LinearLayout) findViewById(R.id.edit_layout);
		_ll_pic = (LinearLayout) findViewById(R.id.ll_pic);
		_ll_camera = (LinearLayout) findViewById(R.id.ll_camera);
		_ll_location = (LinearLayout) findViewById(R.id.ll_location);
		_moreLayout = (LinearLayout) findViewById(R.id.moreLayout);
		_faceLayout = (LinearLayout) findViewById(R.id.faceLayout);

		_layout_point = (LinearLayout) findViewById(R.id.iv_image);
		_vp_face = (ViewPager) findViewById(R.id.faceCategroyViewPager);
		_iv_voice.setOnClickListener(this);
		_iv_face.setOnClickListener(this);
		_edit.setOnClickListener(this);
		_iv_more.setOnClickListener(this);
		_ll_pic.setOnClickListener(this);
		_ll_camera.setOnClickListener(this);
		_ll_location.setOnClickListener(this);
		_btn_send.setOnClickListener(this);

		_edit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (hasFocus) {
					hideFaceLayout();
				}
				// showKeyboard(context);
			}
		});

		_edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s != null && !"".equals(s.toString().trim())) {
					_iv_more.setVisibility(View.GONE);
					_btn_send.setEnabled(true);
					_btn_send.setVisibility(View.VISIBLE);
				} else {
					_iv_more.setVisibility(View.VISIBLE);
					if (_iv_more.getVisibility() == View.VISIBLE) {
						_btn_send.setVisibility(View.GONE);
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		_btn_voice.setAudioRecord(new AudioRecorder());
		_btn_voice.setRecordListener(new RecordListener() {

			@Override
			public void sendVoice(String content, int duration) {
				//抽象方法回调录音发送
				_oListener.sendVocieMessage(content, duration);
			}
		});
	}

	/**
	 * 初始化显示表情的viewpager
	 */
	private void initViewPager() {
		_pageViews = new ArrayList<View>();
		// 左侧添加空页
		View nullView1 = new View(context);
		// 设置透明背景
		nullView1.setBackgroundColor(Color.TRANSPARENT);
		_pageViews.add(nullView1);
		// 中间添加表情页
		_faceAdapters = new ArrayList<FaceAdapter>();
		for (int i = 0; i < _emojis.size(); i++) {
			GridView view = new GridView(context);
			FaceAdapter adapter = new FaceAdapter(context, _emojis.get(i));
			view.setAdapter(adapter);
			_faceAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			_pageViews.add(view);
		}

		// 右侧添加空页面
		View nullView2 = new View(context);
		// 设置透明背景
		nullView2.setBackgroundColor(Color.TRANSPARENT);
		_pageViews.add(nullView2);
	}

	public float getRawSize(int unit, float value) {
		Resources res = this.getResources();
		return TypedValue.applyDimension(unit, value, res.getDisplayMetrics());
	}

	/**
	 * 初始化游标
	 */
	private void initPoint() {
		_pointViews = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < _pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setBackgroundResource(R.drawable.iv_point_normal);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			layoutParams.leftMargin = (int) getRawSize(
					TypedValue.COMPLEX_UNIT_DIP, 5);
			layoutParams.rightMargin = (int) getRawSize(
					TypedValue.COMPLEX_UNIT_DIP, 5);
			layoutParams.width = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,
					8);
			layoutParams.height = (int) getRawSize(TypedValue.COMPLEX_UNIT_DIP,
					8);
			_layout_point.addView(imageView, layoutParams);
			if (i == 0 || i == _pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setBackgroundResource(R.drawable.iv_point_focus);
			}
			_pointViews.add(imageView);

		}
	}

	/**
	 * 填充数据
	 */
	private void initData() {
		_vp_face.setAdapter(new ViewPagerAdapter(_pageViews));

		_vp_face.setCurrentItem(1);
		current = 0;
		_vp_face.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				current = arg0 - 1;
				// 描绘分页点
				drawPoint(arg0);
				// 如果是第一屏或者是最后一屏禁止滑动，其实这里实现的是如果滑动的是第一屏则跳转至第二屏，如果是最后一屏则跳转到倒数第二屏.
				if (arg0 == _pointViews.size() - 1 || arg0 == 0) {
					if (arg0 == 0) {
						_vp_face.setCurrentItem(arg0 + 1);// 第二屏 会再次实现该回调方法实现跳转.
						_pointViews.get(1).setBackgroundResource(
								R.drawable.iv_point_focus);
					} else {
						_vp_face.setCurrentItem(arg0 - 1);// 倒数第二屏
						_pointViews.get(arg0 - 1).setBackgroundResource(
								R.drawable.iv_point_focus);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	/**
	 * 绘制游标背景
	 */
	public void drawPoint(int index) {
		for (int i = 1; i < _pointViews.size(); i++) {
			if (index == i) {
				_pointViews.get(i).setBackgroundResource(
						R.drawable.iv_point_focus);
			} else {
				_pointViews.get(i).setBackgroundResource(
						R.drawable.iv_point_normal);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ChatEmoji emoji = (ChatEmoji) _faceAdapters.get(current).getItem(
				position);
		if (emoji.getId() == R.drawable.face_del_icon) {
			int selection = _edit.getSelectionStart();
			String text = _edit.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					_edit.getText().delete(start, end);
					return;
				}
				_edit.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			if (_mListener != null)
				_mListener.onCorpusSelected(emoji);
			SpannableString spannableString = FaceConversionUtil.getInstace()
					.addFace(getContext(), emoji.getId(), emoji.getCharacter());
			_edit.append(spannableString);
		}

	}

	public void showFaceLayout() {
		hideKeyboard(this.context);

		postDelayed(new Runnable() {
			@Override
			public void run() {
				_moreLayout.setVisibility(View.GONE);
				_faceLayout.setVisibility(View.VISIBLE);
				_bottomHideLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
	}

	public void showMoreLayout() {
		hideKeyboard(this.context);

		postDelayed(new Runnable() {
			@Override
			public void run() {
				_moreLayout.setVisibility(View.VISIBLE);
				_faceLayout.setVisibility(View.GONE);
				_bottomHideLayout.setVisibility(View.VISIBLE);
			}
		}, 50);
	}

	public void hideFaceLayout() {
		_moreLayout.setVisibility(View.GONE);
		_faceLayout.setVisibility(View.GONE);
		_bottomHideLayout.setVisibility(View.GONE);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param activity
	 */
	public static void hideKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			// View view = activity.getWindow().peekDecorView();
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive() && activity.getCurrentFocus() != null) {
				imm.hideSoftInputFromWindow(activity.getCurrentFocus()
						.getWindowToken(), 0);
			}
			// imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
		}
	}

	/**
	 * 显示软键盘
	 * 
	 * @param activity
	 */
	public static void showKeyboard(Context context) {
		Activity activity = (Activity) context;
		if (activity != null) {
			// 获取输入控制管理器服务
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInputFromInputMethod(activity.getCurrentFocus()
					.getWindowToken(), 0);
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_voice:
			if (_edit_layout.getVisibility() == VISIBLE) {
				hideFaceLayout();
				hideKeyboard(context);
				_iv_voice.setImageResource(R.drawable.rc_message_bar_keyboard);
				_edit_layout.setVisibility(View.GONE);
				_btn_voice.setVisibility(View.VISIBLE);
				if (_btn_send.getVisibility() == VISIBLE) {
					_btn_send.setVisibility(View.GONE);
					_iv_more.setVisibility(View.VISIBLE);
				}
			} else if (_edit_layout.getVisibility() == GONE) {
				_iv_face.setBackgroundResource(R.drawable.rc_smiley_normal);
				_iv_voice
						.setImageResource(R.drawable.rc_message_bar_vioce_icon);
				_edit_layout.setVisibility(View.VISIBLE);
				_btn_voice.setVisibility(View.GONE);
				if (_edit.getText().length() > 0) {
					_iv_more.setVisibility(View.GONE);
					_btn_send.setVisibility(View.VISIBLE);
				} else {
					_iv_more.setVisibility(View.VISIBLE);
					_btn_send.setVisibility(View.GONE);
				}
				showKeyboard(context);
				_edit.requestFocus();
				_edit.setSelection(_edit.getText().length());
			}
			break;
		case R.id.iv_face:
			if (_faceLayout.getVisibility() == VISIBLE) {
				_iv_face.setBackgroundResource(R.drawable.rc_smiley_normal);
				hideFaceLayout();
				showKeyboard(context);
			} else {
				_iv_face.setBackgroundResource(R.drawable.rc_smiley_hover);
				showFaceLayout();
			}
			break;
		case R.id._edit:
			hideFaceLayout();
			break;
		case R.id.iv_more:
			_iv_face.setBackgroundResource(R.drawable.rc_smiley_normal);
			if (_moreLayout.getVisibility() == VISIBLE) {
				hideFaceLayout();
				// showKeyboard(context);
			} else {
				if (_btn_voice.getVisibility() == VISIBLE) {
					_iv_voice
							.setImageResource(R.drawable.rc_message_bar_vioce_icon);
					_btn_voice.setVisibility(View.GONE);
					_edit_layout.setVisibility(View.VISIBLE);
				}
				showMoreLayout();
			}
			break;
		case R.id.ll_pic:
			_oListener.selectPic();
			break;
		case R.id.ll_camera:
			_oListener.selectCamera();
			break;
		case R.id.ll_location:
			_oListener.selectLocation();
			break;
		case R.id.btn_send:
			if (_edit.getText() != null && _edit.getText().toString() != null) {
				_oListener.sendTextMessage(_edit.getText().toString());
				_edit.setText("");
			}
			break;
		}
	}

	public interface OnOperationListener {
		//选择图库
		public void selectPic();
		//调用系统拍照
		public void selectCamera();
		//当前地理位置
		public void selectLocation();
		//发送文本类消息
		public void sendTextMessage(String content);
		//发送语音消息
		public void sendVocieMessage(String patch, int duration);
	}

	public void setOnOperationListener(OnOperationListener listener) {
		_oListener = listener;
	}

}
