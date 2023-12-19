package com.snake2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;

// import com.snake2d.sound.SoundManger;

@SuppressWarnings("serial")
public class BoardPanel extends JPanel implements ActionListener {

	/*Buat GameBoard yg baru*/

	private Snake snake;
	private Bakso snakeFood;
    private Pemburu hunter; 
    
	private InputManger inputManager;

	private Timer gameThread;
	private Timer timerThread;

	private boolean isGameOver = false;

	private int timer = 0;
	private int playerScore = 0;
	private int teleportChance = 1;
	
	private JButton teleport;

	// private String soundFilePath = "start.wav";

	public BoardPanel(int level) {

		setBackground(Color.BLACK);
		setFocusable(true);

		snake = new Snake();
		snakeFood = new Bakso();
		hunter = new Pemburu();

		inputManager = new InputManger(this);

		gameThread = new Timer(getDelay(level), this);

		timerThread = new Timer(1000, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (isGameOver()) {
					stopGame();
				}

				timer++;
			}
		});
		
		addKeyListener(inputManager);

	}

	private int getDelay(int level) {

		int delay = 0;

		if (level == 1) {
			delay = 140;
		} 
		else if (level == 2) {
			delay = 70;

		} 
		else if (level == 3) {
			delay = 40;

		}
		return delay;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		doDrawing(g);
	}

	public void doDrawing(Graphics g) {

		Graphics2D g2 = (Graphics2D) g;

		if (isGameRunning()) {

			snake.move();

			checkCollision();

			DrawSnakeFood(g2);

		}

		DrawStatusbar(g2);

		DrawBoundry(g2);

		DrawSnake(g2);
		
		DrawChasingObject(g2); 

	}

	public void DrawBoundry(Graphics2D g2) {
		for (int i = 0; i < 17; i++) {
			Rectangle2D.Double rect = new Rectangle2D.Double(227.0 - i,
					127.0 - i, 624, 480);

			g2.setColor(Color.YELLOW);

			g2.draw(rect);

		}
	}

	public void DrawSnake(Graphics2D g2) {

		for (int i = 0; i < snake.getSnakeBody().size(); i++) {

			if (i == 0) {
				g2.setColor(Color.RED);
				g2.fill(snake.getSnakeBody().get(i));

			} else {
				g2.setColor(Color.ORANGE);
				g2.draw(snake.getSnakeBody().get(i));
			}

		}
	}

