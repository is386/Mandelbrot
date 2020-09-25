import java.awt.Color;
import java.awt.image.BufferedImage;
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
    private int[] imgArr;

    public Mandelbrot(int s, int t, double xlo, double xhi, double ylo, double yhi) {
        this.width = s;
        this.height = s;
        this.thresh = t;
        this.xlo = xlo;
        this.xhi = xhi;
        this.ylo = ylo;
        this.yhi = yhi;
        this.imgArr = new int[(s * s)];
    }

    public void generate() {
        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < (this.width * this.height); i++) {
            double x = i % this.width;
            double y = i / this.width;
            double xc = (this.xhi - this.xlo) * x / this.width + this.xlo;
            double yc = (this.yhi - this.ylo) * y / this.width + this.ylo;
            int iters = this.compute(xc, yc);
            this.imgArr[i] = (255 - iters) * 6;
        }
        System.out.printf("Time (ms): %d", System.currentTimeMillis() - startTime);
    }

    private int compute(double xc, double yc) {
        int i = 0;
        double x = 0.0;
        double y = 0.0;
        double xy = 2 * x * y;

        while (x + y < 4 && i < this.thresh) {
            double xt = x - y + xc;
            double yt = xy + yc;
            x = xt * xt;
            y = yt * yt;
            xy = 2 * xt * yt;
            i++;
        }
        return i;
    }

    public void save(String name) {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, this.width, this.height, this.imgArr, 0, this.width);
        try {
            ImageIO.write(image, "png", new File(name));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
