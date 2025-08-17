package main;

import java.util.ArrayList;
import java.util.List;

record Phase(Model model, Controller controller){ 
  private static Phase createLevel(Runnable next, Runnable first, Compact.KeyBinds currentBinds, List<Entity> ee) {
    Camera c= new Camera(new Point(5, 5));
    Sword s= new Sword(c);
    Cells cells= new Cells();
    var m= new Model(){
    	List<Entity> entities = new ArrayList<>(List.of(c, s));
    	{entities.addAll(ee);}
    @Override   
	public Camera camera(){ return c; }
    @Override
	public List<Entity> entities(){return entities;}
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
  
  public static Phase level1(Runnable next, Runnable first, Compact.KeyBinds currentBinds) {
	  return createLevel(next,first,currentBinds, List.of(new Monster(new Point(8, 8)))); 
  }
  public static Phase level2(Runnable next, Runnable first, Compact.KeyBinds currentBinds) {
	    Monster r = new Monster(new Point(16, 16));
	    r.state = new RoamingMonster();
	    return createLevel(next,first,currentBinds, List.of(
	    		new Monster(new Point(0, 0)),
	    		new Monster(new Point(16, 0)),
	    		new Monster(new Point(0, 16)), r)		
	    		); 
	  }
  public static Phase level3(Runnable next, Runnable first, Compact.KeyBinds currentBinds) {
	  return createLevel(next, first, currentBinds, List.of(new Monster(new Point(8,8))));
  }


}