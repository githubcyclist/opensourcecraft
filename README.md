# OpenSourceCraft
A Minecraft clone written using jMonkeyEngine.

## A word of caution before you run
OpenSourceCraft is in an extremely early pre-release stage, and the master branch is bleeding-edge and extremely unstable. If you are going to play, download a stable release. 

## I just wanna run it
### The easy way (recommended)
The Java Runtime Environment is required. Run this command in the repo's "dist" directory:


Open the 
### The hard way
You will need the JDK, and Ant is recommended.

Assuming everything's working, run the appropriate command in the repo directory:

### Windows - No Ant
`"ant/bin/ant.bat" run`

### Linux - No Ant
`./ant/bin/ant run`

### Any Platform - With Ant
`ant run`

## I wanna tinker!
Again, the JDK is required. Follow the instructions appropriate for your IDE below.

### JMonkeyEngine SDK (https://github.com/jMonkeyEngine/sdk/releases)

When downloading, select the appropriate binary (.sh or .exe). Run it, and install.

Once it's installed, select File > Import Project > From Zip...

If you haven't already, download the ZIP version. Select it and you're done!

### Eclipse

Hit the new project button on the toolbar. Select Java Project from Existing Ant Buildfile.

Press Browse and navigate to the repo directory. Select build.xml and press OK.

Enter your project name of choice ("OpenSourceCraft" recommended).
