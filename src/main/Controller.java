package main;

class Controller extends Keys {
  Controller(Camera c, Sword s, Compact.KeyBinds currentBinds) {
    setAction(currentBinds.up, c.set(Direction::up), c.set(Direction::unUp));
    setAction(currentBinds.down, c.set(Direction::down), c.set(Direction::unDown));
    setAction(currentBinds.left, c.set(Direction::left), c.set(Direction::unLeft));
    setAction(currentBinds.right, c.set(Direction::right), c.set(Direction::unRight));
    setAction(currentBinds.swingLeft, s.set(Direction::left), s.set(Direction::unLeft));
    setAction(currentBinds.swingRight, s.set(Direction::right), s.set(Direction::unRight));
	  } 
}
