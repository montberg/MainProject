package com.example.mainproject

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URL


class PointProperties : AppCompatActivity(), DataBase{
    companion object {
        const val LAT = "lat"
        const val LNG = "lng"
    }
    lateinit var txtAddress: AutoCompleteTextView
    lateinit var txtTipOsnovania : AutoCompleteTextView
    lateinit var txtPloshad : EditText
    lateinit var checkUvelichitPloshad : CheckBox
    lateinit var checkSReconstrukcjei : CheckBox
    lateinit var checkOgrazhdenie : CheckBox
    lateinit var txtMaterialOgrazhdenia : AutoCompleteTextView
    lateinit var txtKolvoKonteinerov : EditText
    lateinit var txtTipMusora : AutoCompleteTextView
    lateinit var txtObjemKonteinerov : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_properties)
        findAllElements()
        val lng = intent.getDoubleExtra(LAT, 0.0)
        val lat = intent.getDoubleExtra(LNG, 0.0)
        val apiKey = "18e0a04c-f1d7-4665-af58-499ad280cd46"
        val geocodeURL = "https://geocode-maps.yandex.ru/1.x/?apikey=$apiKey&format=json&geocode=$lat,$lng"
        val apiResponse = URL(geocodeURL).readText()
        val txtAddressArray = getAddressLines(apiResponse)
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


    }
    fun findAllElements(){
        txtAddress = findViewById(R.id.chooseAddress)
        txtAddress = findViewById(R.id.chooseAddress)
        txtTipOsnovania = findViewById(R.id.autoTipOsnovania)
        txtPloshad = findViewById(R.id.txtPloshad)
        checkUvelichitPloshad = findViewById(R.id.checkUvelichitPloshad)
        checkSReconstrukcjei = findViewById(R.id.checkSReconstrukcjei)
        checkOgrazhdenie = findViewById(R.id.checkOgrazhdenie)
        txtMaterialOgrazhdenia = findViewById(R.id.autoOgrazhdenie)
        txtKolvoKonteinerov = findViewById(R.id.txtKolvoKonteinerov)
        txtTipMusora = findViewById(R.id.autoTipMusora)
        txtObjemKonteinerov = findViewById(R.id.autoObjemKonteinerov)
    }
    fun showHide(view: View) {
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
