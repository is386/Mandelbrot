import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class Main {

    public static void writeImage(Color[][] pixels, String filename) {
        BufferedImage im = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[0].length; y++) {
                im.setRGB(y, x, pixels[x][y].getRGB());
            }
        }

        try {
            ImageIO.write(im, "png", new File(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Color[][] mapToPixels(int[][] mandelbrotResults, int maxThreshold) {
        double third = maxThreshold / 3.0;
        double conversion = (255.0 / third);
        Color[][] result = new Color[mandelbrotResults.length][mandelbrotResults[0].length];

        for (int x = 0; x < mandelbrotResults.length; x++) {
            for (int y = 0; y < mandelbrotResults[0].length; y++) {
                int iters = mandelbrotResults[x][y];
                if (iters < third)
                    result[x][y] = new Color((int) (iters * conversion), 0, 0);
                else if (iters < 2 * third)
                    result[x][y] = new Color(0, (int) ((iters - third) * conversion), 0);
                else
                    result[x][y] = new Color(0, 0, (int) ((iters - third * 2) * conversion));
            }
        }

        return result;
    }

    public static HashMap<String, Object> mapCommandLineArguments(String[] args) {
        int marker = 0;
        HashMap<String, Object> result = new HashMap<>();
        result.put("NUMTHREADS", 1);
        result.put("OUT", "image");
        result.put("RUNS", 1);
        result.put("QUEUED", false);

        while (marker < args.length) {
            if (args[marker].equals("-NUMTHREADS")) {
                result.put("NUMTHREADS", Integer.parseInt(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-SIZE")) {
                result.put("SIZE", Integer.parseInt(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-THRESHOLD")) {
                result.put("THRESHOLD", Integer.parseInt(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-XLO")) {
                result.put("XLO", Double.parseDouble(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-XHI")) {
                result.put("XHI", Double.parseDouble(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-YLO")) {
                result.put("YLO", Double.parseDouble(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-YHI")) {
                result.put("YHI", Double.parseDouble(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-OUT")) {
                result.put("OUT", args[marker + 1]);
                marker += 2;
            } else if (args[marker].equals("-RUNS")) {
                result.put("RUNS", Integer.parseInt(args[marker + 1]));
                marker += 2;
            } else if (args[marker].equals("-QUEUED")) {
                result.put("QUEUED", true);
                marker += 1;
            } else {
                throw new RuntimeException("Unrecognized command line argument: " + args[marker]);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        HashMap<String, Object> argmap = mapCommandLineArguments(args);
        System.out.println("Computing mandelbrot " + argmap.get("OUT") + "...");
        int[][] mandelbrot = null;
        long totalTime = 0;
        int numRuns = (int) argmap.get("RUNS");
        long minTime = 0;
        long maxTime = 0;
        Mandelbrot man = new Mandelbrot();
        for (int i = 0; i < numRuns; i++) {
            long start = System.currentTimeMillis();
            int numThreads = (int) argmap.get("NUMTHREADS");
            if (numThreads <= 1) {
                mandelbrot = man.sequentialMandelbrot((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"));
            } else if ((boolean) argmap.get("QUEUED")) {
                mandelbrot = man.threadedMandelbrot((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"), numThreads);
            } else {
                mandelbrot = man.threadedMandelbrotNoConsumer((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"), numThreads);
            }
            long time = System.currentTimeMillis() - start;
            if (minTime == 0 || minTime > time)
                minTime = time;
            if (maxTime == 0 || maxTime < time)
                maxTime = time;
            System.out.println("Run " + (i + 1) + "/" + numRuns + " Finished in " + time + " milliseconds");
            totalTime += time;
        }
        if (numRuns > 1) {
            double avg = (totalTime / (double) numRuns);
            double deviation = (maxTime - minTime) / 2.0;
            System.out.println("Average time of " + avg + " ms, +/- " + deviation + " ms");
        }
        System.out.println("Mapping values to pixels...");
        Color[][] image = mapToPixels(mandelbrot, (int) argmap.get("THRESHOLD"));
        System.out.println("Writing to file...");
        writeImage(image, argmap.get("OUT") + ".png");
        System.out.println("Done");
    }
}
