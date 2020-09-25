import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Mandelbrot {
    public Mandelbrot() {
    }

    public static int calculatePixel(double real, double imag, int maxIter) {
        double _real = 0, _imag = 0;
        for (int i = 0; i < maxIter; i++) {
            if (_real * _real + _imag * _imag > 4)
                return i;
            double __real = _real * _real - _imag * _imag + real;
            _imag = 2 * _real * _imag + imag;
            _real = __real;
        }
        return maxIter;
    }

    public static int[][] sequentialMandelbrot(int size, int threshold, double xlo, double xhi, double ylo,
            double yhi) {
        int[][] result = new int[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                double real = (((double) x) / size) * (xhi - xlo) + xlo;
                double imag = (((double) y) / size) * (yhi - ylo) + ylo;
                int pixel = calculatePixel(real, imag, threshold);
                result[x][y] = pixel;
            }
        }

        return result;
    }

    /**
     * Reimplementation of multithreaded version without using a consumer queue, as
     * per the professor's instruction
     */
    public static int[][] threadedMandelbrotNoConsumer(final int size, final int threshold, final double xlo,
            final double xhi, final double ylo, final double yhi, final int threadCount) {
        final int tileSize = 32;

        final int tileWidth = Math.floorDiv(size - 1 + tileSize, tileSize);
        int[][] result = new int[size][size];
        Thread[] workers = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int id = i;
            workers[i] = new Thread(() -> {
                for (int tileId = id; tileId < tileWidth * tileWidth; tileId += threadCount) {
                    int tileXId = tileId % tileWidth;
                    int tileYId = Math.floorDiv(tileId, tileWidth);
                    int tileX = tileXId * tileSize;
                    int tileY = tileYId * tileSize;
                    for (int x = tileX; x < Math.min(tileX + tileSize, size); x++) {
                        for (int y = tileY; y < Math.min(tileY + tileSize, size); y++) {
                            double real = (((double) x) / size) * (xhi - xlo) + xlo;
                            double imag = (((double) y) / size) * (yhi - ylo) + ylo;
                            result[x][y] = calculatePixel(real, imag, threshold);
                        }
                    }
                }
            });
        }
        for (Thread worker : workers) {
            worker.start();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public static int[][] threadedMandelbrot(final int size, final int threshold, final double xlo, final double xhi,
            final double ylo, final double yhi, final int threadCount) {
        int[][] result = new int[size][size];

        ArrayList<Thread> workerPool = new ArrayList<>();
        ArrayBlockingQueue workQueue = new ArrayBlockingQueue(32);

        for (int i = 0; i < threadCount; i++) {
            workerPool.add(new Thread(() -> {
                while (true) {
                    try {
                        WorkerMessage nextMessage = (WorkerMessage) workQueue.take();
                        if (nextMessage instanceof WorkerMessageStop)
                            break;
                        else {
                            WorkerMessageContent nextItem = (WorkerMessageContent) nextMessage;
                            for (int x = nextItem.minX; x < nextItem.maxX; x++) {
                                for (int y = nextItem.minY; y < nextItem.maxY; y++) {
                                    double real = (((double) x) / size) * (xhi - xlo) + xlo;
                                    double imag = (((double) y) / size) * (yhi - ylo) + ylo;
                                    int pixel = calculatePixel(real, imag, threshold);
                                    result[x][y] = pixel;
                                }
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }));
        }
        for (Thread worker : workerPool) {
            worker.start();
        }

        /*
         * Strategy 1: Each pixel distributed individually to workers
         * 
         * for (int x = 0; x < size; x++) { for (int y = 0; y < size; y++) { try {
         * workQueue.put(new WorkerMessageContent(x, x+1, y, y+1)); } catch
         * (InterruptedException e) { e.printStackTrace(); } } }
         * 
         * Result: Too much overhead from inter-thread blocking and queue time, no
         * performance gain
         */

        /*
         * Strategy 2: Naive segmentation into large, evenly sized chunks per thread
         * 
         * double chunkWidth = size / ((double) threadCount);
         * 
         * for (int i = 0; i < threadCount; i++) { WorkerMessageContent chunk = new
         * WorkerMessageContent((int)(chunkWidth*i), (int)(chunkWidth*(i+1)), 0, size);
         * try { workQueue.put(chunk); } catch (InterruptedException e) {
         * e.printStackTrace(); } }
         * 
         * Result: Decent performance gain, total performance limited by uneven spread
         * of workload
         */

        /*
         * Strategy 3: Small tiles representing tiny chunks of pixels fed to each worker
         */

        int tileSize = 32;

        for (int tileX = 0; tileX < size; tileX += tileSize) {
            for (int tileY = 0; tileY < size; tileY += tileSize) {
                WorkerMessageContent tile = new WorkerMessageContent(tileX, Math.min(size, tileX + tileSize), tileY,
                        Math.min(size, tileY + tileSize));
                try {
                    workQueue.put(tile);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /*
         * Result: Significant performance gain; the most effective work distribution
         * strategy
         */

        for (int i = 0; i < threadCount; i++) {
            try {
                workQueue.put(new WorkerMessageStop());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for (Thread worker : workerPool) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}

abstract class WorkerMessage {
}

class WorkerMessageContent extends WorkerMessage {
    public int minX;
    public int maxX;
    public int minY;
    public int maxY;

    public WorkerMessageContent(int minX, int maxX, int minY, int maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }
}

class WorkerMessageStop extends WorkerMessage {
}