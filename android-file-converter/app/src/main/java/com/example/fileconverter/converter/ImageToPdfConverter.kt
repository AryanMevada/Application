package com.example.fileconverter.converter

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.fileconverter.utils.FileHelper
import java.io.ByteArrayOutputStream

object ImageToPdfConverter {

    /**
     * Converts a picked image to a single-page PDF.
     */
    fun convert(context: Context, imageUri: Uri): String {
        val imageBytes = FileHelper.readAllBytes(context, imageUri)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            ?: throw IllegalArgumentException("Selected file is not a valid image")

        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
        val page = document.startPage(pageInfo)
        page.canvas.drawBitmap(bitmap, 0f, 0f, null)
        document.finishPage(page)

        val output = ByteArrayOutputStream()
        document.writeTo(output)
        document.close()

        val fileName = "image_${System.currentTimeMillis()}.pdf"
        return FileHelper.saveBytesToDownloads(context, fileName, "application/pdf", output.toByteArray())
    }
}
