package org.opensourcecraft.client;

import javax.swing.JOptionPane;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

public class GreatCharacterControl extends BetterCharacterControl {
	
	public Geometry dummy = new Geometry("Box", new Box(0.0f, 0.0f, 0.0f));
	
	public GreatCharacterControl(Node root, PhysicsSpace ps) {
		root.attachChild(root);
		dummy.addControl(new RigidBodyControl(0f));
		new UpdateThread(this).start();
	}
	
	public void setPhysicsLocation(Vector3f location) {
		dummy.setLocalTranslation(location);
	}
	
	public Vector3f getPhysicsLocation() {
		return dummy.getLocalTranslation();
	}
	
}

class UpdateThread extends Thread {
	
	private GreatCharacterControl gcc = null;
	
	public UpdateThread(GreatCharacterControl gcc) {
		this.gcc = gcc;
	}
	
	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				gcc.warp(gcc.dummy.getLocalTranslation());
				Thread.sleep(100);
				run();
			} catch (Error e) {
				if(e instanceof StackOverflowError) {
					new Thread(new UpdateThread(gcc)).start();
					Thread.currentThread().interrupt();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}