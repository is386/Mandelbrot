public class Main {
    public static void main(final String[] args) throws Exception {
        // Ignoring number of threads for now.
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
            man.save("man.png");
        } catch (final Exception e) {
            System.out.println(e);
            System.exit(1);
        }
    }
}
