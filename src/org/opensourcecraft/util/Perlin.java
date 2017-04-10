package org.opensourcecraft.util;

import java.awt.Color;

import java.awt.Graphics2D;

import java.awt.RenderingHints;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.util.Random;



import javax.imageio.ImageIO;



public class Perlin

{

   private BufferedImage   image;



   private int            section;

   private BufferedImage[]   imageSections;

   private char[]         remap;



   public static void main(String[] args)

   {

      Perlin perlin;

      perlin = new Perlin(1024, 1024, 4, 2.5f, 0x70, 0.9875f, 6);

      System.out.println("writing image file");

      long start = System.currentTimeMillis();

      try

      {

         ImageIO.write(perlin.image, "png", new File("test.png"));

      }

      catch (IOException e)

      {

         // TODO Auto-generated catch block

         e.printStackTrace();

      }

      System.out.println(System.currentTimeMillis() - start);

   }



   /**

    * Builds a Cloud texture map with Perlin noise

    *

    * @param width

    * @param height

    * @param initFreq

    * @param persistency

    * @param decay

    * @param detail

    */

   public Perlin(int width, int height, int initFreq, float persistency,

         int density, float cloudSharpness, int detail)

   {

      long start = System.currentTimeMillis();

      System.out.println("Generating lookup table");

      // generate a re-mapping lookup table

      if (density < 0)

      {

         density = 0;

      }

      else if (density > 0xFF)

      {

         density = 0xFF;

      }

      if (cloudSharpness < 0.0f)

      {

         cloudSharpness = 0.0f;

      }

      else if (cloudSharpness > 1.0f)

      {

         cloudSharpness = 1.0f;

      }

      remap = new char[0xFF];

      for (int i = 0; i < 0xFF; i++)

      {

         remap[i] = (char) (density - i);

         if (remap[i] < 0 || remap[i] > 0xFF)

         {

            remap[i] = (char) 0;

         }

         remap[i] = (char) (0xFF - (Math.pow(cloudSharpness, remap[i]) * 0xFF));

      }

      System.out.println(System.currentTimeMillis() - start);

      System.out.println("Generating noise maps and combining...");

      start = System.currentTimeMillis();

      // time to generate the 2D noise functions

      Noise2D[] noiseMaps = new Noise2D[detail];

      float amplitude = 1.0f;

      for (int i = 0; i < detail; i++)

      {

         noiseMaps[i] = new Noise2D(width, height, initFreq, initFreq,

               amplitude);

         noiseMaps[i].start();

         amplitude /= persistency;

         initFreq *= 2;

      }

      // initialize our main image

      image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);

      Graphics2D g = image.createGraphics();

      g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,

            RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT);

      // as thread finish, blend them in

      boolean keepRunning = true;

      while (keepRunning)

      {

         keepRunning = false;

         for (int i = 0; i < noiseMaps.length; i++)

         {

            if (noiseMaps[i] != null)

            {

               // check to see if we can extract data

               if (!noiseMaps[i].isAlive())

               {

                  // we can get the data

                  g.drawImage(noiseMaps[i].image, null, 0, 0);

                  // allow Java to garbage collect the thread

                  noiseMaps[i] = null;

               }

               else

               {

                  keepRunning = true;

               }

            }

         }

         try

         {

            Thread.sleep(15);

         }

         catch (InterruptedException e)

         {

            // TODO Auto-generated catch block

            e.printStackTrace();

         }

      }

      g.dispose();

      System.out.println(System.currentTimeMillis() - start);

      System.out.println("Adjusting image");

      start = System.currentTimeMillis();

      // split the image up into sections and re-map the image

      adjustImage(Runtime.getRuntime().availableProcessors(), image

            .getWidth()

            * image.getHeight() / 0x40000);

      System.out.println(System.currentTimeMillis() - start);

   }



   /**

    * Adjusts the image using the LUT

    *

    * @param threadCount

    * @param sectionsCount

    */

   public void adjustImage(int threadCount, int sectionsCount)

   {

      if (sectionsCount == 0)

      {

         // need at least one section

         sectionsCount = 1;

      }

      if (sectionsCount < threadCount)

      {

         // split up into more sections

         sectionsCount = threadCount;

      }

      ImageAdjuster[] threads = new ImageAdjuster[threadCount];

      imageSections = new BufferedImage[sectionsCount];

      for (int i = 0; i < sectionsCount; i++)

      {

         int sectionStart = i * image.getHeight() / sectionsCount;

         if (i + 1 == sectionsCount)

         {

            // last section

            imageSections[i] = image.getSubimage(0, sectionStart, image

                  .getWidth(), image.getHeight() - sectionStart);

         }

         else

         {

            imageSections[i] = image.getSubimage(0, sectionStart, image

                  .getWidth(), image.getHeight() / sectionsCount);

         }

      }

      for (int i = 0; i < threads.length; i++)

      {

         threads[i] = new ImageAdjuster(this);

         threads[i].start();

      }

      // sleep this thread until done re-mapping image

      boolean keepRunning = true;

      while (keepRunning)

      {

         keepRunning = false;

         for (int i = 0; i < threads.length; i++)

         {

            if (threads[i].isAlive())

            {

               keepRunning = true;

            }

         }

         try

         {

            Thread.sleep(30);

         }

         catch (InterruptedException e)

         {

            // TODO Auto-generated catch block

            e.printStackTrace();

         }

      }

   }



   /**

    * helper method to give ImageAdjuster threads new image sections to adjust

    *

    * @param thread

    * @return

    */

   private synchronized BufferedImage requestNextSection(ImageAdjuster thread)

   {

      // divide the image up into 64 scan-lines

      if (section == imageSections.length)

      {

         return null;

      }

      BufferedImage toReturn = imageSections[section];

      imageSections[section] = null;

      section++;

      return toReturn;

   }



   /**

    * Internal class used by Perlin to adjust the image using the remap LUT

    *

    * @author Andrew

    */

   private class ImageAdjuster extends Thread

   {

      public BufferedImage   image;

      private Perlin         owner;



      public ImageAdjuster(Perlin owner)

      {

         this.owner = owner;

      }



      @Override

      public void run()

      {

         // get an initial image

         image = owner.requestNextSection(this);

         while (image != null)

         {

            // work

            for (int i = 0; i < image.getWidth(); i++)

            {

               for (int j = 0; j < image.getHeight(); j++)

               {

                  char val = (char) (image.getRGB(i, j) & 0xFF);

                  val = owner.remap[val];

                  image.setRGB(i, j, (val << 16) + (val << 8) + val);

               }

            }

            image = owner.requestNextSection(this);

         }

      }

   }



   private static class Noise2D extends Thread

   {

      BufferedImage   image;

      private int      width;

      private int      height;

      private int      freqX;

      private float   alpha;

      private int      freqY;



      public Noise2D(int width, int height, int freqX, int freqY, float alpha)

      {

         this.width = width;

         this.height = height;

         this.freqX = freqX;

         this.freqY = freqY;

         this.alpha = alpha;

      }



      @Override

      public void run()

      {

         BufferedImage temp = new BufferedImage(freqX, freqY,

               BufferedImage.TYPE_4BYTE_ABGR);

         Graphics2D g = temp.createGraphics();

         // generate a low-res random image

         for (int i = 0; i < freqX; i++)

         {

            for (int j = 0; j < freqY; j++)

            {

               int val = new Random().nextInt(255);

               g.setColor(new Color(val, val, val, (int) (alpha * 0xFF)));

               g.fillRect(i, j, 1, 1);

            }

         }

         g.dispose();

         // re-scale the image up using interpolation (in this case, linear)

         image = new BufferedImage(width, height,

               BufferedImage.TYPE_4BYTE_ABGR);

         g = image.createGraphics();

         g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,

               RenderingHints.VALUE_INTERPOLATION_BILINEAR);



         g.drawImage(temp, 0, 0, width, height, 0, 0, freqX, freqY, null);

         g.dispose();

      }

   }

}
