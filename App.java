public class App {
    public static void main(final String[] args) throws Exception {
        final long startTime = System.currentTimeMillis();
        if (args.length < 6) {
            System.out.println("Usage: java App <size> <threshold> <xlo> <xhi> <ylo> <yhi>");
            System.exit(1);
        }
        try {
            final int size = Integer.parseInt(args[0]);
            final int thresh = Integer.parseInt(args[1]);
            final double xlo = Double.parseDouble(args[2]);
            final double xhi = Double.parseDouble(args[3]);
            final double ylo = Double.parseDouble(args[4]);
            final double yhi = Double.parseDouble(args[5]);

            final Mandelbrot man = new Mandelbrot(size, thresh, xlo, xhi, ylo, yhi);
            man.generate();
            System.out.println("Execution time in milliseconds: " + (System.currentTimeMillis() - startTime));
            man.save("man.png");
        } catch (final Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}

// java App 1100 5000 -1.236 -1.191 0.14 0.172
// java App 1000 5000 -0.74998880248225142145 -0.74998880228812666519
// 0.00699725115971273323 0.00699725130530630042
// java App 5500 250 -0.840716 -0.840732 0.22420 0.224216
// java App 3000 550 -0.15 -0.312 0.626 0.7532