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
        for (int i = 0; i < (this.width * this.height); i++) {
            double x = i % this.width;
            double y = i / this.width;
            double xc = this.xlo + (this.xhi - this.xlo) * x / this.width;
            double yc = this.ylo + (this.yhi - this.ylo) * y / this.height;
            int iters = this.compute(xc, yc);
            this.imgArr[i] = (255 - iters) * 6;
        }
    }

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
