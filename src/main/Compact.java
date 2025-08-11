package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

class Compact extends JFrame{
  private static final long serialVersionUID= 1L;
  Runnable closePhase= ()->{};
  Phase currentPhase;
  KeyBinds currentBinds = KeyBinds.Defaults();
  Compact(){
    assert SwingUtilities.isEventDispatchThread();
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    phaseZero();
    setVisible(true);
    addWindowListener(new WindowAdapter(){     
    @Override
      public void windowClosed(WindowEvent e){ closePhase.run(); }
    });
  }
  private void phaseZero() {
    var welcome= new JLabel("Welcome to Compact. A compact Java game!");
    var start= new JButton("Start!");
    var controls = new JButton("Change Controls");
    closePhase.run();
    closePhase = ()->{
     remove(welcome);
     remove(start);
     remove(controls);
     };
    add(BorderLayout.CENTER, welcome);
    add(BorderLayout.SOUTH, start);
    add(BorderLayout.NORTH, controls);
    start.addActionListener(e->phaseOne());
    controls.addActionListener(e->settingsPhase());
    setPreferredSize(new Dimension(800, 400));
    pack();
  }

  static class KeyBinds {
	  int up, down, left, right, swingLeft, swingRight;
	  KeyBinds(int up, int down, int left, int right, int swingLeft, int swingRight) {
	    this.up = up; this.down = down; this.left = left; 
	    this.right = right; this.swingLeft = swingLeft; this.swingRight = swingRight;
	  }
	  static KeyBinds Defaults() {
	    return new KeyBinds(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, 
	                       KeyEvent.VK_D, KeyEvent.VK_O, KeyEvent.VK_P);
	  }
	}  
  
  private JButton changableButton(int current, String title, Consumer<Integer> update) {
	   var button = new JButton(title+"("+KeyEvent.getKeyText(current)+")");
	   button.addActionListener(e-> changeControl(button, current, title, update));
	   return button;
	}
  private void changeControl(JButton b ,int current, String title, Consumer<Integer> update) {
	  b.setText("Press a key to replace "+ KeyEvent.getKeyText(current));
	  KeyAdapter keyListener = new KeyAdapter() {
	        @Override
			public void keyPressed(KeyEvent e) {
	            b.setText( title + "(" + KeyEvent.getKeyText(e.getKeyCode()) + ")");
	            update.accept(e.getKeyCode());
	            removeKeyListener(this);
	        }
	    };
	    addKeyListener(keyListener);
	    requestFocus();
  }
  private void settingsPhase() {
	    closePhase.run();
		var settingsLabel = new JLabel("Want to change you controls?");
		var back = new JButton("Back");
		var keyChange = new JPanel();
		add(BorderLayout.NORTH, settingsLabel);
		add(BorderLayout.CENTER, keyChange);
		// Movement controls
	    keyChange.add(changableButton(currentBinds.up,"Up", k -> currentBinds.up = k));
	    keyChange.add(changableButton(currentBinds.down,"Down",k -> currentBinds.down = k ));
	    keyChange.add(changableButton(currentBinds.left,"Left",k -> currentBinds.left = k));
	    keyChange.add(changableButton(currentBinds.right,"Right",k -> currentBinds.right = k ));
	    
	    // Sword controls
	    keyChange.add(changableButton(currentBinds.swingLeft,"Swing Left", k -> currentBinds.swingLeft = k ));
	    keyChange.add(changableButton(currentBinds.swingRight,"Swing Right",k -> currentBinds.swingRight = k));
	    
		add(BorderLayout.SOUTH, back);
		back.addActionListener(e->phaseZero());
		
		 closePhase = ()->{
		    	remove(settingsLabel);
		    	remove(back);
		    	remove(keyChange);
		    };
		    pack();
	  }
  private void phaseOne(){
    setPhase(Phase.level1(()->phaseOne(), ()->phaseZero(), currentBinds));
  }
  void setPhase(Phase p){
    //set up the viewport and the timer
    Viewport v= new Viewport(p.model());
    v.addKeyListener(p.controller());
    v.setFocusable(true);
    Timer timer= new Timer(34, unused->{
      assert SwingUtilities.isEventDispatchThread();
      p.model().ping();
      v.repaint();
    });
    closePhase.run();//close phase before adding any element of the new phase
    closePhase = ()->{ timer.stop(); remove(v); };
    add(BorderLayout.CENTER, v);//add the new phase viewport
    setPreferredSize(getSize());//to keep the current size
    pack();                     //after pack
    v.requestFocus();//need to be after pack
    timer.start();
  }
}