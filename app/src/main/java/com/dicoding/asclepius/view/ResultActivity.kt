package com.dicoding.asclepius.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.dicoding.asclepius.database.History
import com.dicoding.asclepius.database.HistoryRoomDatabase
import com.dicoding.asclepius.databinding.ActivityResultBinding
import com.dicoding.asclepius.history.HistoryActivity
import com.dicoding.asclepius.history.HistoryModelFactory
import com.dicoding.asclepius.history.HistoryViewModel
import com.dicoding.asclepius.repository.HistoryRepository

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
            imageUri?.let {
                Log.d("Image URI", "showImage: $it")
                binding.resultImage.setImageURI(it)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }

        val historyDao = HistoryRoomDatabase.getDatabase(application).historyDao()
        val repository = HistoryRepository(historyDao)
        val viewModelFactory = HistoryModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory)[HistoryViewModel::class.java]

        // TODO: Menampilkan hasil gambar, prediksi, dan confidence score.
        val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))
        imageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.resultImage.setImageURI(it)
        }

        val results = intent.getStringExtra("results")
        val inferenceTime = intent.getLongExtra("inferenceTime", 0)

        val resultLabel = results?.substringBeforeLast(" ")
        val confidenceScore = results?.substringAfterLast(" ")?.replace("%", "")?.toFloatOrNull()?.times(100) ?: 0f
        val formattedConfidenceScore = String.format("%.1f%%", confidenceScore)


        binding.resultText.text = "Hasil Klasifikasi: $resultLabel $formattedConfidenceScore\nWaktu Inference: $inferenceTime ms"

        val history = History(
            resultLabel = resultLabel,
            confidenceScore = confidenceScore,
            image = imageUri.toString()
        )
        historyViewModel.insert(history)

        binding.btnToHistory.setOnClickListener{
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnToHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val imageUri = Uri.parse(intent.getStringExtra(EXTRA_IMAGE_URI))

                imageUri?.let {
                    binding.resultImage.setImageURI(it)

                    try {
                        contentResolver.takePersistableUriPermission(
                            it,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )
                    } catch (e: SecurityException) {
                        Log.e("Permission Error", "Failed to take persistable URI permission", e)
                    }
                }
            } else {
                Toast.makeText(this, "Permission denied. Cannot access image.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val REQUEST_PERMISSION_CODE = 1001
    }
}