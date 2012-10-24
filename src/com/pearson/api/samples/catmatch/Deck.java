package com.pearson.api.samples.catmatch;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

public class Deck {
	
	private Card[] cards;
	private HashMap<String, Bitmap> imageCache = new HashMap<String, Bitmap>();

	public Deck(Resources res){
		super();
		imageCache.put("card_back", BitmapFactory.decodeResource(res, R.drawable.card_back));
	}
	
	public class DownloadImage extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... args) {
			// args[0] is the URL, args[1] is the id
			return downloadImage(args[0], args[1]);
		}

		private Void downloadImage(String urlString, String id) {
			URL url;
			InputStream in;
			BufferedInputStream buffer;
			try {
				if (urlString.contains("?"))
					urlString = urlString + "&size=qvga";
				else
					urlString = urlString + "?size=qvga";
				url = new URL(urlString);
				in = url.openStream();
				buffer = new BufferedInputStream(in);
				Bitmap bitMap = BitmapFactory.decodeStream(buffer);
				if (in != null) {
					in.close();
				}
				if (buffer != null) {
					buffer.close();
				}
				imageCache.put(id, bitMap);
				Log.v("CatMatch", "Downloaded " + urlString + " for " + id);
			} catch (Exception e) {
				System.out.println("Darn, couldn't download the image due to " + e);
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public void selectFrom(List<Card> from, int numPairs){
		ArrayList<Card> selected = new ArrayList<Card>();
		while (selected.size() < numPairs *2) {
			int idx = (int) Math.floor(Math.random() * from.size());
			if (!selected.contains(from.get(idx))) {
				Card card = from.get(idx);
				try {
					selected.add(card.clone());
					selected.add(card.clone());
					if (!imageCache.containsKey(card.id)){
						new DownloadImage().execute(card.url, card.id);
					}
				} catch (CloneNotSupportedException e) {
					// sometimes ..it happens, even if it shouldn't !
					e.printStackTrace();
				}
			}
		}
		cards = selected.toArray(new Card[selected.size()]);
	}
	
	public void shuffle(){
		for (int i = 0; i < cards.length; i++) {
			cards[i].position = (int) Math.floor(Math.random()*1000);
		}
		Arrays.sort(cards);
		for (int i = 0; i < cards.length; i++) {
			cards[i].position = i;
		}
	}
	
	public Bitmap getBitmap(int position) {
		Card card = cards[position];
		Bitmap result;
		if (!card.visible) result = null;
		else if (!card.faceUp) result = imageCache.get("card_back"); 
		else result = imageCache.get(cards[position].id); 
		return result;
	}
	
	public Card getCard(int position) {
		if (cards.length > position) return cards[position];
		else return null;
	}
}
