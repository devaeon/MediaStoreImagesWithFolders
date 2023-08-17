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
import androidx.recyclerview.widget.GridLayoutManager
import com.devaeon.mediastoreimageswithfolders.adapter.FilterAdapter
import com.devaeon.mediastoreimageswithfolders.adapter.ItemsAdapter
import com.devaeon.mediastoreimageswithfolders.databinding.ActivityMainBinding
import com.devaeon.mediastoreimageswithfolders.extensions.formatDate
import com.devaeon.mediastoreimageswithfolders.model.AbsListItems
import com.devaeon.mediastoreimageswithfolders.model.DateGroupedItems
import com.devaeon.mediastoreimageswithfolders.model.FolderListWithData
import com.devaeon.mediastoreimageswithfolders.model.ListItems
import com.devaeon.mediastoreimageswithfolders.viewModel.MediaViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var adapter: ItemsAdapter? = null
    private lateinit var filterAdapter: FilterAdapter
    private val viewModel by viewModel<MediaViewModel>()
    private val requestStorage = 1
    private var granted = false
    val consolidatedList = arrayListOf<AbsListItems>()
    var folderListItems = listOf<FolderListWithData>()
    private lateinit var listItems: List<ListItems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                hasPermissions(this, Manifest.permission.READ_MEDIA_AUDIO, Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO, Manifest.permission.WAKE_LOCK) -> viewModel.loadLibraryContent()

                else -> requestStoragePermission()
            }
        } else {
            when {
                hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WAKE_LOCK) -> viewModel.loadLibraryContent()
                else -> requestStoragePermission()
            }
        }
        setContentView(binding.root)

        setupFolderAdapter()
        observeData()


    }

    private fun observeData() {
        //full media images list
        viewModel.mediaImages.onEach {
            listItems = it
            setupAdapter(it)
        }.launchIn(lifecycleScope)


        //folder through media store list items
        viewModel.mediaFolders.onEach {
            folderListItems = it
            filterAdapter.submitList(it)
        }.launchIn(lifecycleScope)
    }


    private fun setupAdapter(listItems: List<ListItems>) {
        CoroutineScope(IO).launch {
            adapter = ItemsAdapter() { }

            withContext(Main) {
                val layoutManager = GridLayoutManager(this@MainActivity, 2)
                binding.recyclerView.layoutManager = layoutManager
                layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        return when (adapter?.getItemViewType(position)) {
                            AbsListItems.TYPE_DATE -> 2
                            else -> 1
                        }
                    }
                }
                binding.recyclerView.adapter = adapter
                adapter?.submitList(getGroupedDate(listItems))
            }
        }

    }

    private fun getGroupedDate(listItems: List<ListItems>): ArrayList<AbsListItems> {
        consolidatedList.clear()
        val groupedMapMap: Map<String, List<ListItems>> = listItems.groupBy { it.dateCreated.formatDate() }
        for ((date, items) in groupedMapMap) {
            var size = 0L
            items.forEach { size += it.size }

            consolidatedList.add(DateGroupedItems(date, items.size, size))
            val groupItems: List<ListItems>? = groupedMapMap[date]
            groupItems?.forEach {
                consolidatedList.add(
                    ListItems(
                        it.file, it.name, it.size, it.imageUri, it.imageId, it.filePath, it.dateCreated, it.dateModified, items.size
                    )
                )
            }
        }
        return consolidatedList
    }

    private fun setupFolderAdapter() {

        filterAdapter = FilterAdapter { listItems ->
            adapter = null
            setupAdapter(listItems)
        }
        binding.folderFilter.adapter = filterAdapter

    }

    private fun requestStoragePermission() {
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
                        ActivityCompat.checkSelfPermission(
                            context, permission
                        ) != PackageManager.PERMISSION_GRANTED -> {
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

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
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