package com.pearson.api.samples.catmatch;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class CardImageAdapter extends BaseAdapter {
	private Context context;
	private Deck deck;

	public CardImageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return 12;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {
			imageView = new ImageView(context);
		} else {
			imageView = (ImageView) convertView;
		}
		int w = (int) (parent.getWidth() / 3) - 12;
		int h = (int) (parent.getHeight() / 4) - 12;
		imageView.setLayoutParams(new GridView.LayoutParams(w, h));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		imageView.setPadding(10, 10, 10, 10);
		imageView.setImageBitmap(deck.getBitmap(position));
		return imageView;
	}

	public void refresh(View view, int position, ViewGroup parent) {
		view = getView(position, view, parent);
	}

	public void setDeck(Deck deck) {
		this.deck = deck;
	}

}
