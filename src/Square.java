// This class is used for the dynamic division of tasks. A Mandelbrot is split into squares.
public class Square {
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