package com.android.example.cameraxbasic.utils

import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.util.Log
import android.util.Size
import androidx.annotation.CheckResult
import com.kishorejethava.cameraxcropping.fragments.CameraFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


/**
 * crop bitmap based on given Rect
 */
fun Bitmap.crop(crop: Rect): Bitmap {
    require(crop.left < crop.right && crop.top < crop.bottom) { "Cannot use negative crop" }
    require(crop.left >= 0 && crop.top >= 0 && crop.bottom <= this.height && crop.right <= this.width) {
        "Crop is outside the bounds of the image"
    }
    return Bitmap.createBitmap(this, crop.left, crop.top, crop.width(), crop.height())
}

/**
 * Get the size of a bitmap.
 */
fun Bitmap.size(): Size = Size(this.width, this.height)

/**
 * Calculate the position of the [Size] within the [containingSize]. This makes a few
 * assumptions:
 * 1. the [Size] and the [containingSize] are centered relative to each other.
 * 2. the [Size] and the [containingSize] have the same orientation
 * 3. the [containingSize] and the [Size] share either a horizontal or vertical field of view
 * 4. the non-shared field of view must be smaller on the [Size] than the [containingSize]
 *
 * Note that the [Size] and the [containingSize] are allowed to have completely independent
 * resolutions.
 */
@CheckResult
fun Size.scaleAndCenterWithin(containingSize: Size): Rect {
    val aspectRatio = width.toFloat() / height

    // Since the preview image may be at a different resolution than the full image, scale the
    // preview image to be circumscribed by the fullImage.
    val scaledSize = maxAspectRatioInSize(containingSize, aspectRatio)
    val left = (containingSize.width - scaledSize.width) / 2
    val top = (containingSize.height - scaledSize.height) / 2
    return Rect(
            /* left */ left,
            /* top */ top,
            /* right */ left + scaledSize.width,
            /* bottom */ top + scaledSize.height
    )
}

/**
 * Determine the maximum size of rectangle with a given aspect ratio (X/Y) that can fit inside the
 * specified area.
 *
 * For example, if the aspect ratio is 1/2 and the area is 2x2, the resulting rectangle would be
 * size 1x2 and look like this:
 * ```
 *  ________
 * | |    | |
 * | |    | |
 * | |    | |
 * |_|____|_|
 * ```
 */
@CheckResult
fun maxAspectRatioInSize(area: Size, aspectRatio: Float): Size {
    var width = area.width
    var height = (width / aspectRatio).roundToInt()

    return if (height <= area.height) {
        Size(area.width, height)
    } else {
        height = area.height
        width = (height * aspectRatio).roundToInt()
        Size(min(width, area.width), height)
    }
}

fun storeImage(image: Bitmap, photoFile: File): Uri {
    try {
        val fos = FileOutputStream(photoFile)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()
    } catch (e: FileNotFoundException) {
        Log.d(CameraFragment.TAG, "File not found: " + e.message)
    } catch (e: IOException) {
        Log.d(CameraFragment.TAG, "Error accessing file: " + e.message)
    }
    return Uri.fromFile(photoFile)
}

/**
 * Calculate the crop from the [fullImage] for the credit card based on the [cardFinder] within the [previewImage].
 *
 * Note: This algorithm makes some assumptions:
 * 1. the previewImage and the fullImage are centered relative to each other.
 * 2. the fullImage circumscribes the previewImage. I.E. they share at least one field of view, and the previewImage's
 *    fields of view are smaller than or the same size as the fullImage's
 * 3. the fullImage and the previewImage have the same orientation
 */
fun cropImage(fullImage: Bitmap, previewSize: Size, cardFinder: Rect): Bitmap {
    require(
            cardFinder.left >= 0 &&
                    cardFinder.right <= previewSize.width &&
                    cardFinder.top >= 0 &&
                    cardFinder.bottom <= previewSize.height
    ) { "Card finder is outside preview image bounds" }

    // Scale the previewImage to match the fullImage
    val scaledPreviewImage = previewSize.scaleAndCenterWithin(fullImage.size())
    val previewScale = scaledPreviewImage.width().toFloat() / previewSize.width

    // Scale the cardFinder to match the scaledPreviewImage
    val scaledCardFinder = Rect(
            (cardFinder.left * previewScale).roundToInt(),
            (cardFinder.top * previewScale).roundToInt(),
            (cardFinder.right * previewScale).roundToInt(),
            (cardFinder.bottom * previewScale).roundToInt()
    )

    // Position the scaledCardFinder on the fullImage
    val cropRect = Rect(
            max(0, scaledCardFinder.left + scaledPreviewImage.left),
            max(0, scaledCardFinder.top + scaledPreviewImage.top),
            min(fullImage.width, scaledCardFinder.right + scaledPreviewImage.left),
            min(fullImage.height, scaledCardFinder.bottom + scaledPreviewImage.top)
    )

    return fullImage.crop(cropRect)
}