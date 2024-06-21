package com.dutaram.selfquest.utils

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel


class ObjectDetectionHelper(
    context: Context,
    modelFileName: String = "model.tflite",
    labelFileName: String
) {

    private var interpreter: Interpreter
    private val labelList = mutableListOf<String>()
    private val inputSize = 14739


    init {
        // Load the model when the helper is initialized
        interpreter = loadModelFile(context, modelFileName)
        loadLabelList(context, labelFileName)
    }

    private fun loadModelFile(context: Context, modelFileName: String): Interpreter {
        val modelFileDescriptor = context.assets.openFd(modelFileName)
        val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = modelFileDescriptor.startOffset
        val declaredLength = modelFileDescriptor.declaredLength
        val mappedByteBuffer: MappedByteBuffer =
            fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
        val interpreter = Interpreter(mappedByteBuffer)
        inputStream.close()
        return interpreter
    }

    fun preprocessImage(image: Bitmap): ByteBuffer {
        val resizedImage = Bitmap.createScaledBitmap(image, inputSize, inputSize, false)
        val inputBuffer = ByteBuffer.allocateDirect(1 * inputSize * 4)

        inputBuffer.order(ByteOrder.nativeOrder())
        inputBuffer.rewind()

        val pixels = IntArray(inputSize * inputSize)
        resizedImage.getPixels(pixels, 0, inputSize, 0, 0, inputSize, inputSize)

        // Mengisi buffer dengan nilai grayscale dari gambar
        for (pixelValue in pixels) {
            val grayscale = (pixelValue and 0xFF).toFloat() / 255.0f
            inputBuffer.putFloat(grayscale)
        }
        return inputBuffer
    }

    fun runInference(inputBuffer: ByteBuffer): Pair<Int, Float> {
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputSize = outputShape[1]

        val outputBuffer = ByteBuffer.allocateDirect(outputSize * 4)
        outputBuffer.order(ByteOrder.nativeOrder())
        outputBuffer.rewind()

        interpreter.run(inputBuffer, outputBuffer)

        return processOutput(outputBuffer)
    }

    private fun processOutput(outputBuffer: ByteBuffer): Pair<Int, Float> {
        val outputFloatArray = FloatArray(outputBuffer.capacity() / 4)
        outputBuffer.rewind()
        outputBuffer.asFloatBuffer().get(outputFloatArray)

        var maxIndex = 0
        var maxValue = outputFloatArray[0]
        for (i in 1 until outputFloatArray.size) {
            if (outputFloatArray[i] > maxValue) {
                maxValue = outputFloatArray[i]
                maxIndex = i
            }
        }
        return Pair(maxIndex, maxValue)
    }

    private fun loadLabelList(context: Context, labelFileName: String) {
        try {
            val labels =
                context.assets.open(labelFileName).bufferedReader().useLines { it.toList() }
            labelList.clear()
            labelList.addAll(labels)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun getClassName(index: Int): String {
        return if (index >= 0 && index < labelList.size) {
            labelList[index]
        } else {
            "Unknown"
        }
    }
}