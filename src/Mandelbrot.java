import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.File;

public class Mandelbrot {
    private int width;
    private int height;
    private int thresh;
    private double xlo;
    private double xhi;
    private double ylo;
    private double yhi;

    public Mandelbrot(int s, int t, double xlo, double xhi, double ylo, double yhi) {
        this.width = s;
        this.height = s;
        this.thresh = t;
        this.xlo = xlo;
        this.xhi = xhi;
        this.ylo = ylo;
        this.yhi = yhi;
    }

    // Loops over all the pixels for the image and sets the color according to the
    // iterations.
    public int[][] sequential() {
        final long startTime = System.currentTimeMillis();
        int[][] imgArr = new int[this.width][this.height];
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                double xc = this.xlo + (this.xhi - this.xlo) * i / this.width;
                double yc = this.ylo + (this.yhi - this.ylo) * j / this.height;
                imgArr[i][j] = this.compute(xc, yc);
            }
        }
        System.out.printf("Time (ms): %d\n", System.currentTimeMillis() - startTime);
        return imgArr;
    }

    // Splits the image into strips and then uses multiple threads to compute each
    // strip.
    public int[][] staticThreads(int numThreads) {
        final long startTime = System.currentTimeMillis();
        int[][] imgArr = new int[this.width][this.height];
        Thread[] thdArr = new Thread[numThreads];
        // The number of pixels per strip in the image
        int split = Math.round(this.width / numThreads) + 1;

        for (int t = 0; t < numThreads; t++) {
            int t2 = t;
            // int h1 = split * t;
            // int h2 = split * (t + 1);
            thdArr[t] = new Thread(() -> {
                for (int i = 0; i < this.width; i++) {
                    for (int j = t2; j < this.height; j += numThreads) {
                        // if (j >= this.width) {
                        // break;
                        // }
                        double xc = this.xlo + (this.xhi - this.xlo) * i / this.width;
                        double yc = this.ylo + (this.yhi - this.ylo) * j / this.height;
                        imgArr[i][j] = this.compute(xc, yc);
                    }
                }
            });
        }
        for (Thread t : thdArr) {
            t.start();
        }

        for (Thread t : thdArr) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.print("Error:\n" + ex);
            }
        }

        System.out.printf("Time (ms): %d\n", System.currentTimeMillis() - startTime);
        return imgArr;
    }

    public int[][] dynamicThreads(int numThreads) {
        final long startTime = System.currentTimeMillis();
        int[][] imgArr = new int[this.width][this.height];
        Thread[] thdArr = new Thread[numThreads];
        // The number of pixels per strip in the image
        int split = Math.round(this.width / numThreads) + 1;

        for (int t = 0; t < numThreads; t++) {
            int t2 = t;
            // int h1 = split * t;
            // int h2 = split * (t + 1);
            thdArr[t] = new Thread(() -> {
                for (int i = 0; i < this.width; i++) {
                    for (int j = t2; j < this.height; j += numThreads) {
                        // if (j >= this.width) {
                        // break;
                        // }
                        double xc = this.xlo + (this.xhi - this.xlo) * i / this.width;
                        double yc = this.ylo + (this.yhi - this.ylo) * j / this.height;
                        imgArr[i][j] = this.compute(xc, yc);
                    }
                }
            });
        }
        for (Thread t : thdArr) {
            t.start();
        }

        for (Thread t : thdArr) {
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.print("Error:\n" + ex);
            }
        }

        System.out.printf("Time (ms): %d\n", System.currentTimeMillis() - startTime);
        return imgArr;
    }

    // Computes the iterations until the threshold is reached or x^2 + y^2 < 4.
    private int compute(double xc, double yc) {
        int i = 0;
        double x = 0.0;
        double y = 0.0;

        while (x * x + y * y < 2 && i < this.thresh) {
            double xt = x * x - y * y + xc;
            double yt = 2 * x * y + yc;
            x = xt;
            y = yt;
            i++;
        }
        return i;
    }

    // Saves the integer array as a png image.
    public void save(String name, int[][] imgArr) {
        BufferedImage image = new BufferedImage(imgArr.length, imgArr[0].length, BufferedImage.TYPE_INT_ARGB);
        Color[][] colors = mapColors(imgArr);

        for (int i = 0; i < colors.length; i++) {
            for (int j = 0; j < colors[0].length; j++) {
                image.setRGB(i, j, colors[i][j].getRGB());
            }
        }

        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Maps the imgArr int array to a color array.
    private Color[][] mapColors(int[][] imgArr) {
        double third = this.thresh / 3.0;
        double conversion = (255.0 / third);
        Color[][] result = new Color[imgArr.length][imgArr[0].length];

        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[0].length; j++) {
                int iters = imgArr[i][j];
                if (iters < third)
                    result[i][j] = new Color((int) (iters * conversion), 0, 0);
                else if (iters < 2 * third)
                    result[i][j] = new Color(0, (int) ((iters - third) * conversion), 0);
                else
                    result[i][j] = new Color(0, 0, (int) ((iters - third * 2) * conversion));
            }
        }

        return result;
    }
}
