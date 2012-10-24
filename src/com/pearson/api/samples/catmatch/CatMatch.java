package com.pearson.api.samples.catmatch;

import java.util.List;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.google.resting.Resting;
import com.google.resting.component.RequestParams;
import com.google.resting.component.impl.BasicRequestParams;

/**
 * @author dgem A simple memory matching game using the dkimages.com API from
 *         Pearson. See developer.pearson.com for details and sign up for a key.
 */
public class CatMatch extends Activity {
	private static final int WELCOME = 10;
	private static final int LOADING = 20;
	private static final int PLAYING = 30;
	private static final int COMPLETED = 40;

	private static final int NUM_PAIRS = 6;
	private static final String API_URL = "https://api.pearson.com/dk/v1/images";
	private static final String API_KEY = "";

	private int currentGameState = WELCOME;
	private int previouslyTurnedPosition = -1;
	private int matchedCards;
	private Deck deck;
	
	private OnClickListener clickListener;
	private OnItemClickListener cardClickListener;
	private CardImageAdapter cardImageAdapter;
	private CatImagesTask imageTask;
	private ViewGroup gridView;
	

	/**
	 * Use the http://code.google.com/p/resting/ client to access api. Which
	 * saves coding to the Apache HTTPClient, which is nice.
	 */
	private class CatImagesTask extends AsyncTask<Void, Void, List<Card>> {

		public List<Card> result = null;

		@Override
		protected List<Card> doInBackground(Void... ignored) {
			RequestParams params = new BasicRequestParams();
			params.add("apikey", API_KEY);
			params.add("keywords", "feline");
			params.add("caption", "cat");
			params.add("limit", "100");
			List<Card> images = Resting.getByJSON(API_URL, 443, params, Card.class, "images");
			return images;
		}

		@Override
		protected void onPostExecute(List<Card> response) {
			result = response;
			playGame();
		}
	}
	
	private class TwoCardsTurnedTask extends AsyncTask<Card, Void, Card[]>{

		@Override
		protected Card[] doInBackground(Card... cards) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// Lets's hope not, but we've got no choice !
				e.printStackTrace();
			}
			return cards;
		}
		
		@Override
		protected void onPostExecute(Card[] cards) {
			Card cardOne = cards[0];
			Card cardTwo = cards[1];
			View cardOneView = gridView.getChildAt(cardOne.position);
			View cardTwoView = gridView.getChildAt(cardTwo.position);
			if (cardOne.id.equals(cardTwo.id)) {
				matchedCards ++;
				cardTwo.hide();
				cardOne.hide();
				cardOneView.setOnClickListener(null);
				cardTwoView.setOnClickListener(null);
				if (matchedCards == NUM_PAIRS) {
					currentGameState = COMPLETED;
					refreshScreen();				
				}
			} else {
				cardTwo.turn();
				cardOne.turn();
			}
			cardImageAdapter.refresh(cardTwoView, cardTwo.position, gridView);
			cardImageAdapter.refresh(cardOneView, cardOne.position, gridView);
		}
	}

	private class ButtonListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			switch (currentGameState) {
			case WELCOME:
				currentGameState = LOADING;
				refreshScreen();
				playGame();
				break;
			case COMPLETED:
				currentGameState = LOADING;
				refreshScreen();
				playGame();
				break;
			default:
				break;
			}
		}
	}

	private class CardClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				cardClicked(view, position, parent);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			int savedState = savedInstanceState.getInt("STATE");
			if (savedState >= WELCOME && savedState <= COMPLETED)
				currentGameState = savedState;
		}
		if (clickListener == null)
			clickListener = new ButtonListener();
		if (cardClickListener == null)
			cardClickListener = new CardClickListener();
		if (cardImageAdapter == null)
			cardImageAdapter = new CardImageAdapter(getApplicationContext());
		if (imageTask == null)
			imageTask = new CatImagesTask();
		if (deck == null)
			deck = new Deck(getResources());
		refreshScreen();
	}

	public void playGame() {
		if (imageTask.result == null)
			imageTask.execute();
		else {
			deck.selectFrom(imageTask.result, NUM_PAIRS);
			deck.shuffle();
			cardImageAdapter.setDeck(deck);
			currentGameState = PLAYING;
			refreshScreen();
		}
	}

	public void cardClicked(View view, int position, AdapterView<?> parent) {
		Card currCard = deck.getCard(position);
		currCard.turn();
		cardImageAdapter.refresh(view, position, parent);
		if (previouslyTurnedPosition > -1){
			Card prevCard = deck.getCard(previouslyTurnedPosition);
			previouslyTurnedPosition = -1;
			if (!currCard.equals(prevCard)) {
				if (gridView != parent)
					gridView = parent;
				new TwoCardsTurnedTask().execute(currCard, prevCard);
			}
		} else {
			previouslyTurnedPosition = position;
		}
	}

	private void refreshScreen() {
		switch (currentGameState) {
		case WELCOME:
			setContentView(R.layout.messages);
			((TextView) findViewById(R.id.message)).setText(R.string.welcome_message);
			((Button) findViewById(R.id.play_button)).setText(R.string.welcome_play_button);
			((Button) findViewById(R.id.play_button)).setOnClickListener(clickListener);
			break;
		case LOADING:
			setContentView(R.layout.messages);
			((TextView) findViewById(R.id.message)).setText(R.string.loading_message);
			((Button) findViewById(R.id.play_button)).setVisibility(View.INVISIBLE);
			break;
		case PLAYING:
			setContentView(R.layout.board);
			GridView board = (GridView) findViewById(R.id.card_view);
			board.setAdapter(cardImageAdapter);
			board.setOnItemClickListener(cardClickListener);
			break;
		case COMPLETED:
			setContentView(R.layout.messages);
			((TextView) findViewById(R.id.message)).setText(R.string.completed_message);
			((Button) findViewById(R.id.play_button)).setText(R.string.completed_play_button);
			((Button) findViewById(R.id.play_button)).setOnClickListener(clickListener);
			matchedCards = 0;
			previouslyTurnedPosition = -1;
			break;
		default:
			System.out.println("Aaaagggghhhh we are out of states");
		}
	}
}
