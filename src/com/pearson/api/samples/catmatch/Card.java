package com.pearson.api.samples.catmatch;

public class Card implements Cloneable, Comparable<Card>{
	
	public String id;
	public String filename;
	public String url;
	public String caption;
	public String keywords;
	public boolean faceUp = false;
	public boolean visible = true;
	public int position;
	
	@Override
	public Card clone() throws CloneNotSupportedException {
		 Card clone = (Card) super.clone();
		 return clone;
	}
	
	@Override
	public int compareTo(Card another) {
		if (position == another.position) return 0;
		return (position < another.position ? -1: 1); 
	}
	
	public void turn(){
		faceUp = !faceUp;
	}
	
	public void hide(){
		visible = false;
	}
}
