package com.example.fileconverter.converter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import com.example.fileconverter.utils.FileHelper
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object PdfToImageConverter {

    /**
     * Converts each page of selected PDF into PNG and packs them in one ZIP file.
     */
    fun convert(context: Context, pdfUri: Uri): String {
        val tempPdf = FileHelper.copyUriToCache(context, pdfUri, "pdf")
        val descriptor = ParcelFileDescriptor.open(tempPdf, ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(descriptor)

        val output = ByteArrayOutputStream()
        ZipOutputStream(output).use { zip ->
            for (index in 0 until renderer.pageCount) {
                renderer.openPage(index).use { page ->
                    val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

                    val imageBytes = ByteArrayOutputStream().use { imageOut ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, imageOut)
                        imageOut.toByteArray()
                    }

                    val entryName = "page_${index + 1}.png"
                    zip.putNextEntry(ZipEntry(entryName))
                    zip.write(imageBytes)
                    zip.closeEntry()
                    bitmap.recycle()
                }
            }
        }

        renderer.close()
        descriptor.close()
        tempPdf.delete()

        val fileName = "pdf_images_${System.currentTimeMillis()}.zip"
        return FileHelper.saveBytesToDownloads(context, fileName, "application/zip", output.toByteArray())
    }
}
