package com.input.adapter;

import java.util.List;

import com.input.moder.ChatEmoji;
import com.kdz.input.R;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

/**
 * 
 ****************************************** 
 * @文件名称 : FaceAdapter.java
 * @文件描述 : 表情填充器
 ****************************************** 
 */
public class FaceAdapter extends BaseAdapter {

	private List<ChatEmoji> _data;

	private LayoutInflater _inflater;

	private int _size = 0;

	public FaceAdapter(Context context, List<ChatEmoji> list) {
		this._inflater = LayoutInflater.from(context);
		this._data = list;
		this._size = list.size();
	}

	@Override
	public int getCount() {
		return this._size;
	}

	@Override
	public Object getItem(int position) {
		return _data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatEmoji emoji = _data.get(position);
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = _inflater.inflate(R.layout.item_face, null);
			viewHolder.iv_face = (ImageView) convertView
					.findViewById(R.id.item_iv_face);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (emoji.getId() == R.drawable.face_del_icon) {
			convertView.setBackgroundDrawable(null);
			viewHolder.iv_face.setImageResource(emoji.getId());
		} else if (TextUtils.isEmpty(emoji.getCharacter())) {
			convertView.setBackgroundDrawable(null);
			viewHolder.iv_face.setImageDrawable(null);
		} else {
			viewHolder.iv_face.setTag(emoji);
			viewHolder.iv_face.setImageResource(emoji.getId());
		}

		return convertView;
	}

	class ViewHolder {

		public ImageView iv_face;
	}
}