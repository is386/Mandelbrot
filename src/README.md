# Mandelbrot

Single and multi-threaded implementation of the Mandelbrot assignment. Examples were run on Tux. Each configuration was timed 10 times, and then the times were averaged. The code for the Mandelbrot generation was inspired by the provided pseudo-code. The argument parsing was inspired by the code written by nac93. The strategy used for static work division was inspired by the assignment document. The strategy used for the dynamic work division was inspired by the assignment document and nac93. For the dynamic division, the worker pool and tile logic was inspired by the assignment document and nac93. hd349's implementation helped me resolve a bug with my worker queue.

## Usage

Compile:

`javac Main.java`

Run:

`java Main -SIZE <int> -THRESHOLD <int> -XLO <double> -XHI <double> -XHI <double> -YLO <double> -YHI <double> -NUMTHREADS <int> -QUEUED`

## Benchmarks

Snowman:

`java Main -SIZE 1100 -THRESHOLD 5000 -XLO -1.236 -XHI -1.191 -YLO 0.14 -YHI 0.172 -NUMTHREADS 16`

Fields:

`java Main -SIZE 1000 -THRESHOLD 5000 -XLO -0.74998880248225142145 -XHI -0.74998880228812666519 -YLO 0.00699725115971273323 -YHI 0.00699725130530630042 -NUMTHREADS 16`

Dragon: (may need to increase max heap size)

`java Main -SIZE 5500 -THRESHOLD 250 -XLO -0.840716 -XHI -0.840732 -YLO 0.22420 -YHI 0.224216 -NUMTHREADS 16`

Flames: (may need to increase max heap size)

`java Main -SIZE 5000 -THRESHOLD 2000 -XLO -0.2512314 -XHI -0.21245745747 -YLO 0.636780784345 -YHI 0.6532236235626 -NUMTHREADS 16`
