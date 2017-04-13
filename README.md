# OpenSourceCraft
A Minecraft clone written using Java and jMonkeyEngine.

## A word of caution before you run
OpenSourceCraft is in an extremely early pre-release stage, and the master branch is bleeding-edge and extremely unstable. If you are going to play, download a stable release. 

## I just wanna run it
### The easy way (recommended)
The Java Runtime Environment is required. First, go to the "dist" directory in the repo.

Depending on your operating system and configuration, you may need to take extra steps. On most systems, you can just double-click the file. On Linux, to allow execution by double-click:

`chmod +x MyGame.jar`

If you can't launch by double-click and don't know what to do, open a terminal or command prompt and run this command:

`java -jar MyGame.jar`

### The hard way
You will need the JDK, and Ant is recommended.

Assuming everything's working, run the appropriate command in the repo directory:

#### Windows - No Ant
`"ant/bin/ant.bat" run`

#### Linux - No Ant
`./ant/bin/ant run`

#### Any Platform - With Ant
`ant run`

## I wanna tinker!
Again, the JDK is required. Follow the instructions appropriate for your IDE below.

### JMonkeyEngine SDK (https://github.com/jMonkeyEngine/sdk/releases)

When downloading, select the appropriate binary (.sh or .exe). Run it, and install.

Once it's installed, select File > Import Project > From Zip...

If you haven't already, download the ZIP version of OpenSourceCraft (https://github.com/githubcyclist/opensourcecraft/archive/master.zip). Select it and you're done!

### Eclipse

Hit the new project button on the toolbar. Select Java Project from Existing Ant Buildfile.

Press Browse and navigate to the repo directory. Select build.xml and press OK.

Enter your project name of choice ("OpenSourceCraft" recommended).

After you're done with that, go take a look at the wiki (https://github.com/githubcyclist/opensourcecraft/wiki). It explains how the game works.
