package com.devaeon.mediastoreimageswithfolders.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.devaeon.mediastoreimageswithfolders.adapter.FilterAdapter
import com.devaeon.mediastoreimageswithfolders.adapter.ItemsAdapter
import com.devaeon.mediastoreimageswithfolders.databinding.ActivityMainBinding
import com.devaeon.mediastoreimageswithfolders.viewModel.MediaViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ItemsAdapter
    private lateinit var filterAdapter: FilterAdapter
    private val viewModel by viewModel<MediaViewModel>()
    private val requestStorage = 1
    private var granted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                hasPermissions(
                    this,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.WAKE_LOCK
                ) -> {
                    viewModel.loadLibraryContent()
                }

                else -> requestStoragePermission()
            }
        } else {
            when {
                hasPermissions(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK
                ) -> {
                    viewModel.loadLibraryContent()
                }

                else -> requestStoragePermission()
            }
        }
        setContentView(binding.root)

        setupAdapter()
        setupFolderAdapter()
    }

    private fun setupAdapter() {
        adapter = ItemsAdapter { }
        binding.recyclerView.adapter = adapter

        viewModel.mediaImages.onEach {
            Log.i(TAG, "setupAdapter: ${it.size}")
            adapter.submitList(it)
        }.launchIn(lifecycleScope)
    }

    private fun setupFolderAdapter() {
        filterAdapter = FilterAdapter { mediaType, title, images ->
            adapter.submitList(images)
        }
        binding.folderFilter.adapter = filterAdapter

        viewModel.mediaFolders.onEach {
            Log.i(TAG, "setupAdapter: ${it.size}")
            filterAdapter.submitList(it)
        }.launchIn(lifecycleScope)
    }

    private fun requestStoragePermission() {
        Log.w("SplashScreen", "Storage permission is not granted. Requesting permission")
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.WAKE_LOCK
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK
            )
        }

        ActivityCompat.requestPermissions(this, permissions, requestStorage)
    }

    private fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        val perm = permissions.size
        var numGranted = 0
        when {
            context != null -> {
                permissions.forEach { permission ->
                    Log.d("SplashScreen", "Checking permission : $permission")
                    when {
                        ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED -> {
                            Log.w("SplashScreen", "not granted : $permission")
                        }

                        else -> {
                            Log.d("SplashScreen", "granted : $permission")
                            numGranted++
                        }
                    }
                }
            }
        }
        granted = numGranted == perm
        return granted
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when {
            requestCode != requestStorage -> {
                Log.d(
                    "SplashScreen", "Got unexpected permission result: $requestCode"
                )
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                return
            }

            (permissions.size == grantResults.size) -> {
                Log.d("SplashScreen", "Storage permission granted")
                granted = true
                viewModel.loadLibraryContent()
            }

            else -> {
                Toast.makeText(this, "permissions not granted", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }


}

private const val TAG = "MainActivityLogs"