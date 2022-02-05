package com.habileducation.pdfexport.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Base64OutputStream
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by Annas Surdyanto on 15/09/21.
 *
 */

fun File.asBase64(): String {
    return ByteArrayOutputStream().use { outputStream ->
        Base64OutputStream(outputStream, Base64.DEFAULT).use { base64FilterStream ->
            this.inputStream().use { inputStream ->
                inputStream.copyTo(base64FilterStream)
            }
        }
        return@use outputStream.toString()
    }
}

fun String.asDrawable(context: Context): Int {
    return context.resources.getIdentifier(this, "drawable", context.packageName)
}

fun String.asDrawableBitmap(context: Context): Bitmap? {
    val icon = context.resources.getIdentifier(this, "drawable", context.packageName)
    val bitmapdraw = ContextCompat.getDrawable(context, icon)

    return if (bitmapdraw != null) {
        val b: Bitmap = bitmapdraw.toBitmap(100, 100)
        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
        smallMarker
    } else null
}

fun Uri.getMimeType(context: Context): String {
    val mimeType: String? = if (ContentResolver.SCHEME_CONTENT == this.scheme) {
        val cr: ContentResolver = context.contentResolver
        cr.getType(this)
    } else {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(this.toString())
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.lowercase(Locale.getDefault())
        )
    }
    return mimeType ?: ""
}

fun String.toStringArray(): List<String> {
    var newData = this.replace("[", "")
    newData = newData.replace("]", "")

    /** These two removals are used for getting through API (not sheet service)*/
    /*newData = newData.removeRange(0,0)
    newData = newData.removeRange(newData.length,newData.length)*/

    val splitter = newData.split("\",")
    val output = ArrayList<String>()
    splitter.forEach {
        var item = it.replace("\"", "")
        output.add(item)
    }
    return output
}

fun Bitmap.asBase64(): String {
    val byteArrayOutputStream = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun Long.toDateString(): String {
    val formatter = SimpleDateFormat("dd MMM yyyy", Locale.US)
    return formatter.format(Date(this))
}

fun Long.toDateInNumber(): String {
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    return formatter.format(Date(this))
}

fun Long.toYearString(): String {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.get(Calendar.YEAR).toString()
}

fun String.toDate(): Long {
    var milliseconds = System.currentTimeMillis()
    val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    try {
        val d: Date? = formatter.parse(this)
        milliseconds = d?.time ?: milliseconds
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return milliseconds
}

fun String.toTime(): Long {
    var milliseconds = System.currentTimeMillis()
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    try {
        val d: Date? = formatter.parse(this)
        milliseconds = d?.time ?: milliseconds
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return milliseconds
}


fun String.toIdr(): String {
    val output = this.replace(",", "")
        .replace(".", "")
        .replace("IDR", "")
        .replace(" ", "")
    val formatter: NumberFormat = DecimalFormat("###,###,###")
    val result =
        if (this.isNotEmpty()) formatter.format(output.toLong()) else this
    return result
}

fun String.toAmount(): String {
    return if (this.contains(",")) this.replace(",", "") else this
}

fun String.copyToClipboard(context: Context, label: String) {
    val clipboard: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(label, this)
    clipboard.setPrimaryClip(clip)
    label.toashShortly(context)
}

fun String.toashShortly(context: Context) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}