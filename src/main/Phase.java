package main;

import java.util.List;

record Phase(Model model, Controller controller){ 
  static Phase level1(Runnable next, Runnable first, Compact.KeyBinds currentBinds) {
    Camera c= new Camera(new Point(5, 5));
    Sword s= new Sword(c);
    Cells cells= new Cells();
    var m= new Model(){
      List<Entity> entities= List.of(c, s, new Monster(new Point(0, 0)));
    @Override
	public Camera camera(){ return c; }
    @Override
	public List<Entity> entities(){ return entities; }
    
	@Override
	public void remove(Entity e){ 
        entities= entities.stream()
          .filter(ei->!ei.equals(e))
          .toList();
      }
    @Override
	public Cells cells(){ return cells; }
    @Override
	public void onGameOver(){ first.run(); }
    @Override
	public void onNextLevel(){ next.run(); }
    };
    return new Phase(m, new Controller(c, s, currentBinds));    
  }
}