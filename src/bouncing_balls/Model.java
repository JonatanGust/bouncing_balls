package bouncing_balls;

import java.awt.*;
import java.util.Random;

/**
 * The physics model.
 *
 * This class is where you should implement your bouncing balls model.
 *
 * The code has intentionally been kept as simple as possible, but if you wish, you can improve the design.
 *
 * @author Simon Robillard
 *
 */
class Model {

	double areaWidth, areaHeight, GRAVITY = -9.82;

	Ball [] balls;
	Boolean [][] collisionOK;
	int nrOfBalls = 2;
	Random myRandom = new Random();

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;

		// Initialize the model with a few balls randomized balls
		balls = new Ball[nrOfBalls];
		for(int i = 0; i < nrOfBalls; i++ ){
            double x = myRandom.nextDouble();
            while (!(x > 0.1 && x < 0.9))
                x = myRandom.nextDouble();

            double y = myRandom.nextDouble();
            while (!(y > 0.1 && y < 0.9))
                y = myRandom.nextDouble();

            double vx = myRandom.nextDouble();
            while (!(vx > 0.7 && vx < 1.2))
                vx = myRandom.nextDouble();
            double isPosetive = myRandom.nextDouble();
            if (isPosetive > 0.5) vx=vx*(-1.0);

            double vy = myRandom.nextDouble();
            while (!(vy > 0.5 && vy < 0.9))
                vy = myRandom.nextDouble();
            isPosetive = myRandom.nextDouble();
            if (isPosetive > 0.5) vy=vy*(-1.0);

            double r = myRandom.nextDouble();
            while (!(r > 0.3 && r < 0.6))
                r = myRandom.nextDouble();

		    balls[i]= new Ball(width * x,height * y, vx, vy, r);
        }
		collisionOK = new Boolean[nrOfBalls][nrOfBalls];
        for (int i = 0; i < nrOfBalls; i++ ){
            for (int j = 0; i < nrOfBalls; i++ ){
                collisionOK[i][j]=true;
            }
        }

	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
        for (int i = 0; i < nrOfBalls ; i++) {
            Ball b = balls[i];
			// detect collision with the border
			if ( (b.x < b.radius) ) {
			    b.x=b.radius;
			    if( (b.vx < 0) )b.vx *= -1; // change direction of ball
			}else if( (b.x > (areaWidth - b.radius)) ){
			    b.x=areaWidth - b.radius;
				if (b.vx > 0 )b.vx *= -1; // change direction of ball
			}
			if ( (b.y < b.radius) ) {
			    b.y=b.radius;
			    if(b.vy < 0)b.vy *= -1; // change direction of ball
			}else if( (b.y > (areaHeight - b.radius) )  ){
			    b.y=(areaHeight - b.radius);
				if(b.vy > 0 )b.vy *= -1; // change direction of ball
			}

			// compute new position according to the speed of the ball
            b.vy += deltaT * GRAVITY;//Applying acceleration after movement gives increase in total momentum
			b.x += deltaT * b.vx;
			b.y += deltaT * b.vy;

			for (int j = 0; j < nrOfBalls ; j++) {
			    if(i != j){
                    Ball b2 = balls[j];
                    double deltaX = b.x - b2.x, deltaY = b.y - b2.y, collisionDistance = b.radius+b2.radius;
                    if( (deltaX*deltaX + deltaY*deltaY) < collisionDistance*collisionDistance) {

                        if(collisionOK[i][j]){
                            collision(b, b2);
                            collisionOK[i][j] = false;
                            collisionOK[j][i] = false;
                        }

                    } else {
                        collisionOK[i][j] = true;
                        collisionOK[j][i] = true;
                    }
			    }
            }
		}
	}

	/**
	 * Simple inner class describing balls.
	 */
	class Ball {

		Ball(double x, double y, double vx, double vy, double r) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius, r, angle;

        public void rectToPolar() {
            r = Math.sqrt(vx * vx + vy * vy);
            angle = Math.atan(vy / vx);
            if (vx < 0) angle += Math.PI;
        }

        public void polarToRect() {
            vx = r * Math.cos(angle);
            vy = r * Math.sin(angle);
        }

        public void rotate(double rotateAngle) {
            rectToPolar();
            angle += rotateAngle;
            polarToRect();
        }
	}

    class Point {

        public Point(Ball myBall) {
            this.ball = myBall;
        }

        double x, y, r, angle;
        Ball ball;

        public void rectToPolar() {
            r = Math.sqrt(x * x + y * y);
            angle = Math.atan(y / x);
            if (x < 0) angle += Math.PI;
        }

        public void polarToRect() {
            x = r * Math.cos(angle);
            y = r * Math.sin(angle);
        }

        public void rotate(double rotateAngle) {
            rectToPolar();
            angle += rotateAngle;
            polarToRect();
        }
    }

    public void collision(Ball b1, Ball b2) {
        double deltaX = b1.x - b2.x, deltaY = b1.y - b2.y;

        double rotAngle=Math.atan(deltaY/deltaX);
        b1.rotate(-rotAngle);
        b2.rotate(-rotAngle);

        double I = b1.radius*b1.vx+b2.radius*b2.vx;
        double R = -(b2.vx-b1.vx);
        b1.vx = (I-R*b2.radius) / (b1.radius+b2.radius);
        b2.vx = R+b1.vx;

        b1.rotate(rotAngle);
        b2.rotate(rotAngle);

    }
}
