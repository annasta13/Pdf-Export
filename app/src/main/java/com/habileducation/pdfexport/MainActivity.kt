package com.habileducation.pdfexport

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.StrictMode
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.habileducation.pdfexport.data.User
import com.habileducation.pdfexport.databinding.ActivityMainBinding
import com.habileducation.pdfexport.pdfService.AppPermission
import com.habileducation.pdfexport.pdfService.AppPermission.Companion.permissionGranted
import com.habileducation.pdfexport.pdfService.AppPermission.Companion.requestPermission
import com.habileducation.pdfexport.pdfService.FileHandler
import com.habileducation.pdfexport.pdfService.PdfService
import java.io.File

class MainActivity : AppCompatActivity() {
    private val userData = listOf(
        User(1, "Amir", "Tan", "Khan"),
        User(2, "Michael", "Calvin", "Gonzalez"),
        User(3, "Alim", "Bin", "Hasan"),
        User(4, "John", "Carl", "Tean"),
        User(5, "James", "Brown", "Cold"),
        User(6, "Virats", "Kader", "Can"),
        User(7, "Lim", "Lui", "Pao"),
        User(8, "Endro", "Tava", "Pero"),
        User(9, "Dani", "Pedro", "Leo"),
        User(10, "Leonardo", "Chris", "Luiza")
    )

    private var viewDataBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewDataBinding?.apply {
            lifecycleOwner = this@MainActivity
            val adapter = UserAdapter().also { it.submitList(userData) }
            userList.adapter = adapter
            exportButton.setOnClickListener {
                createPdf()
            }
        }
        if (!permissionGranted(this)) requestPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == AppPermission.REQUEST_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermission(this)
                toastErrorMessage("Permission should be allowed")
            }
        }
    }

    private fun createPdf() {
        val onError: (Exception) -> Unit = { toastErrorMessage(it.message.toString()) }
        val onFinish: (File) -> Unit = { openFile(it) }
        val paragraphList = listOf(
            getString(R.string.paragraph1), getString(R.string.paragraph2)
        )
        val pdfService = PdfService()
        pdfService.createUserTable(
            data = userData,
            paragraphList = paragraphList,
            onFinish = onFinish,
            onError = onError
        )
    }

    private fun toastErrorMessage(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun openFile(file: File) {
        val path = FileHandler().getPathFromUri(this, file.toUri())
        val pdfFile = File(path)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        builder.detectFileUriExposure()
        val pdfIntent = Intent(Intent.ACTION_VIEW)
        pdfIntent.setDataAndType(pdfFile.toUri(), "application/pdf")
        pdfIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(pdfIntent)
        } catch (e: ActivityNotFoundException) {
            toastErrorMessage("Can't read pdf file")
        }
    }
}