//	public void DrawSnakeFood(Graphics2D g2) {
//		g2.setColor(Color.GREEN);
//		g2.fill(snakeFood.getFood());
//	}
	public void DrawSnakeFood(Graphics2D g) {
		g.drawImage(snakeFood.getImage(),(int) snakeFood.getFood().getX(),(int) snakeFood.getFood().getY(), 
				(int)snakeFood.getFood().getWidth(),(int)snakeFood.getFood().getHeight(),null);
	}

	public void DrawStatusbar(Graphics2D g2) {
		g2.setColor(Color.RED);
		g2.setFont(new Font("Comic Sans MS", Font.BOLD, 35));
		g2.drawString("Snake Game", 390, 50);
		
//		g2.setColor(Color.ORANGE);
//		g2.drawString("FP PBO", 450, 100);
		g2.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
		
		g2.setColor(Color.WHITE);
		g2.drawString("Press Esc for exit!", 5, 20);
		g2.drawString("Press Spacebar for pause!", 5, 50);
		g2.drawString("Press T for teleport!", 5, 80);

		g2.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
		g2.drawString("Time: ", 210, 100);
		g2.drawString("Your Score: ", 680, 100);
		g2.drawString("Teleport: ", 435, 100);
		g2.setColor(Color.BLUE);
		g2.drawString("" + playerScore, 810, 100);
		g2.drawString("" + timer, 270, 100);
		g2.drawString("" + teleportChance, 550, 100);

		if (isGameOver()) {
			g2.setColor(Color.WHITE);
			g2.drawString("Game Over!", 480, 350);

		} else if (!isGameRunning()) {
			g2.setColor(Color.WHITE);
			g2.drawString("Press SpaceBar to Start Game!", 400, 500);
		}

	}
	
	public void DrawChasingObject(Graphics2D g2) {
        if (hunter.isVisible()) {
            g2.setColor(Color.YELLOW);
            g2.fill(hunter.getObject());
        }
    }

	public void changeSnakeDirection(int direction) {
		this.snake.setDirection(direction);
	}

	public void checkCollision() {

		if (isSelfCollisioned() || isBoundryCollisioned()) {

			isGameOver = true;

			stopGame();

		}

		if (isFoodCollisioned()) {

			snake.eat();
			snakeFood = new Bakso();
			playerScore += 5;
		}
		
		if (isChasingObjectCollisioned()) {
	        stopGame(); 
	        isGameOver = true; 
	    }
	}

	public boolean isBoundryCollisioned() {
		if (snake.getDirection() == 1) {
			double centerY = ((Ellipse2D.Double) snake.getSnakeBody().get(0))
					.getMinY();
			return centerY < 127;
		} else if (snake.getDirection() == 2) {
			double centerY = ((Ellipse2D.Double) snake.getSnakeBody().get(0))
					.getMaxY();
			return centerY > 591;
		} else if (snake.getDirection() == 3) {
			double centerX = ((Ellipse2D.Double) snake.getSnakeBody().get(0)).x;
			return centerX > 819;
		} else if (snake.getDirection() == 4) {
			double centerX = ((Ellipse2D.Double) snake.getSnakeBody().get(0))
					.getMinX();
			return centerX < 227.0;
		}
		return false;
	}

	public boolean isSelfCollisioned() {

		if (snake.getDirection() == 1) {
			for (int i = 1; i < snake.getSnakeBody().size(); i++) {
				if ((((Ellipse2D.Double) snake.getSnakeBody().get(0)).getMinY() == ((Ellipse2D.Double) snake
						.getSnakeBody().get(i)).getMaxY())
						&& (((Ellipse2D.Double) snake.getSnakeBody().get(0))
								.getCenterX() == ((Ellipse2D.Double) snake
								.getSnakeBody().get(i)).getCenterX())) {
					return true;
				}
			}

		} else if (snake.getDirection() == 2) {
			for (int i = 1; i < snake.getSnakeBody().size(); i++) {
				if ((((Ellipse2D.Double) snake.getSnakeBody().get(0)).getMaxY() == ((Ellipse2D.Double) snake
						.getSnakeBody().get(i)).getMinY())
						&& (((Ellipse2D.Double) snake.getSnakeBody().get(0))
								.getCenterX() == ((Ellipse2D.Double) snake
								.getSnakeBody().get(i)).getCenterX())) {
					return true;
				}
			}

		} else if (snake.getDirection() == 3) {
			for (int i = 1; i < snake.getSnakeBody().size(); i++) {
				if ((((Ellipse2D.Double) snake.getSnakeBody().get(0)).getMaxX() == ((Ellipse2D.Double) snake
						.getSnakeBody().get(i)).getMinX())
						&& (((Ellipse2D.Double) snake.getSnakeBody().get(0))
								.getCenterY() == ((Ellipse2D.Double) snake
								.getSnakeBody().get(i)).getCenterY())) {
					return true;
				}
			}

		} else if (snake.getDirection() == 4) {
			for (int i = 1; i < snake.getSnakeBody().size(); i++) {
				if ((((Ellipse2D.Double) snake.getSnakeBody().get(0)).getMinX() == ((Ellipse2D.Double) snake
						.getSnakeBody().get(i)).getMaxX())
						&& (((Ellipse2D.Double) snake.getSnakeBody().get(0))
								.getCenterY() == ((Ellipse2D.Double) snake
								.getSnakeBody().get(i)).getCenterY())) {
					return true;
				}
			}
		}

		return false;

	}

	public boolean isFoodCollisioned() {

		boolean collisionedWithFood = false;

		int direction = snake.getDirection();

		Ellipse2D.Double head = snake.getHead();

		if (direction == 1) {
			if ((head.getCenterY() == snakeFood.getFood().getCenterY())
					&& (head.getCenterX() == snakeFood.getFood().getCenterX())) {
				collisionedWithFood = true;
			} else
				collisionedWithFood = false;
		} else if (direction == 2) {

			if ((head.getCenterY() == snakeFood.getFood().getCenterY())
					&& (head.getCenterX() == snakeFood.getFood().getCenterX())) {
				collisionedWithFood = true;
			} else
				collisionedWithFood = false;

		} else if (direction == 3) {
			if ((head.getCenterX() == snakeFood.getFood().getCenterX())
					&& (head.getCenterY() == snakeFood.getFood().getCenterY())) {
				collisionedWithFood = true;
			} else
				collisionedWithFood = false;
		} else if (direction == 4) {
			if ((head.getCenterX() == snakeFood.getFood().getCenterX())
					&& (head.getCenterY() == snakeFood.getFood().getCenterY())) {
				collisionedWithFood = true;
			} else
				collisionedWithFood = false;
		}

		return collisionedWithFood;

	}
	
	public boolean isChasingObjectCollisioned() {
	    if (hunter.isVisible()) {
	        Ellipse2D.Double head = snake.getHead();
	        Ellipse2D.Double chasingObjectEllipse = hunter.getObject();

	        return head.intersects(chasingObjectEllipse.getBounds2D());
	    }
	    return false;
	    
//	    if(hunter.isVisible()) {
//	    	for(int i = 1; i < snake.getSnakeBody().size(); i++) {
//		    Ellipse2D.Double snakeBodyPart = (Ellipse2D.Double) snake.getSnakeBody().get(i);
//		    if (hunter.getObject().getMaxX() == snakeBodyPart.getMinX() &&
//		        hunter.getObject().getMaxY() == snakeBodyPart.getMinY()) {
//		        return true;
//		    }
//		}
//	    }s
		

	}

	public void startGame() {

		if (gameThread.isRunning()) {
			gameThread.restart();
			timerThread.restart();

		} 
		
		else {
			gameThread.start();
			timerThread.start();
		}

	}

	public void pauseGame() {

		gameThread.stop();
		timerThread.stop();
		repaint();

	}

	public void stopGame() {

		gameThread.stop();
		timerThread.stop();

	}

	public boolean isGameRunning() {
		return gameThread.isRunning() && !isGameOver();
	}

	public boolean isGameOver() {
		return isGameOver;
	}
	
	public void Teleport() {
		if(teleportChance > 0) {
			snake.teleport();
			teleportChance--;
		}
		
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		repaint();
		if (hunter.isVisible()) {
            // Jika objek yang mengejar ular terlihat, gerakkan objek tersebut menuju ular
			hunter.moveTowardsSnake(snake);
        }

	}

}