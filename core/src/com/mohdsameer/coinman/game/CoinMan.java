package com.mohdsameer.coinman.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;

	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<>();
	Texture coin;
	int coinCount = 0;

	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<>();
	Texture bomb;
	Texture dizzy;
	int bombCount = 0;

	Random random;
	int score = 0;
	BitmapFont font;
	int gameState = 0;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");

		manY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		dizzy = new Texture("dizzy-1.png");

		random = new Random();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(8);

	}
	public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombYs.add((int)height);
		bombXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 1){
			//GAME IS LIVE

			//bombs
			if(bombCount < 300){
				bombCount++;
			}else{
				bombCount = 0;
				makeBomb();
			}
			bombRectangle.clear();
			for(int i=0; i < bombXs.size(); i++){
				batch.draw(bomb, bombXs.get(i), bombYs.get(i));
				bombXs.set(i, bombXs.get(i) - 8);
				bombRectangle.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//Coins
			if(coinCount <100){
				coinCount++;
			}else{
				coinCount = 0;
				makeCoin();
			}
			coinRectangle.clear();
			for(int i=0; i < coinXs.size(); i++){
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 4);
				coinRectangle.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			if(Gdx.input.justTouched()){
				velocity = -10;
			}

			if(pause < 5){
				pause ++;
			}else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}

			velocity += gravity;
			manY -= velocity;

			if(manY <= 0){
				manY = 0;
			}

		}else if(gameState == 0){
			//WAITING TO START
			font.draw(batch, "Tap to start", 370, 200);
			font.getData().setScale(5);

			if(Gdx.input.justTouched()){
				gameState = 1;
			}
		} else if (gameState == 2) {
			//GAME OVER
			font.draw(batch, "Tap to play again", 280, 200);
//			font.getData().setScale(5);

			if(Gdx.input.justTouched()){
				gameState =  1;
				manY = Gdx.graphics.getHeight()/2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinRectangle.clear();
				coinCount = 0;
				bombXs.clear();
				bombYs.clear();
				bombRectangle.clear();
				bombCount = 0;


			}
		}

		if(gameState == 2){
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}else{
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}

		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for(int i=0; i< coinRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, coinRectangle.get(i))){
				score++;
				coinRectangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
			}
		}

		for(int i=0; i< bombRectangle.size(); i++) {
			if (Intersector.overlaps(manRectangle, bombRectangle.get(i))){
				Gdx.app.log("Bomb", "Collision");
				gameState = 2;
			}
		}

		font.draw(batch, String.valueOf(score), 930, 1750);

		batch.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();

	}
}
