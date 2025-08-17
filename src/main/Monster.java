package main;

import java.awt.Dimension;
import java.awt.Graphics;
import imgs.Img;

class Monster implements Entity{
  EntityState state = new AwakeMonster();
  Sword s;
  private Point location;
  @Override
  public Point location(){ return location; }
  @Override
  public void location(Point p){ location = p; }
  Monster(Point location){ this.location= location; }
  public double speed() {return state.speed();}
  @Override
  public void ping(Model m){
      var size = m.camera().location().distance(location).size();
      state.ping(size, m, this);
  } 
  public double chaseTarget(Monster outer, Point target){
    var arrow= target.distance(outer.location());
    double size= arrow.size();
    arrow = arrow.times(speed() / size);
    outer.location(outer.location().add(arrow));
    return size;
  }
  public void hit(){state = new DeadMonster();} 
  @Override
  public void draw(Graphics g, Point center, Dimension size) {
    state.draw(this, g, center, size);
  }
}

interface EntityState{
	void draw(Monster Self, Graphics g, Point center, Dimension size);
	double speed();
	void ping(double size ,Model m, Monster self);
	}


class RoamingMonster implements EntityState{
    private int count = 0;
    private static Point targetPoint = new Point(Math.random() * 16, Math.random() * 16);
    @Override
    public void draw(Monster self, Graphics g, Point center, Dimension size) {
        self.drawImg(Img.AwakeMonster.image, g, center, size);
    }
    @Override
    public double speed(){ return 0.05d; }
    @Override
    public void ping(double size, Model m, Monster self){
        if(++count >= 50) { 
            targetPoint = new Point(Math.random() * 16, Math.random() * 16); 
            count = 0; 
        }
        self.chaseTarget(self, targetPoint);
        if (size < 0.6d){ m.onGameOver(); }
    }
}

class AwakeMonster implements EntityState{
    @Override
    public void draw(Monster self, Graphics g, Point center, Dimension size) {
        self.drawImg(Img.AwakeMonster.image, g, center, size);
    }
    @Override
    public double speed(){ return 0.05d; }
    @Override
    public void ping(double size, Model m, Monster self){
        self.chaseTarget(self, m.camera().location());
        if(size > 6) {self.state = new AsleepMonster();}
        if (size < 0.6d){ m.onGameOver(); }
    }
}
class AsleepMonster implements EntityState{
	@Override
	public void draw(Monster self, Graphics g, Point center, Dimension size) {
	self.drawImg(Img.SleepMonster.image, g, center, size);
	}
	@Override
	public double speed(){ return 0.0d; }
	@Override
	public void ping(double size, Model m, Monster self){
	    if(size < 6) {self.state = new AwakeMonster();}
	  }
}
class DeadMonster implements EntityState{
	private int count = 0;
	@Override
	public void draw(Monster self, Graphics g, Point center, Dimension size) {
	self.drawImg(Img.DeadMonster.image, g, center, size);
	}
	@Override
	public double speed(){ return 0.0d; }
	@Override
	public void ping(double size, Model m, Monster self){
    count++;
    if(count>=100) {m.remove(self);}
    m.remove(self.s);
	  }
}


	
