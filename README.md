# Mandelbrot

Multi-threaded implementation of an algorithm to produce Mandelbrot images. It has 3 methods, sequential, static, and dynamic. Sequential uses no threads, just to show a baseline. The static method splits the image into strips equal to the number of threads. The dynamic method splits the image into squares, then puts each square into a worker pool. The threads are then assigned a square dynamically until the image is built.

## Usage

Compile:

`javac Main.java`

Run:

`java Main -SIZE <int> -THRESHOLD <int> -XLO <double> -XHI <double> -XHI <double> -YLO <double> -YHI <double> -NUMTHREADS <int> -QUEUED`

## Examples

Snowman:

`java Main -SIZE 1100 -THRESHOLD 5000 -XLO -1.236 -XHI -1.191 -YLO 0.14 -YHI 0.172 -NUMTHREADS 16`

Fields:

`java Main -SIZE 1000 -THRESHOLD 5000 -XLO -0.74998880248225142145 -XHI -0.74998880228812666519 -YLO 0.00699725115971273323 -YHI 0.00699725130530630042 -NUMTHREADS 16`

Dragon:

`java Main -SIZE 5500 -THRESHOLD 250 -XLO -0.840716 -XHI -0.840732 -YLO 0.22420 -YHI 0.224216 -NUMTHREADS 16`

Flames:

`java Main -SIZE 5000 -THRESHOLD 2000 -XLO -0.2512314 -XHI -0.21245745747 -YLO 0.636780784345 -YHI 0.6532236235626 -NUMTHREADS 16`
