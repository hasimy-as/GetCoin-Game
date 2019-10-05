package com.getcoin.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;


import java.util.Random;
import java.util.ArrayList;


public class GetCoin extends ApplicationAdapter {

//	Stage stage;
//	Viewport viewport;
//	OrthographicCamera camera;
//	TextureAtlas atlas;

	Skin skin;
	SpriteBatch batch;
	Texture bg;
	Texture[] coinMan;
	int coinManState = 0;
	int staticPause = 0;
	float worldGravity = 0.2f;
	float worldVelocity = 0;
	int coinManY = 0;
	int score = 0;
	int gameState = 0;
	Rectangle coinManRectangle;
	BitmapFont fontScore;
	BitmapFont gameOverSign;

	Random random;
	ArrayList<Integer> coinX = new ArrayList<Integer>();
	ArrayList<Integer> coinY = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangle = new ArrayList<Rectangle>();
	Texture coin;
	int coinCounting;
	ArrayList<Integer> bombX = new ArrayList<Integer>();
	ArrayList<Integer> bombY = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangle = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCounting;
	Texture gameOverCoinMan;

	@Override
	public void create() {
		batch = new SpriteBatch();
		bg = new Texture("bgflappycoin.png");
		coinMan = new Texture[4];

		coinMan[0] = new Texture("frame-1.png");
		coinMan[1] = new Texture("frame-2.png");
		coinMan[2] = new Texture("frame-3.png");
		coinMan[3] = new Texture("frame-4.png");

		coinManY = Gdx.graphics.getHeight() / 2;

		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		random = new Random();

		gameOverCoinMan = new Texture("dizzy-1.png");

		fontScore = new BitmapFont();
		fontScore.setColor(Color.WHITE);
		fontScore.getData().setScale(10);

		gameOverSign = new BitmapFont();
		gameOverSign.setColor(Color.RED);
		gameOverSign.getData().setScale(10);
	}

	public void coinMake() {
		float heightCoin = random.nextFloat() * Gdx.graphics.getHeight();
		coinY.add((int) heightCoin);
		coinX.add(Gdx.graphics.getWidth());
	}

	public void bombMake() {
		float heightBomb = random.nextFloat() * Gdx.graphics.getHeight();
		bombY.add((int) heightBomb);
		bombX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render() {
		batch.begin();
		batch.draw(bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// game state
		if (gameState == 1) {
			// coin render
			if (coinCounting < 100) {
				coinCounting++;
			} else {
				coinCounting = 0;
				coinMake();
			}

			coinRectangle.clear();

			for (int i = 0; i < coinX.size(); i++) {
				batch.draw(coin, coinX.get(i), coinY.get(i));
				coinX.set(i, coinX.get(i) - 4);
				coinRectangle.add(new Rectangle(coinX.get(i), coinY.get(i), coin.getWidth(), coin.getHeight()));
			}

			// bomb render
			if (bombCounting < 250) {
				bombCounting++;
			} else {
				bombCounting = 0;
				bombMake();
			}

			bombRectangle.clear();

			for (int i = 0; i < bombX.size(); i++) {
				batch.draw(bomb, bombX.get(i), bombY.get(i));
				bombX.set(i, bombX.get(i) - 8);
				bombRectangle.add(new Rectangle(bombX.get(i), bombY.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			// positions
			if (Gdx.input.justTouched()) {
				worldVelocity = -10;
			}

			if (staticPause < 8) {
				staticPause++;
			} else {
				staticPause = 0;
				if (coinManState < 3) {
					coinManState++;
				} else {
					coinManState = 0;
				}
			}

			worldVelocity = worldVelocity + worldGravity;
			coinManY -= worldVelocity;

			if (coinManY <= 0) {
				coinManY = 0;
			}
		} else if (gameState == 0) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
			}
		} else if (gameState == 2) {
			if (Gdx.input.justTouched()) {
				gameState = 1;
				score = 0;
				worldVelocity = 0;
				coinManY = Gdx.graphics.getHeight() / 2;
				coinX.clear();
				coinY.clear();
				coinRectangle.clear();
				coinCounting = 0;
				bombX.clear();
				bombY.clear();
				bombRectangle.clear();
				bombCounting = 0;

			}
		}

		if (gameState == 2) {
			batch.draw(gameOverCoinMan, Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth() / 2, coinManY);
		} else {
			batch.draw(coinMan[coinManState], Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth() / 2,
					coinManY);
		}
		coinManRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - coinMan[coinManState].getWidth() / 2, coinManY,
				coinMan[coinManState].getWidth(), coinMan[coinManState].getHeight());

		for (int i = 0; i < coinRectangle.size(); i++) {
			if (Intersector.overlaps(coinManRectangle, coinRectangle.get(i))) {
				score++;

				coinRectangle.remove(i);
				coinX.remove(i);
				coinY.remove(i);
				break;
			}
		}

		for (int i = 0; i < bombRectangle.size(); i++) {
			if (Intersector.overlaps(coinManRectangle, bombRectangle.get(i))) {
				Gdx.app.log("Bomb!", "Collision!!");
				gameState = 2;
				gameOverSign.draw(batch, "Game Over!", 150, 2000);

			}
		}

		fontScore.draw(batch, String.valueOf(score), 100, 200);

		batch.end();
	}

}
