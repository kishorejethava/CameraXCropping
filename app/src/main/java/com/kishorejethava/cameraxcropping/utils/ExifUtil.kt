package com.kishorejethava.cameraxcropping.utils

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.IOException

object ExifUtil {
    fun rotateBitmap(src: String, bitmap: Bitmap): Bitmap {
        try {
            val exif = ExifInterface(src)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            if (orientation == 1) {
                return bitmap
            }
            val matrix = Matrix()
            when (orientation) {
                2 -> matrix.setScale(-1f, 1f)
                3 -> matrix.setRotate(180f)
                4 -> {
                    matrix.setRotate(180f)
                    matrix.postScale(-1f, 1f)
                }
                5 -> {
                    matrix.setRotate(90f)
                    matrix.postScale(-1f, 1f)
                }
                6 -> matrix.setRotate(90f)
                7 -> {
                    matrix.setRotate(-90f)
                    matrix.postScale(-1f, 1f)
                }
                8 -> matrix.setRotate(-90f)
                else -> return bitmap
            }
            return try {
                val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                bitmap.recycle()
                oriented
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                bitmap
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bitmap
    }
}