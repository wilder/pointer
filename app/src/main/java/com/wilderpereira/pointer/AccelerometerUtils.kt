package com.wilderpereira.pointer

private const val ALPHA = 0.2f

/**
 * Algorithm to remove the noise from the accelerometer data.
 * https://en.wikipedia.org/wiki/Low-pass_filter
 */
fun lowPassFilter(input: FloatArray, output: FloatArray?): FloatArray {
    if (output == null) return input.copyOf()

    for (i in input.indices) {
        output[i] = output[i] + ALPHA * (input[i] - output[i])
    }
    return output
}