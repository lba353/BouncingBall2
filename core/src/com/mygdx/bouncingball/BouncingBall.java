package com.mygdx.bouncingball;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class BouncingBall extends ApplicationAdapter {
	ShapeRenderer myRenderer;
	BallObject myBall;
	BallObject[] balls;

	int screenWidth, screenHeight;
	int ballRad = 15;
	int numBalls = 30;
	
	@Override
	public void create () {
		myBall = new BallObject();
		balls = new BallObject[numBalls];
		myRenderer = new ShapeRenderer();

		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();

		int ballRows = (int) Math.ceil(Math.sqrt(numBalls));

		for(int i = 0; i < numBalls; i++) {
			balls[i] = new BallObject();

			balls[i].x= (0.5f+i/ballRows) * screenWidth/(ballRows);
			balls[i].y= (0.5f+i% ballRows) * screenHeight/(ballRows);

			float initSpeed = 1f;
			float initAngle = (float) (Math.random()*2*3.14159);
			balls[i].vx = (float) (initSpeed * Math.cos(initAngle));
			balls[i].vy = (float) (initSpeed * Math.sin(initAngle));

			balls[i].redF = (float) Math.random();
			balls[i].greenF = (float) Math.random();
			balls[i].blueF = (float) Math.random();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		myRenderer.begin(ShapeRenderer.ShapeType.Filled);

		for(BallObject myBall: balls){
			myRenderer.setColor(myBall.redF, myBall.greenF, myBall.blueF, 1);
			myRenderer.circle(myBall.x, myBall.y, ballRad);
		}

		myRenderer.end();

		mover();
	}
	
	@Override
	public void dispose () {
		myRenderer.dispose();
	}

	public void mover() {
		for (int i = 0; i < numBalls; i++) {
			balls[i].x += balls[i].vx;
			balls[i].y += balls[i].vy;

			// Hitting a wall
			if (balls[i].x < ballRad)
				balls[i].vx = Math.abs(balls[i].vx);
			if (balls[i].y < ballRad)
				balls[i].vy = Math.abs(balls[i].vy);
			if (balls[i].x > screenWidth - ballRad)
				balls[i].vx = -Math.abs(balls[i].vx);
			if (balls[i].y > screenHeight - ballRad)
				balls[i].vy = -Math.abs(balls[i].vy);

			//Hitting other balls
			for(int j = i+1; j<numBalls; j++){
				float dx = balls[i].x - balls[j].x;
				float dy = balls[i].y - balls[j].y;
				float distance = (float) Math.sqrt(dx*dx+dy*dy);
				if (distance < 2 * ballRad) {
					Vector2 dV = colliderator(i,j);
					balls[i].vx -= dV.x;
					balls[i].vy -= dV.y;
					balls[j].vx += dV.x;
					balls[j].vy += dV.y;
					Vector2 newP = setBall2Rad(i, j );
					balls[i].x = newP.x;
					balls[i].y = newP.y;

					balls[i].redF = (float) Math.random();
					balls[i].greenF = (float) Math.random();
					balls[i].blueF = (float) Math.random();
					balls[j].redF = (float) Math.random();
					balls[j].greenF = (float) Math.random();
					balls[j].blueF = (float) Math.random();
				}
			}
		}
	}

	private Vector2 colliderator(int i, int j){
		Vector2 V1 = new Vector2(balls[i].vx, balls[i].vy);
		Vector2 V2 = new Vector2(balls[j].vx, balls[j].vy);
		Vector2  P1 = new Vector2(balls[i].x, balls[i].y);
		Vector2 P2 = new Vector2(balls[j].x, balls[j].y);

		Vector2 Vdiff = new Vector2(V1.x-V2.x, V1.y-V2.y);// V1 - V2 Vec
		Vector2 Pdiff = new Vector2(P1.x-P2.x, P1.y-P2.y); // P1-P2 Vec
		float dotter = Vdiff.dot(Pdiff);
		float Pdist2 = Pdiff.len2();

		return Pdiff.scl(dotter/(Pdist2));
	}

	private Vector2 setBall2Rad(int i,int j){
		Vector2 ball1 = new Vector2(balls[i].x, balls[i].y);
		Vector2 ball2 = new Vector2(balls[j].x, balls[j].y);

		Vector2 ballDiff = new Vector2(ball1.x-ball2.x, ball1.y-ball2.y);
		ballDiff.setLength(2*ballRad+1);

		return ballDiff.add(ball2);
	}
}
