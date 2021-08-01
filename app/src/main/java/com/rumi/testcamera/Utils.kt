package com.rumi.testcamera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.webkit.URLUtil
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlin.math.roundToInt

fun loadImage(imageView: ImageView, path: String) {
    if (path.isEmpty())
        return
    if (URLUtil.isHttpsUrl(path) || URLUtil.isHttpUrl(path)) {
        Glide.with(imageView.context).load(path).into(imageView)
        return
    }
    Glide.with(imageView.context)
        .load(File(path))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
//                    getCircularProgress(imageView.context).setVisible(false, false)
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: com.bumptech.glide.request.target.Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
//                    getCircularProgress(imageView.context).setVisible(false, false)
                return false
            }
        }).into(imageView)
}

fun rotateImage(imagePathName: String) {
    try {
        val file = File(imagePathName)
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        options.inPreferredConfig = Bitmap.Config.RGB_565
        BitmapFactory.decodeStream(FileInputStream(file.absolutePath), null, options)
        // Calculate inSampleSize
        options.inSampleSize =
            calculateInSampleSize(options, 720, 1280) //My device pixel resolution
        // Decode bitmap with inSampleSize set
        val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)
        val rotatedBitmap = rotateBitmaps(bitmap = bitmap, imageFileLocation = imagePathName)
        val fileOutputStream = FileOutputStream(file)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
        fileOutputStream.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun calculateInSampleSize(
    ourOption: BitmapFactory.Options,
    imageWidth: Int, imageHeight: Int
): Int {
    val height = ourOption.outHeight
    val width = ourOption.outWidth
    var inSampleSize = 1
    if (height > imageHeight || width > imageWidth) {
        inSampleSize = if (width > height) {
            (height.toFloat() / imageHeight.toFloat()).roundToInt()
        } else {
            (width.toFloat() / imageWidth.toFloat()).roundToInt()
        }
    }
    return inSampleSize
}

/**
 * Rotates the bitmaps of image into vertical orientation depending on the image orientation
 * @param bitmap  {[Bitmap]}
 * @param imageFileLocation location of image to be rotated
 * @return the {[Bitmap]} of rotated image
 * */
private fun rotateBitmaps(bitmap: Bitmap, imageFileLocation: String): Bitmap {
    val exifInterface = androidx.exifinterface.media.ExifInterface(imageFileLocation)
    println("exif interface is ${exifInterface.rotationDegrees}")
    val orientation = exifInterface.getAttributeInt(
        androidx.exifinterface.media.ExifInterface.TAG_ORIENTATION,
        androidx.exifinterface.media.ExifInterface.ORIENTATION_UNDEFINED
    )
    val matrix = Matrix()
    when (orientation) {
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
        androidx.exifinterface.media.ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(
            180f
        )
    }
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}
