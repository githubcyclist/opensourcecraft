package org.opensourcecraft.util;

public class GameSettings {
	 public int maxHeight = 10;
	 public int mapXMax = 80;
	 public int mapZMax = 80;
	 public boolean bedrock = true;
	 public boolean stone = false;
	 
	 public GameSettings(final int maxHeight, final int mapXMax, final int mapZMax, 
			 final boolean bedrock, final boolean stone) {
		 this.maxHeight = maxHeight;
		 this.mapXMax = mapXMax;
		 this.mapZMax = mapZMax;
		 this.bedrock = bedrock;
		 this.stone = stone;
	 }
}
