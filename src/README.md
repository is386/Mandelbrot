# Mandelbrot - Sequential

Single threaded implementation of the Mandelbrot assignment. Examples were run on Tux.

## Usage

Compile:

`javac App.java`

Run:

`java App <size> <threshold> <xlo> <xhi> <ylo> <yhi>`

## Examples

Snowman: 9.842s

`java App 1100 5000 -1.236 -1.191 0.14 0.172`

Fields: 9.478s

`java App 1000 5000 -0.74998880248225142145 -0.74998880228812666519 0.00699725115971273323 0.00699725130530630042`

Dragon: 9.518s

`java App 5500 250 -0.840716 -0.840732 0.22420 0.224216`

Custom: 9.168s

`java App 3000 550 -0.15 -0.312 0.626 0.7532`
