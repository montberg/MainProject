package com.example.mainproject

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.*
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import id.zelory.compressor.Compressor
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.util.*


lateinit var containerList: MutableList<Container>
lateinit var txtTipMusora : AutoCompleteTextView
lateinit var txtObjemKonteinerov : EditText

class PointProperties : AppCompatActivity(), DataBase{
    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
        const val platformLAT = "platformlat"
        const val platformLNG = "platformlng"
        const val userLogin = "userLogin"
    }
    private var PREFERENCES_NAME = "loginPrefs"
    private val FILE_NAME = "photo.jpg"
    private lateinit var photoFile: File
    private var REQUEST_CODE = 1
    lateinit var txtAddress: AutoCompleteTextView
    lateinit var txtTipOsnovania : AutoCompleteTextView
    lateinit var txtPloshad : EditText
    lateinit var checkUvelichitPloshad : CheckBox
    lateinit var checkSReconstrukcjei : CheckBox
    lateinit var checkOgrazhdenie : CheckBox
    lateinit var txtMaterialOgrazhdenia : AutoCompleteTextView
    lateinit var btnAddContainer : Button
    lateinit var btnAddPlatform : Button
    lateinit var btnAddPhoto:Button
    lateinit var imageList:MutableList<Bitmap>
    lateinit var picList:RecyclerView
    lateinit var mAdapter:PictureListAdapter
    lateinit var adapter:ContainerAdapter
    lateinit var imageBase64list:MutableList<String>
    lateinit var checkNaves:CheckBox
    lateinit var checkKGO:CheckBox
    lateinit var spisokkont:TextView
    lateinit var photografii:TextView
    @SuppressLint("QueryPermissionsNeeded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_properties)
        findAllElements()
        imageBase64list = mutableListOf()
        imageList = mutableListOf()
        val recyclerView = findViewById<RecyclerView>(R.id.picList)
        mAdapter = PictureListAdapter(imageList, imageBase64list)
        val mLayoutManager = LinearLayoutManager(applicationContext)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        recyclerView.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = mAdapter

        containerList = arrayListOf()
        val containerListView = findViewById<RecyclerView>(R.id.containerListView)

        adapter = ContainerAdapter(containerList)
        val cLayoutManager =LinearLayoutManager(applicationContext)
        cLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        containerListView.layoutManager = cLayoutManager
        containerListView.itemAnimator = DefaultItemAnimator()
        containerListView.adapter = adapter

        val baseType = listOf("Бетон", "Плита", "Щебень", "Отсутствует")
        val fenceMat = listOf("Металл", "Профнастил")
        val rubbishType = listOf("ТКО", "Негабарит", "Стекло", "Бумага", "Пищевые отходы")
        val lng = intent.getDoubleExtra(LAT, 0.0)
        val lat = intent.getDoubleExtra(LNG, 0.0)
        val platformlng = intent.getDoubleExtra(platformLAT, 0.0)
        val platformlat = intent.getDoubleExtra(platformLNG, 0.0)
        val prefs = getSharedPreferences("loginPrefs", MODE_PRIVATE)
        val login = prefs.getString("login", null)
        val apiKey = "18e0a04c-f1d7-4665-af58-499ad280cd46"
        val geocodeURL = "https://geocode-maps.yandex.ru/1.x/?apikey=$apiKey&format=json&geocode=$lat,$lng"
        val apiResponse = URL(geocodeURL).readText()
        val txtAddressArray = getAddressLines(apiResponse)
        val tipOsnovaniaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, baseType
        )
        txtTipOsnovania.setAdapter(tipOsnovaniaAdapter)

        val og: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, fenceMat
        )
        txtMaterialOgrazhdenia.setAdapter(og)

        val rt: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_dropdown_item_1line, rubbishType
        )
        txtTipMusora.setAdapter(rt)
        txtTipOsnovania.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtTipOsnovania.showDropDown()
        }
        txtTipOsnovania.setOnClickListener {
            txtTipOsnovania.showDropDown()
        }
        txtMaterialOgrazhdenia.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtMaterialOgrazhdenia.showDropDown()
        }
        txtMaterialOgrazhdenia.setOnClickListener {
            txtMaterialOgrazhdenia.showDropDown()
        }

        txtTipMusora.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtTipMusora.showDropDown()
        }
        txtTipMusora.setOnClickListener {
            txtTipMusora.showDropDown()
        }



        val txtAddressAdapter = ArrayAdapter(this, R.layout.autocomplete_layout, txtAddressArray)
        txtAddressAdapter.setDropDownViewResource(R.layout.autocomplete_layout)
        txtAddress.setAdapter(txtAddressAdapter)
        txtAddress.setText(txtAddressArray[0])
        txtAddress.isSelected = true
        txtAddress.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) txtAddress.showDropDown()
        }
        txtAddress.setOnClickListener {
            txtAddress.showDropDown()
        }
        val act = this@PointProperties
        checkUvelichitPloshad.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) showHide(checkSReconstrukcjei)
            else showHide(checkSReconstrukcjei)
        }
        checkOgrazhdenie.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) showHide(txtMaterialOgrazhdenia)
            else showHide(txtMaterialOgrazhdenia)
        }
        btnAddContainer.setOnClickListener {
            try{
            val container = Container(
                txtTipMusora.text.toString(),
                txtObjemKonteinerov.text.toString().toDouble()
            )
            containerList.add(container)
            adapter.notifyDataSetChanged()
            } catch (e: Exception){
                checkContainer()
            }
        }

        btnAddPhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = getPhotoFile(FILE_NAME)
            val fileProvider = FileProvider.getUriForFile(
                this,
                "com.example.mainproject.fileprovider",
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            if(takePictureIntent.resolveActivity(this.packageManager)!= null){
                startActivityForResult(takePictureIntent, REQUEST_CODE)
            }else{
                Toast.makeText(this, "Невозможно открыть камеру", Toast.LENGTH_SHORT).show()
            }
        }

        btnAddPlatform.setOnClickListener {
            btnAddPlatform.isClickable = false
            btnAddPlatform.isEnabled = false
            btnAddPlatform.setBackgroundResource(R.drawable.loginbutton2state)

            val address = txtAddress.text.toString()
            val latitude = platformlat
            val longitude = platformlng
            val baseType = txtTipOsnovania.text.toString()
            val square = txtPloshad.text.toString().toDouble()
            val boolIsIncreaseble = checkUvelichitPloshad.isChecked
            val boolWithRec = checkSReconstrukcjei.isChecked
            val boolWithFence = checkOgrazhdenie.isChecked
            val boolNaves = checkNaves.isChecked
            val boolKGO = checkKGO.isChecked
            val fenceMat:String? = if(txtMaterialOgrazhdenia.text == null) null else txtMaterialOgrazhdenia.text.toString()
            val containersArray: MutableList<Container>? = if(containerList.isEmpty()) mutableListOf() else containerList
            val newPlatform = Platform(
                0,
                latitude,
                longitude,
                address,
                baseType,
                square,
                boolIsIncreaseble,
                boolWithRec,
                boolWithFence,
                boolNaves,
                boolKGO,
                fenceMat,
                containersArray,
                login,
                imageBase64list
            )
            try{
                if(containersArray.isNullOrEmpty()){
                    spisokkont.text = "Добавьте контейнеры"
                    spisokkont.setTextColor(resources.getColor(R.color.red))
                }
                else if(imageBase64list.isNullOrEmpty()){
                    photografii.text = "Приложите фото"
                    photografii.setTextColor(resources.getColor(R.color.red))
                }
                else {

                GlobalScope.launch {
                    insertDataToTable(newPlatform)
                    while (this.isActive){
                    }
                }.start()
                Toast.makeText(this, "Площадка успешно добавлена", Toast.LENGTH_LONG).show()
                finish()
                }
            } catch (e: Exception){
                btnAddPlatform.isEnabled = true
                btnAddPlatform.setBackgroundResource(R.drawable.loginbutton)
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
                var takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                takenImage = Bitmap.createScaledBitmap(
                        takenImage,
                        (takenImage.width) / 4,
                        (takenImage.height) / 4,
                        true
                )
                val stream = ByteArrayOutputStream()
                takenImage.compress(Bitmap.CompressFormat.JPEG, 15, stream)
                val bitmapBytearray = stream.toByteArray()
                val base64 = Base64.getEncoder().encodeToString(bitmapBytearray)

                imageBase64list.add(base64)
                imageList.add(takenImage)
                mAdapter.notifyDataSetChanged()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
    private fun Bitmap.toByteArray():ByteArray{
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG, 10, this)
            return toByteArray()
        }
    }
    private fun checkContainer(){
        if(txtTipMusora.text.isEmpty()) txtTipMusora.error = "Не заполнено"
        if(txtObjemKonteinerov.text.isEmpty()) txtObjemKonteinerov.error = "Не заполнено"
    }
    private fun checkPlatform(){
        if(txtAddress.text.isEmpty()) txtAddress.error = "Не заполнено"
        if(txtTipOsnovania.text.isEmpty()) txtTipOsnovania.error = "Не заполнено"
        if(checkOgrazhdenie.isChecked){
            if(txtMaterialOgrazhdenia.text.isEmpty()) txtMaterialOgrazhdenia.error = "Не заполнено"
        }
        if(txtPloshad.text.isEmpty()) txtPloshad.error = "Не заполнено"
        if(txtMaterialOgrazhdenia.text.isEmpty()) txtAddress.error = "Не заполнено"
    }

    private fun findAllElements(){
        btnAddPhoto = findViewById(R.id.btnAddPhoto)
        picList = findViewById(R.id.picList)
        btnAddPlatform = findViewById(R.id.addPlatform)
        txtAddress = findViewById(R.id.chooseAddress)
        txtAddress = findViewById(R.id.chooseAddress)
        txtTipOsnovania = findViewById(R.id.autoTipOsnovania)
        txtPloshad = findViewById(R.id.txtPloshad)
        checkUvelichitPloshad = findViewById(R.id.checkUvelichitPloshad)
        checkSReconstrukcjei = findViewById(R.id.checkSReconstrukcjei)
        checkOgrazhdenie = findViewById(R.id.checkOgrazhdenie)
        txtMaterialOgrazhdenia = findViewById(R.id.autoOgrazhdenie)
        txtTipMusora = findViewById(R.id.autoTipMusora)
        txtObjemKonteinerov = findViewById(R.id.autoObjemKonteinerov)
        btnAddContainer = findViewById(R.id.addContainer)
        checkKGO = findViewById(R.id.checkKGO)
        checkNaves = findViewById(R.id.checkNaves)
        spisokkont = findViewById(R.id.spisokkont)
        photografii = findViewById(R.id.photografii)
    }

    private fun showHide(view: View) {
        if (view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        } else{
            view.visibility = View.VISIBLE
        }
    }
    private fun getAddressLines(RESPONSE: String) : List<String> {
        val jsonObject:JsonObject = Gson().fromJson(RESPONSE, JsonObject::class.java)
        val response = jsonObject.getAsJsonObject("response")
        val geoObjectCollection = response.getAsJsonObject("GeoObjectCollection")
        val featureMember = geoObjectCollection.getAsJsonArray("featureMember")
        return featureMember.map { jsonElement ->
            val memberChild = jsonElement.asJsonObject
            val geoObject = memberChild.getAsJsonObject("GeoObject")
            val metaData = geoObject.getAsJsonObject("metaDataProperty")
            val geocoder = metaData.getAsJsonObject("GeocoderMetaData")
            val addressDetails = geocoder.getAsJsonObject("AddressDetails")
            val country = addressDetails.getAsJsonObject("Country")
            country.get("AddressLine").asString
        }
    }
}