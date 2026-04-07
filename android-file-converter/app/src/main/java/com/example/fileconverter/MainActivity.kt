package com.example.fileconverter

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.fileconverter.converter.ImageToPdfConverter
import com.example.fileconverter.converter.PdfToImageConverter
import com.example.fileconverter.converter.TxtToPdfConverter
import com.example.fileconverter.databinding.ActivityMainBinding
import com.example.fileconverter.utils.ConversionType
import com.example.fileconverter.utils.FileHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var selectedFileUri: Uri? = null

    private val conversionOptions = ConversionType.entries.toTypedArray()

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            selectedFileUri = it
            // Persist read permission so app can still access file later.
            contentResolver.takePersistableUriPermission(
                it,
                IntentFlags.READ
            )
            binding.tvSelectedFile.text = "Selected: ${FileHelper.uriFileName(this, it)}"
            setStatus("Status: File selected")
        }
    }

    private val writePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startConversion()
        } else {
            Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupSpinner()
        setupClickListeners()
    }

    private fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            conversionOptions.map { it.label }
        )
        binding.spinnerConversionType.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.btnSelectFile.setOnClickListener {
            launchPickerForSelectedMode()
        }
        binding.btnConvert.setOnClickListener {
            checkPermissionAndConvert()
        }
    }

    private fun launchPickerForSelectedMode() {
        val mimeTypes = when (currentType()) {
            ConversionType.IMAGE_TO_PDF -> arrayOf("image/png", "image/jpeg")
            ConversionType.PDF_TO_IMAGE -> arrayOf("application/pdf")
            ConversionType.TXT_TO_PDF -> arrayOf("text/plain")
        }
        filePickerLauncher.launch(mimeTypes)
    }

    private fun checkPermissionAndConvert() {
        if (selectedFileUri == null) {
            setStatus("Status: Please select a file first")
            return
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val granted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                writePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                return
            }
        }
        startConversion()
    }

    private fun startConversion() {
        val uri = selectedFileUri ?: return

        binding.progressBar.visibility = android.view.View.VISIBLE
        setStatus("Status: Converting...")
        binding.btnConvert.isEnabled = false

        lifecycleScope.launch {
            try {
                val resultPath = withContext(Dispatchers.IO) {
                    when (currentType()) {
                        ConversionType.IMAGE_TO_PDF -> ImageToPdfConverter.convert(this@MainActivity, uri)
                        ConversionType.PDF_TO_IMAGE -> PdfToImageConverter.convert(this@MainActivity, uri)
                        ConversionType.TXT_TO_PDF -> TxtToPdfConverter.convert(this@MainActivity, uri)
                    }
                }
                setStatus("Success: Saved to $resultPath")
            } catch (ex: Exception) {
                setStatus("Failed: ${ex.message ?: "Unknown error"}")
            } finally {
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnConvert.isEnabled = true
            }
        }
    }

    private fun currentType(): ConversionType {
        return conversionOptions[binding.spinnerConversionType.selectedItemPosition]
    }

    private fun setStatus(message: String) {
        binding.tvStatus.text = message
    }

    private object IntentFlags {
        const val READ: Int = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
}
