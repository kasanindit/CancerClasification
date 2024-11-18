package com.dicoding.asclepius.history

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.adapter.HistoryAdapter
import com.dicoding.asclepius.database.HistoryRoomDatabase
import com.dicoding.asclepius.databinding.ActivityHistoryBinding
import com.dicoding.asclepius.repository.HistoryRepository

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private lateinit var historyViewModel: HistoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestStoragePermission()

        val historyDao = HistoryRoomDatabase.getDatabase(application).historyDao()
        val repository = HistoryRepository(historyDao)
        val viewModelFactory = HistoryModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory)[HistoryViewModel::class.java]

        val adapter = HistoryAdapter(this)  // Pass context to adapter
        binding.rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvHistory.setHasFixedSize(true)
        binding.rvHistory.adapter = adapter

        historyViewModel.getAllHistory().observe(this) { historyList ->
            adapter.submitList(historyList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted. Images can now be displayed.", Toast.LENGTH_SHORT).show()
            binding.rvHistory.adapter?.notifyDataSetChanged()
        } else {
            Toast.makeText(this, "Permission denied to access images.", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
        const val REQUEST_PERMISSION_CODE = 1001
    }
}