package bouncing_balls;
import java.math.*;

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

	double GRAVITY = 9.82;
	double areaWidth, areaHeight;
	
	Ball [] balls;

	Model(double width, double height) {
		areaWidth = width;
		areaHeight = height;
		
		// Initialize the model with a few balls
		balls = new Ball[2];
		balls[0] = new Ball(width / 3, height * 0.5,
				1.5, 0.5, 0.3,0,-GRAVITY,5);
		balls[1] = new Ball(2 * width / 3, height * 0.7,
				-2, -0.2, 0.4,0,-GRAVITY, 10);
	}

	void step(double deltaT) {
		// TODO this method implements one step of simulation with a step deltaT
		for (Ball b : balls) {
			// detect collision with the border
			double u = b.vy;
			if ( (b.x < b.radius) || (b.x > (areaWidth - b.radius)) ) {
				b.vx *= -1; // change direction of ball
				if(b.x > (areaWidth-b.radius)){b.x = (areaWidth - b.radius) - (b.x - (areaWidth - b.radius));}
				else if(b.x < b.radius){b.x = b.radius + (b.radius - b.x);}
			}
			if (b.y < b.radius || b.y > areaHeight - b.radius) {
				b.vy *= -1;
				if(b.y > (areaHeight-b.radius)){b.y = (areaHeight - b.radius) - (b.y - (areaHeight - b.radius));}
				else if(b.y < b.radius){b.y = b.radius + (b.radius - b.y);}
			}
			//compute new speed of ball
			b.vx += (deltaT * b.ax);
			b.vy += (deltaT * b.ay);
			double x = b.y;
			// compute new position according to the speed of the ball
			b.x += (deltaT * b.vx);
			b.y += (deltaT * b.vy);
			double v = b.vy;
			/*if(u>=0 && v<=0 && b == balls[0]){
				if(b == balls[0]) System.out.print("Ball-1 ");
				else System.out.print("Ball-2 ");
				System.out.println("max heigth = "+x);
			}*/


		}

		if(isCollision()){

			//Reset balls to not be collided
			double resetTicks=0;
			while(!isCollisionNoMore()){
				//Move 0
				double dDT = 100;
				if( !( balls[0].x <= balls[0].radius &&
						balls[0].x >= (areaWidth - balls[0].radius) )
						){balls[0].x -= (deltaT/dDT) * balls[0].vx;}
				if( !( balls[0].y <= balls[0].radius &&
						balls[0].y >= areaHeight - balls[0].radius)
						){balls[0].y -= (deltaT/dDT) * balls[0].vy;}
				resetTicks++;
				//If still collision, also move 1
				if(!isCollisionNoMore()){
					if( !( balls[1].x <= balls[1].radius &&
							balls[1].x >= (areaWidth - balls[1].radius) )
							){balls[1].x -= (deltaT/dDT) * balls[1].vx;}
					if( !( balls[1].y <= balls[1].radius &&
							balls[1].y >= areaHeight - balls[1].radius)
							){balls[1].y -= (deltaT/100) * balls[1].vy;}
					resetTicks++;
				}
			}
			double v0,v1,u0,u1,I,R,h,o,sina;
			u0 = Math.sqrt(
					Math.pow(balls[0].vx,2)+
					Math.pow(balls[0].vy,2));
			u1 = Math.sqrt(
					Math.pow(balls[1].vx,2)+
					Math.pow(balls[1].vy,2));
			I = balls[0].m*u0 + balls[1].m*u1;
			R = -(u1-u0);
			v0 = ( (I - balls[1].m * R) / (balls[0].m + balls[1].m) );
			v1 = ( (I - balls[1].m * R) / (balls[0].m + balls[1].m) ) + R;
			//TODO determine x & y part by using pythagoras on the intersection line
			h = Math.sqrt(
					Math.pow((balls[0].x-balls[1].x),2)+
					Math.pow((balls[0].y-balls[1].y),2));
			o = Math.sqrt(Math.pow((balls[0].y-balls[1].y),2));
			sina = o/h;

			if(balls[0].x == balls[1].x){
				//Vertical hit vektor
				if(balls[0].y < balls[1].y){
					//0 under
					balls[0].vy -= v0;
					balls[1].vy += v1;
				} else {
					//0 over
					balls[0].vy += v0;
					balls[1].vy -= v1;
				}

			}else if(balls[0].y == balls[1].y){
				//Horizontal hit vektor
				if(balls[0].x < balls[1].x){
					//0 to the left
					balls[0].vx -= v0;
					balls[1].vx += v1;
				} else {
					//0 to the right
					balls[0].vx += v0;
					balls[1].vx -= v1;
				}

			}else if(balls[0].x < balls[1].x){
				//0 is to the left
				if(balls[0].y < balls[1].y){

					//0 under to the left -,- & 1 over to the right +,+
					velocityChanger(balls[0], v0, balls[1], v1, sina,
							-1.0, -1.0, 1.0, 1.0);

				} else {
					//0 over to the left -,+ & 1 over to the right +,-
					velocityChanger(balls[0], v0, balls[1], v1, sina,
							-1.0, 1.0, 1.0, -1.0);
				}
			} else {
				//0 is ti the right
				if(balls[0].y < balls[1].y){
					//0 under to the right +,- & 1 over to the left -,+
					velocityChanger(balls[0], v0, balls[1], v1, sina,
							1.0, -1.0, -1.0, 1.0);
				} else {
					//0 over to the right +,+ & 1 under to the left -,-
					velocityChanger(balls[0], v0, balls[1], v1, sina,
							1.0, 1.0, -1.0, -1.0);
				}
			}
			while(resetTicks>0){
				//Move 0
				double dDT = 100;
				if( !( balls[0].x <= balls[0].radius &&
						balls[0].x >= (areaWidth - balls[0].radius) )
						){balls[0].x += (deltaT/dDT) * balls[0].vx;}
				if( !( balls[0].y <= balls[0].radius &&
						balls[0].y >= areaHeight - balls[0].radius)
						){balls[0].y += (deltaT/dDT) * balls[0].vy;}
				resetTicks--;
				//If still ticks left, also move 1
				if(resetTicks>0){
					if( !( balls[1].x <= balls[1].radius &&
							balls[1].x >= (areaWidth - balls[1].radius) )
							){balls[1].x += (deltaT/dDT) * balls[1].vx;}
					if( !( balls[1].y <= balls[1].radius &&
							balls[1].y >= areaHeight - balls[1].radius)
							){balls[1].y += (deltaT/100) * balls[1].vy;}
					resetTicks--;
				}
			}

		}
	}
	private boolean isCollision(){
		//return false;
		return Math.sqrt(
				Math.pow(balls[0].x-balls[1].x,2)+
						Math.pow(balls[0].y-balls[1].y,2))
				< (balls[0].radius+balls[1].radius);
	}
	private boolean isCollisionNoMore(){
		return Math.sqrt(
				Math.pow(balls[0].x-balls[1].x,2)+
						Math.pow(balls[0].y-balls[1].y,2))
				>= ((balls[0].radius+balls[1].radius)-0.05*(balls[0].radius+balls[1].radius));
	}
	private void velocityChanger(Ball b1, double v1, Ball b2, double v2, double sina, double b1xPolarity, double b1yPolarity, double b2xPolarity, double b2yPolarity){
		double y1 = sina*v1;
		double x1 = Math.sqrt( Math.pow(v1,2) - Math.pow(y1,2) );
		b1.vx += (b1xPolarity * x1);
		b1.vy += (b1yPolarity * y1);
		double y2 = sina*v2;
		double x2 = Math.sqrt(Math.pow(v2,2)-Math.pow(y2,2));
		b2.vx += (b2xPolarity * x2);
		b2.vy += (b2yPolarity * y2);
	}
	
	/**
	 * Simple inner class describing balls.
	 */
	class Ball {
		
		Ball(double x, double y, double vx, double vy, double r, double ax, double ay, double m) {
			this.x = x;
			this.y = y;
			this.vx = vx;
			this.vy = vy;
			this.radius = r;
			this.ax = ax;
			this.ay = ay;
			this.m = m;
		}

		/**
		 * Position, speed, and radius of the ball. You may wish to add other attributes.
		 */
		double x, y, vx, vy, radius, ax, ay, m;
	}
}
