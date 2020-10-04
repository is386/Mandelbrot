import java.awt.image.BufferedImage;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;

// This class is used for the dynamic division of tasks. A Mandelbrot is split into squares.
class Square {
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;

    public Square(int minX, int maxX, int minY, int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
}

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
            int h1 = split * t;
            int h2 = split * (t + 1);
            thdArr[t] = new Thread(() -> {
                for (int i = 0; i < this.width; i++) {
                    for (int j = h1; j < h2; j++) {
                        if (j >= this.width) {
                            break;
                        }
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

    // Splits the image into squares and then puts the squares into a worker pool.
    // The squares are then dynamically assigned to each thread.
    public int[][] dynamicThreads(int numThreads) {
        final long startTime = System.currentTimeMillis();
        int[][] imgArr = new int[this.width][this.height];
        Thread[] thdArr = new Thread[numThreads];
        Queue<Square> squares = new LinkedBlockingQueue<Square>();
        int k = 16;

        // This loop creates all the squares determined by k. These will act as the
        // tasks the threads must get through.
        for (int i = 0; i < this.width; i += k) {
            for (int j = 0; j < this.height; j += k) {
                Square s = new Square(i, Math.min(this.width, i + k), j, Math.min(this.height, j + k));
                squares.add(s);
            }
        }

        // Goes through the threads and assigns squares.
        for (int t = 0; t < numThreads; t++) {
            thdArr[t] = new Thread(() -> {
                while (true) {
                    Square s = squares.poll();
                    if (s == null) {
                        break;
                    } else {
                        for (int i = s.minX; i < s.maxX; i++) {
                            for (int j = s.minY; j < s.maxY; j++) {
                                double xc = this.xlo + (this.xhi - this.xlo) * i / this.width;
                                double yc = this.ylo + (this.yhi - this.ylo) * j / this.height;
                                imgArr[i][j] = this.compute(xc, yc);
                            }
                        }
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
