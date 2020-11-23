import java.util.HashMap;

public class Main {
    public static void main(final String[] args) throws Exception {
        // Ignoring number of threads for now.
        HashMap<String, Object> argmap = mapCommandLineArguments(args);
        final Mandelbrot man;
        int[][] imgArr;
        try {
            if ((int) argmap.get("NUMTHREADS") <= 1) {
                man = new Mandelbrot((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"));
                imgArr = man.sequential();
            } else if ((boolean) argmap.get("QUEUED")) {
                man = new Mandelbrot((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"));
                imgArr = man.dynamicThreads((int) argmap.get("NUMTHREADS"));
            } else {
                man = new Mandelbrot((int) argmap.get("SIZE"), (int) argmap.get("THRESHOLD"),
                        (double) argmap.get("XLO"), (double) argmap.get("XHI"), (double) argmap.get("YLO"),
                        (double) argmap.get("YHI"));
                imgArr = man.staticThreads((int) argmap.get("NUMTHREADS"));
            }
            man.save("man.png", imgArr);
        } catch (final Exception e) {
            System.out.println(e);
            System.exit(1);
        }
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
            } else if (args[marker].equals("-QUEUED")) {
                result.put("QUEUED", true);
                marker += 1;
            } else {
                throw new RuntimeException("Unrecognized command line argument: " + args[marker]);
            }
        }

        return result;
    }
}
