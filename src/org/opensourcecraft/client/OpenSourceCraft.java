package org.opensourcecraft.client;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetKey;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.audio.AudioNode;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.objects.PhysicsCharacter;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import de.lessvoid.nifty.Nifty;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.*;

import org.opensourcecraft.util.GameSettings;

@SuppressWarnings("deprecation")
public class OpenSourceCraft extends SimpleApplication implements ActionListener {
    
    private boolean left = false, right = false, up = false, down = false;
    private RigidBodyControl landscape;
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            if (value) { left = true; } else { left = false; }
        } else if (binding.equals("Right")) {
            if (value) { right = true; } else { right = false; }
        } else if (binding.equals("Up")) {
            if (value) { up = true; } else { up = false; }
        } else if (binding.equals("Down")) {
            if (value) { down = true; } else { down = false; }
        } else if (binding.equals("Jump")) {
            player.jump();
        }
    }
    
    
    private static AppSettings prgmSettings;
    
	protected CharacterControl player;
    
    protected BulletAppState bulletAppState;
    
    protected static JFrame craftFrame;
    
    protected static Canvas gameCanvas;
    
    protected static OpenSourceCraft craft;
    
    protected static JTextField wsFieldOne = new JTextField("80", 5),
    		wsFieldTwo = new JTextField("80", 5),
  		  genHeightField = new JTextField("10", 5);
    
    protected static JCheckBox bedrockChecker = new JCheckBox("Bedrock", true),
    		stoneChecker = new JCheckBox("Stone", false);
   
    protected static JPanel settingsPanel;
    
    public static void main(String[] args) {
    	java.awt.EventQueue.invokeLater(new Runnable() {
    	      public void run() {
    	    	  craftFrame = new JFrame("OpenSourceCraft v0.4-snapshot");
    	    	  craftFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	    	  craftFrame.setSize(1152, 900);
    	    	  craftFrame.setLayout(new BorderLayout());
    	    	  craft = new OpenSourceCraft();
    	    	  craft.setShowSettings(false);
    	    	  prgmSettings = new AppSettings(true);
    	    	  prgmSettings.setTitle("OpenSourceCraft");
    	    	  prgmSettings.setResolution(1152, 864);
    	    	  prgmSettings.setSamples(8);
    	    	  prgmSettings.setFullscreen(false);
    	    	  craft.setSettings(prgmSettings);
    	    	  craft.createCanvas();
    	    	  JmeCanvasContext ctx = (JmeCanvasContext) craft.getContext();
    	    	  ctx.setSystemListener(craft);
    	    	  gameCanvas = ctx.getCanvas();
    	    	  gameCanvas.setPreferredSize(new Dimension(1152, 864));
    	    	  wsFieldOne.setHorizontalAlignment(JTextField.CENTER);
    	    	  wsFieldTwo.setHorizontalAlignment(JTextField.CENTER);
    	    	  genHeightField.setHorizontalAlignment(JTextField.CENTER);
    	          settingsPanel = new JPanel();
    	          settingsPanel.setLayout(new GridBagLayout());
    	          JLabel exLabel = new JLabel("x");
    	          JButton playButton = new JButton("Play Game");
    	          playButton.addActionListener(new java.awt.event.ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) { openCanvas(); }
    	        	  
    	          });
    	          addComps(settingsPanel, wsFieldOne, exLabel, wsFieldTwo,
    	        		  genHeightField, bedrockChecker, stoneChecker, playButton);
    	          craftFrame.add(settingsPanel);
    	          craftFrame.setVisible(true);
    	      }
    	    
    	    	public void addComps(JComponent parent, Component...components) {
    	    		for(Component comp : components) {
    	    			GridBagConstraints center = new GridBagConstraints();
    	    	        center.anchor = GridBagConstraints.CENTER;
    	    	        center.fill = GridBagConstraints.NONE;
    	    			parent.add(comp, center);
    	    		}
    	    	}
    	    	
    	    	public void openCanvas() {
    	    		craftFrame.setVisible(false);
    	    		settingsPanel.setVisible(false);
    	    		craft.applySettings(new GameSettings(
    	    				Integer.parseInt(genHeightField.getText()),
    	    				Integer.parseInt(wsFieldOne.getText()),
    	    				Integer.parseInt(wsFieldTwo.getText()),
    	    				bedrockChecker.isSelected(),
    	    				stoneChecker.isSelected()
    	    		));
    	    		craftFrame.add(gameCanvas);
    	    		craftFrame.setVisible(true);
    	    	}
    	});
    }
    
    @SuppressWarnings("static-access")
	public void applySettings(GameSettings gs) {
    	this.MAP_X_MAX = gs.mapXMax;
  	  	this.MAP_Z_MAX = gs.mapZMax;
  	  	this.MAX_HEIGHT = gs.maxHeight;
  	  	this.bedrock = gs.bedrock;
  	  	this.stone = gs.stone;
    }
    
    public static int MAX_HEIGHT = 10;
    public static int MAP_X_MAX = 80;
    public static int MAP_Z_MAX = 80;
    public static boolean bedrock = true;
    public static boolean stone = false;
    int selectedTex = 0;
    BitmapText selectedBlockText;
    
    protected ArrayList<Spatial> animals = new ArrayList<Spatial>();
    protected ArrayList<Geometry> blocks = new ArrayList<Geometry>();
    protected HashMap<String, Integer> inventoryItems = new HashMap<String, Integer>();
    protected int inventorySelected = 0;
    
	@Override
	public void simpleInitApp() {
		System.out.println("Bedrock: " + bedrock + ", Stone: " + stone);
	   assetManager.registerLocator(System.getProperty("user.dir") + "/assets",  FileLocator.class);
       bulletAppState = new BulletAppState();
       stateManager.attach(bulletAppState);
       CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
       player = new CharacterControl(capsuleShape, 1f);
       player.setJumpSpeed(20);
       player.setGravity(30);
       guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
       BitmapText ch = new BitmapText(guiFont, false);
       ch.setSize(guiFont.getCharSet().getRenderedSize() * 3);
       ch.setText("+");        // fake crosshairs :)
       ch.setLocalTranslation( // center
       settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
       settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
       guiNode.attachChild(ch);
       selectedBlockText = new BitmapText(guiFont, false);
       selectedBlockText.setText("Selected Block: Grass");
       selectedBlockText.setLocalTranslation(220, 20, 0);
       guiNode.attachChild(selectedBlockText);
       /* uncomment for background music
        AudioNode ambient = new AudioNode(assetManager,
              "Sounds/techbliss.ogg", false);
       ambient.play();
       */
       
       player.setPhysicsLocation(
               new Vector3f(MAP_X_MAX / 2, MAX_HEIGHT + 1, MAP_Z_MAX / 2));
       flyCam.setMoveSpeed(13);
       flyCam.setRotationSpeed(5);
       viewPort.setBackgroundColor(new ColorRGBA(0, 0.7f, 0.8f, 1));
       int prevHeight = MAX_HEIGHT / 2;
       boolean hill;
       if(new Random().nextInt(2) + 1 == 1) {
    	   hill = false;
       } else {
    	   hill = true;
       }
       boolean downHill = false;
       boolean extremeHill = false;
       if(new Random().nextInt(20) + 1 == 10) {
    	   extremeHill = true;
       } else {
    	   extremeHill = false;
       }
       int hillMax = randHeight() / 2;
       int prevHillLoc = new Random().nextInt(MAP_X_MAX);
       int prevHillMax = randHeight() / 2;
       for(int i = 0; i < MAP_X_MAX; i+=2) {
           for(int i2 = 0; i2 < MAP_Z_MAX; i2+=2) {
        	   if(!hill && new Random().nextInt(150) + 1 == new Random().nextInt(150) + 1
        			   || i == prevHillLoc) {
        		   hill = true;
        	   	   prevHillLoc = i;
        	   }
               int randHeightChance = new Random().nextInt(10) + 1;
               int newHeight = -1;
               int maxPlusHeight = 5;
               if(i == prevHillLoc) {
        		   hill = true;
        		   prevHillLoc = i;
        	   }
               if(hill)  {
            	   maxPlusHeight = 7;
               }
               if(extremeHill) {
            	   maxPlusHeight = 9;
               }
               if(downHill) {
            	   maxPlusHeight = 3;
               }
               if(hill && newHeight >= hillMax) {
        		   downHill = true;
        	   }
               if(randHeightChance <= maxPlusHeight) {
            	   newHeight = prevHeight + 2;
               } else {
            	   newHeight = prevHeight - 2;
               }
               if(newHeight < 0) newHeight = prevHeight;
               if(newHeight > MAX_HEIGHT) newHeight = MAX_HEIGHT;
               if(newHeight == prevHeight)
                       prevHeight = randHeight();
                String texture = "Textures/grass.jpg";
                int otherChance = new Random().nextInt(125) + 1;
                if(otherChance == 5) {
                    int texID = new Random().nextInt(2) + 1;
                    if(texID == 1)
                    { texture = "Textures/sand.jpg"; }
                    else if(texID == 2)
                    { texture = "Textures/gravel.jpg"; }
                } else {
                    texture = "/Textures/grass.jpg";
                }
                prevHeight = newHeight;
                makeCube(i, newHeight, i2, texture);
                int treeChance = new Random().nextInt(75) + 1;
                if(treeChance == 25) {
                    int i3final = newHeight + 2;
                    for(int i3 = newHeight + 2; i3 < MAX_HEIGHT +
                            new Random().nextInt(10) + 5; i3+=2) {
                        makeCube(i, i3, i2, "Textures/bark.jpg");
                        i3final+=2;
                    }
                    String leaves = "Textures/leaves.jpg";
                    makeCube(i, i3final, i2, leaves);
                    makeCube(i, i3final+2, i2, leaves);
                    makeCube(i-2, i3final, i2-2, leaves);
                    makeCube(i-2, i3final, i2, leaves);
                    makeCube(i, i3final, i2-2, leaves);
                    makeCube(i+2, i3final, i2, leaves);
                    makeCube(i, i3final, i2+2, leaves);
                    makeCube(i+2, i3final, i2+2, leaves);
                    makeCube(i-2, i3final, i2+2, leaves);
                    makeCube(i+2, i3final, i2-2, leaves);
                    makeCube(i-2, i3final-2, i2-2, leaves);
                    makeCube(i-2, i3final-2, i2, leaves);
                    makeCube(i, i3final-2, i2-2, leaves);
                    makeCube(i+2, i3final-2, i2, leaves);
                    makeCube(i, i3final-2, i2+2, leaves);
                    makeCube(i+2, i3final-2, i2+2, leaves);
                    makeCube(i-2, i3final-2, i2+2, leaves);
                    makeCube(i+2, i3final-2, i2-2, leaves);
                }
                int grassChance = new Random().nextInt(20) + 1;
                if(grassChance == 10) {
                    Spatial grass = assetManager.loadModel("Models/grass.obj");
                    Material grassMat = new Material(assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md");
                    grassMat.setColor("Color", new ColorRGBA(0, 100, 0, 0));
                    grass.setMaterial(grassMat);
                    grass.setLocalTranslation(new Vector3f(
                        i, newHeight+1, i2
                    ));
                    rootNode.attachChild(grass);
                }
                int flowerChance = new Random().nextInt(30) + 1;
                if(flowerChance == 10) {
                    Spatial flower = assetManager.loadModel("Models/flower.obj");
                    Material flowerMat = new Material(assetManager,
                        "Common/MatDefs/Misc/Unshaded.j3md");
                    flowerMat.setColor("Color", new ColorRGBA(0.8f, 0, 0, 0));
                    flower.setMaterial(flowerMat);
                    flower.setLocalTranslation(new Vector3f(
                        i, newHeight+1, i2
                    ));
                    rootNode.attachChild(flower);
                }
                if(bedrock) {
                	makeCube(i, -5, i2, "Textures/bedrock.jpg");
                }
                if(stone) {
                	for(int y = -5; y < MAX_HEIGHT / 4; y+=2) {
                    	makeCube(i, y, i2, "Textures/stone.jpg");
                	}
                }
           }
       }
       for(int i2 = 0; i2 < new Random().nextInt(50) + 1; i2++) {
           Spatial pig = assetManager.loadModel("Models/pig.obj");
           Material pigHide = new Material(assetManager,
               "Common/MatDefs/Misc/Unshaded.j3md");
           pigHide.setColor("Color", ColorRGBA.Pink);
           pig.setUserData("health", 100);
           pig.setMaterial(pigHide);
           pig.setLocalTranslation(new Vector3f(
               new Random().nextInt(MAP_X_MAX),
               MAX_HEIGHT + 1,
               new Random().nextInt(MAP_Z_MAX)
           ));
           rootNode.attachChild(pig);
           animals.add(pig);
       }
       for(int i3 = 0; i3 < new Random().nextInt(50) + 1; i3++) {
           Spatial chicken = assetManager.loadModel("Models/chicken.obj");
           Material chickenFeathers = new Material(assetManager,
               "Common/MatDefs/Misc/Unshaded.j3md");
           chicken.setUserData("health", 100);
           chicken.setMaterial(chickenFeathers);
           chicken.setLocalTranslation(new Vector3f(
               new Random().nextInt(MAP_X_MAX),
               MAX_HEIGHT + 1,
               new Random().nextInt(MAP_Z_MAX)
           ));
           rootNode.attachChild(chicken);
           animals.add(chicken);
       }
       inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
       inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
       inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
       inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
       inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
       inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_ESCAPE));
       inputManager.addListener(this, "Left");
       inputManager.addListener(this, "Right");
       inputManager.addListener(this, "Up");
       inputManager.addListener(this, "Down");
       inputManager.addListener(this, "Jump");
       inputManager.addMapping("click",
                   new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
       inputManager.addMapping("rightClick",
               new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
       inputManager.addMapping("cycleForward",
               new KeyTrigger(KeyInput.KEY_R));
       inputManager.addMapping("cycleBackward",
               new KeyTrigger(KeyInput.KEY_T));
       inputManager.addMapping("inventoryCycle",
               new KeyTrigger(KeyInput.KEY_E));
       inputManager.addListener(new ActionListener() {

               public void onAction(String name, boolean isPressed, float tpf) {
                    if(!isPressed) {
                      if(selectedTex > 5) {
                          selectedTex = 0;
                      } else {
                          selectedTex++;
                      }
                      if(selectedTex == 0) selectedBlockText.setText("Selected Block: "
                              + "Grass");
                      if(selectedTex == 1) selectedBlockText.setText("Selected Block: "
                              + "Sand");
                      if(selectedTex == 2) selectedBlockText.setText("Selected Block: "
                              + "Bricks");
                      if(selectedTex == 3) selectedBlockText.setText("Selected Block: "
                              + "Tree");
                      if(selectedTex == 4) selectedBlockText.setText("Selected Block: "
                              + "Leaves");
                      if(selectedTex == 5) selectedBlockText.setText("Selected Block: "
                              + "Bushes");
                      if(selectedTex == 6) selectedBlockText.setText("Selected Animal: "
                              + "Pig");
                    }
               }
               
       }, "cycleForward");
       inputManager.addListener(new ActionListener() {

               public void onAction(String name, boolean isPressed, float tpf) {
                    if(!isPressed) {
                      if(selectedTex < 0) {
                          selectedTex = 6;
                      } else {
                          selectedTex--;
                      }
                      if(selectedTex == 0) selectedBlockText.setText("Selected Block: "
                              + "Grass");
                      if(selectedTex == 1) selectedBlockText.setText("Selected Block: "
                              + "Sand");
                      if(selectedTex == 2) selectedBlockText.setText("Selected Block: "
                              + "Bricks");
                      if(selectedTex == 3) selectedBlockText.setText("Selected Block: "
                              + "Tree");
                      if(selectedTex == 4) selectedBlockText.setText("Selected Block: "
                              + "Leaves");
                      if(selectedTex == 5) selectedBlockText.setText("Selected Block: "
                              + "Bushes");
                      if(selectedTex == 6) selectedBlockText.setText("Selected Animal: "
                              + "Pig");
                    }
               }
               
       }, "cycleBackward");
       inputManager.addListener(new ActionListener() {

               public void onAction(String name, boolean isPressed, float tpf) {
                    if(!isPressed) {
                      CollisionResults results = new CollisionResults();
                       Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                       rootNode.collideWith(ray, results);
                       if (results.size() > 0) {
                           boolean success = false;
                           int health = -1;
                           try {
                              health = results.getClosestCollision().
                                      getGeometry().getUserData("health");
                              success = true;
                           } catch(Exception e) {}
                           if(!success) {
                               rootNode.detachChild(
                                    results.getClosestCollision().getGeometry());
                           } else {
                               health -= 10;
                               results.getClosestCollision().getGeometry()
                                       .setUserData("health", health);
                               if(health <= 0) {
                                   rootNode.detachChild(
                                    results.getClosestCollision().getGeometry());
                               }
                           }
                           results.getClosestCollision().getGeometry().removeControl(PhysicsControl.class);
                           
                       }
                    }
               }
               
       }, "click");
       inputManager.addListener(new ActionListener() {

               public void onAction(String name, boolean isPressed, float tpf) {
                    if(!isPressed) {
                      CollisionResults results = new CollisionResults();
                       Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                       rootNode.collideWith(ray, results);
                       if (results.size() > 0) {
                           Geometry cubeGeom = new Geometry("Box", new Box(1,1,1));
                           Material cubeMaterial = new Material(assetManager,
                            "Common/MatDefs/Misc/Unshaded.j3md");
                           String texture = "Textures/grass.jpg";
                           if(selectedTex == 1) { texture = "Textures/sand.jpg"; }
                           else if(selectedTex == 2) { texture = "Textures/bricks.jpg"; }
                           else if(selectedTex == 3) { texture = "Textures/bark.jpg"; }
                           else if(selectedTex == 4) { texture = "Textures/leaves.jpg"; }
                           else if(selectedTex == 5) { texture = "Textures/bushes.jpg"; }
                           else if(selectedTex == 6) { texture = "Textures/dummy.jpg"; }
                           cubeMaterial.setTexture("ColorMap", assetManager.loadTexture(
                                texture));
                           cubeGeom.setMaterial(cubeMaterial);
                           Vector3f loc = results.getClosestCollision()
                                  .getGeometry().getLocalTranslation();
                           float x = loc.getX();
                           float y = loc.getY() + 2;
                           float z = loc.getZ();
                           cubeGeom.setLocalTranslation(
                                new Vector3f(x, y, z));
                            if(!(texture.equals("Textures/dummy.jpg"))) {
                                rootNode.attachChild(cubeGeom);
                            } else {
                                Spatial pig = assetManager.loadModel("Models/pig.obj");
                                Material pigHide = new Material(assetManager,
                                    "Common/MatDefs/Misc/Unshaded.j3md");
                                pigHide.setColor("Color", ColorRGBA.Pink);
                                pig.setUserData("health", 100);
                                pig.setMaterial(pigHide);
                                pig.setLocalTranslation(new Vector3f(
                                        new Random().nextInt(MAP_X_MAX),
                                        MAX_HEIGHT + 1,
                                        new Random().nextInt(MAP_Z_MAX)
                                ));
                                rootNode.attachChild(pig);
                                animals.add(pig);
                            }
                       }
                    }
               }
               
       }, "rightClick");
       inputManager.addListener(new ActionListener() {

           public void onAction(String name, boolean isPressed, float tpf) {
                if(!isPressed) {
                	inputManager.setCursorVisible(true);
                	int result = JOptionPane.showOptionDialog(null, "================"
                			+ "==============",
                			"Pause Menu", -1, JOptionPane.PLAIN_MESSAGE, null, new String[] {
                					"Continue Playing", "Quit"
                    }, "Missing");
                	if(result == 0) {
                		inputManager.setCursorVisible(false);
                	} else if(result == 1) {
                		System.exit(0);
                	} else {
                		inputManager.setCursorVisible(false);
                	}
                }
           }
           
       }, "Pause");
       cam.setLocation(player.getPhysicsLocation());
      CollisionShape blockShape = CollisionShapeFactory.createMeshShape(rootNode);
      landscape = new RigidBodyControl(blockShape, 0);
       rootNode.addControl(landscape);
       bulletAppState.getPhysicsSpace().add(landscape);
       bulletAppState.getPhysicsSpace().add(player);
    }
    
    private Vector3f walkDirection = new Vector3f();
    private Vector3f camDir = new Vector3f();
    private Vector3f camLeft = new Vector3f();
    
    @Override
    public void simpleUpdate(float tpf) {
        camDir.set(cam.getDirection()).multLocal(0.6f);
        camLeft.set(cam.getLeft()).multLocal(0.4f);
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }
        player.setWalkDirection(walkDirection);
        cam.setLocation(player.getPhysicsLocation());
        for(Spatial s : animals) {
            if(new Random().nextInt(125) + 1 == 25) {
                if(new Random().nextInt(2) + 1 == 1) {
                    s.move(new Vector3f(
                        0, 0,
                        new Random().nextInt(2) + 1
                    ));
                } else {
                    s.move(new Vector3f(
                        0, 0,
                        -(new Random().nextInt(2) + 1)
                    ));
                }
            }
        }
    }
    
    public void makeCube(int x, int y, int z, String tex) {
        
        Geometry cubeGeom = new Geometry("Box", new Box(1,1,1));
        Material cubeMaterial = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        cubeMaterial.setTexture("ColorMap", assetManager.loadTexture(tex));
        cubeGeom.setMaterial(cubeMaterial);
        cubeGeom.setLocalTranslation(x,y,z);
        /*cubeGeom.addControl(new RigidBodyControl(
                CollisionShapeFactory.createDynamicMeshShape(cubeGeom)
        ));*/
        rootNode.attachChild(cubeGeom);
        blocks.add(cubeGeom);
        //bulletAppState.getPhysicsSpace().add(cubeGeom);
    }
    
    public int randHeight() {
        return new Random().nextInt(MAX_HEIGHT);
    }
    
}