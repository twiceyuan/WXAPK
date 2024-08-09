package com.twiceyuan.wxapk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.documentfile.provider.DocumentFile

class FileBrowserActivity : ComponentActivity() {

    private val initialUri = DocumentsContract.buildTreeDocumentUri(
        /* authority = */ "com.android.externalstorage.documents",
        /* documentId = */ "primary:Download"
    )

    // 可能的两个微信 APK 存储路径，不是这俩就会触发选择
    private val wxDirUris = listOf(
        DocumentsContract.buildTreeDocumentUri(
            /* authority = */ "com.android.externalstorage.documents",
            /* documentId = */ "primary:Download/WeiXin"
        ),
        DocumentsContract.buildTreeDocumentUri(
            /* authority = */ "com.android.externalstorage.documents",
            /* documentId = */ "primary:Download/WeChat"
        ),
    )

    private var currentDocTree: DocumentFile? = null
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val fileNames = mutableListOf<String>()
    private val fileDocs = mutableMapOf<String, DocumentFile?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupListView()
        handleInitialUri()
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupListView() {
        listView = ListView(this)
        setContentView(listView)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileNames)
        listView.adapter = adapter
        listView.setOnItemClickListener { _, _, position, _ -> handleItemClick(position) }
    }

    private fun handleInitialUri() {
        // 如果已经有权限，直接展示
        contentResolver.persistedUriPermissions.forEach {
            if (it.uri in wxDirUris) {
                val tree = DocumentFile.fromTreeUri(this, it.uri) ?: return
                displayFiles(tree)
                return
            }
        }

        // 否则触发选择路径
        toast(R.string.prompt_choose_apk_dir)
        val chooseExternalContract = getChooseExternalContract()
        registerForActivityResult(chooseExternalContract, activityResultRegistry) { uri ->
            uri ?: return@registerForActivityResult
            applicationContext.contentResolver.takePersistableUriPermission(
                uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            val tree = DocumentFile.fromTreeUri(this, uri) ?: return@registerForActivityResult
            displayFiles(tree)
        }.launch(initialUri)
    }

    private fun getChooseExternalContract() = object : ActivityResultContract<Uri?, Uri?>() {
        override fun createIntent(context: Context, input: Uri?): Intent {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                if (input != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    putExtra(DocumentsContract.EXTRA_INITIAL_URI, input)
                }
            }
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return intent?.data
        }
    }

    // 处理列表点击事件
    private fun handleItemClick(position: Int) {
        if (position == 0) {
            navigateToParentDirectory()
        } else {
            val fileName = fileNames[position]
            val file = fileDocs[fileName] ?: return
            if (file.isFile && fileName.contains(".apk.1")) {
                openInstallerActivity(file)
            } else if (file.isDirectory) {
                navigateToChildDirectory(file)
            } else {
                toast(R.string.not_support_file)
            }
        }
    }

    // 返回上级目录
    private fun navigateToParentDirectory() {
        val parentFile = currentDocTree?.parentFile
        if (parentFile != null && parentFile.canRead() && parentFile.canWrite()) {
            displayFiles(parentFile)
        } else {
            toast(R.string.already_root_dir)
        }
    }

    private fun navigateToChildDirectory(childTree: DocumentFile) {
        displayFiles(childTree)
    }

    // 分发 uri 到安装器页面
    private fun openInstallerActivity(selectedFile: DocumentFile) {
        val installIntent = Intent(this, InstallerActivity::class.java).apply {
            data = selectedFile.uri
        }
        startActivity(installIntent)
    }

    private fun displayFiles(doc: DocumentFile) {
        currentDocTree = doc

        fileNames.clear()
        fileDocs.clear()

        fileNames.add("..")

        val apkRegex = Regex(".*\\.apk(\\.1){1,10}\$")
        doc.listFiles().forEach {
            val fileName = it.name ?: return@forEach
            val isApk = it.isFile && apkRegex.matches(fileName)
            fun displayName() = when {
                it.isDirectory -> "📁 $fileName"
                isApk -> "📦 $fileName"
                else -> error("Unknown file type")
            }
            if (it.isDirectory || isApk) {
                val displayName = displayName()
                fileNames.add(displayName)
                fileDocs[displayName] = it
            }
        }
        adapter.notifyDataSetChanged()
    }
}
