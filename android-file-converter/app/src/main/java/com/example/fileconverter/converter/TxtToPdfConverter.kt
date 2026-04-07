package com.example.fileconverter.converter

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import com.example.fileconverter.utils.FileHelper
import java.io.ByteArrayOutputStream

object TxtToPdfConverter {

    /**
     * Converts plain text into a PDF with simple multi-line pagination.
     */
    fun convert(context: Context, textUri: Uri): String {
        val text = String(FileHelper.readAllBytes(context, textUri))
        val document = PdfDocument()
        val paint = Paint().apply {
            textSize = 14f
            isAntiAlias = true
        }

        val pageWidth = 595 // A4 width in points
        val pageHeight = 842 // A4 height in points
        val margin = 40
        val lineHeight = (paint.textSize * 1.6f).toInt()
        val lines = text.split("\n")

        var pageNumber = 1
        var y = margin
        var page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())

        for (line in lines) {
            if (y > pageHeight - margin) {
                document.finishPage(page)
                pageNumber++
                y = margin
                page = document.startPage(PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create())
            }
            page.canvas.drawText(line, margin.toFloat(), y.toFloat(), paint)
            y += lineHeight
        }
        document.finishPage(page)

        val output = ByteArrayOutputStream()
        document.writeTo(output)
        document.close()

        val fileName = "text_${System.currentTimeMillis()}.pdf"
        return FileHelper.saveBytesToDownloads(context, fileName, "application/pdf", output.toByteArray())
    }
}
