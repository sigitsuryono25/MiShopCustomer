package com.lauwba.surelabs.mishopcustomer.myShop.ui.myshopdashoard

import android.app.Activity.RESULT_OK
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.lauwba.surelabs.mishopcustomer.R
import com.lauwba.surelabs.mishopcustomer.config.Constant
import com.lauwba.surelabs.mishopcustomer.myShop.MyShopActivity
import com.lauwba.surelabs.mishopcustomer.myShop.model.MyShopModel
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.my_shop_activity.*
import kotlinx.android.synthetic.main.my_shop_dashoard_fragment.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.support.v4.alert
import org.jetbrains.anko.support.v4.toast
import org.jetbrains.anko.yesButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MyShopDashoard : Fragment() {

    private var fileUri: Uri? = null
    private var realPath: String? = null
    private var mStorageRef: StorageReference? = null
    private var pd: ProgressDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.my_shop_dashoard_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.posting?.onClick {
            if (realPath.isNullOrEmpty()) {
                toast("Silahkan Pilih 1 Foto Terlebih Dahulu")
            } else if (judul.text.isEmpty()) {
                judul.error = "Judul harus diisi"
            } else if (harga.text.isEmpty()) {
                harga.error = "Harga harus diisi"
            } else if (deskripsiBarang.text.isEmpty()) {
                deskripsiBarang.error = "Deskripsi harus diisi"
            } else if (lokasi.text.isEmpty()) {
                lokasi.error = "Lokasi harus diisi"
            } else {
                val time = Calendar.getInstance()
                val timestamp = time.timeInMillis
                uploadToFirebase(realPath, timestamp.toString(), time.timeInMillis)
            }
        }

        kamera.onClick {
            launchCamera(Constant.CAMERAREQ)
        }

        berkas.onClick {
            showPicker(Constant.FILEREQ, "*/*")
        }

        gallery.onClick {
            showPicker(Constant.GALERYREQ, "image/*")
        }

        lokasi.onClick {
            val intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(activity)
            startActivityForResult(intent, 2)
        }
    }

    private fun showPicker(i: Int, s: String) {
        val picker = Intent(Intent.ACTION_PICK)
        picker.type = s
        startActivityForResult(picker, i)
    }

    private fun launchCamera(camerareq: Int) {
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        val camera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        fileUri = getOutputMediaFileUri(Constant.MEDIA_TYPE_IMAGE)
        camera.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(camera, camerareq)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constant.CAMERAREQ && resultCode == RESULT_OK) {

        } else if (requestCode == Constant.FILEREQ && resultCode == RESULT_OK) {
            realPath = activity?.let { getRealPathFromUri(it, Uri.parse(data?.dataString)) }
            MyShopActivity.realpath = realPath
        } else if (requestCode == Constant.GALERYREQ && resultCode == RESULT_OK) {
            realPath = activity?.let { getRealPathFromUri(it, Uri.parse(data?.dataString)) }
            MyShopActivity.realpath = realPath
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            try {
                val place = PlaceAutocomplete.getPlace(activity, data)
                val name = place.name
                val address = place.address
                lokasi.text = "$name\n$address"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            showPreview(realPath)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showPreview(realPath: String?) {
        activity?.let {
            Glide.with(it)
                .load(realPath)
                .into(imagePreview)
        }
    }

    private fun getRealPathFromUri(context: Context, contentUri: Uri): String {
        var cursor: Cursor? = null
        try {
            val project = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.contentResolver.query(contentUri, project, null, null, null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        } finally {
            cursor?.close()
        }
    }

    private fun getOutputMediaFileUri(type: Int): Uri {
        return Uri.fromFile(getOutputMediaFile(type))
    }

    private fun getOutputMediaFile(type: Int): File? {

        // External sdcard location
        val mediaStorageDir = File(
            Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            Constant.FOLDERANAME
        )

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }

        // Create a media file name
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val mediaFile: File
        if (type == Constant.MEDIA_TYPE_IMAGE) {
            mediaFile = File(
                mediaStorageDir.path + File.separator
                        + "IMG_" + timeStamp + ".jpg"
            )
            realPath = mediaFile.toString()
        } else {
            return null
        }

        return mediaFile
    }

    private fun uploadToFirebase(realPath: String?, idShop: String, time: Long): String {
        pd = ProgressDialog.show(activity, "", "Sedang memposting", false, false)
        mStorageRef = Constant.storage.reference
        var urlDownload = ""
        val file = Uri.fromFile(File(realPath))
        val riversRef = mStorageRef?.child("images/myShop/$idShop/$idShop.jpg")

        riversRef?.putFile(file)
            ?.addOnProgressListener {
                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
                Log.i("PROGRESS", "$progress % done")
            }
            ?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            })?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    urlDownload = task.result.toString()
                    insertDatabase(urlDownload, idShop, time)
                } else {
                }
            }
        return urlDownload
    }

    private fun insertDatabase(urlDownload: String, timestamp: String, time: Long) {
        val myref = Constant.database.getReference(Constant.TB_MYSHOP)
        val myshop = MyShopModel()

        myshop.idMyShop = time
        myshop.image = urlDownload
        myshop.harga = harga.text.toString().toInt()
        myshop.judul = judul.text.toString()
        myshop.lokasi = lokasi.text.toString()
        myshop.tanggalPost = time.toString()
        myshop.deskripsi = deskripsiBarang.text.toString()
        myshop.uid = Prefs.getString(Constant.UID, Constant.mAuth.currentUser?.uid)
        myref.child(timestamp).setValue(myshop).addOnCompleteListener {
            if (it.isSuccessful) {
                pd?.dismiss()
                alert("Berhasil Diposting", "") {
                    yesButton { }
                }.show()
            }
        }
    }
}

