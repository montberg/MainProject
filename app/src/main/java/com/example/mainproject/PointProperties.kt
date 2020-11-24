package com.example.mainproject

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URL


lateinit var containerList: MutableList<Container>
lateinit var txtTipMusora : AutoCompleteTextView
lateinit var txtObjemKonteinerov : EditText

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
    lateinit var btnAddContainer : Button
    lateinit var btnAddPlatform : Button
    lateinit var containerListView:ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_point_properties)
        findAllElements()
        val baseType = listOf("Бетон", "Плита", "Щебень", "Отсутствует")
        val fenceMat = listOf("Металл", "Профнастил")
        val rubbishType = listOf("ТКО", "Негабарит", "Стекло", "Бумага", "Пищевые отходы")
        val lng = intent.getDoubleExtra(LAT, 0.0)
        val lat = intent.getDoubleExtra(LNG, 0.0)
        val apiKey = "18e0a04c-f1d7-4665-af58-499ad280cd46"
        val geocodeURL = "https://geocode-maps.yandex.ru/1.x/?apikey=$apiKey&format=json&geocode=$lat,$lng"
        val apiResponse = URL(geocodeURL).readText()
        val txtAddressArray = getAddressLines(apiResponse)
        val tipOsnovaniaAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, baseType)
        txtTipOsnovania.setAdapter(tipOsnovaniaAdapter)

        val og: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, fenceMat)
        txtMaterialOgrazhdenia.setAdapter(og)

        val rt: ArrayAdapter<String> = ArrayAdapter<String>(
                this, android.R.layout.simple_dropdown_item_1line, rubbishType)
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

        containerList = arrayListOf()

        val adapter = ContainerAdapter(this)
        containerListView.adapter = adapter
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
            val container = Container(txtTipMusora.text.toString(), txtObjemKonteinerov.text.toString().toDouble())
            containerList.add(container)
            adapter.notifyDataSetChanged()
            } catch (e: Exception){
                checkContainer()
            }
        }

        containerListView.setOnItemClickListener { parent, view, position, id ->
            val builder = androidx.appcompat.app.AlertDialog.Builder(this@PointProperties)
            builder.setMessage("Удалить контейнер?")
                    .setCancelable(true)
                    .setPositiveButton("Да") { _, _ ->
                        containerList.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Нет") { dialog, _ ->
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()
        }


        btnAddPlatform.setOnClickListener {
            try{
            val address = txtAddress.text.toString()
            val latitude = lat
            val longitude = lng
            val baseType = txtTipOsnovania.text.toString()
            val square = txtPloshad.text.toString().toDouble()
            val boolIsIncreaseble = checkUvelichitPloshad.isChecked
            val boolWithRec = checkSReconstrukcjei.isChecked
            val boolWithFence = checkOgrazhdenie.isChecked
            val fenceMat:String? = if(txtMaterialOgrazhdenia.text == null) null else txtMaterialOgrazhdenia.text.toString()
            val containersArray: Array<Container>? = if(containerList.isEmpty()) arrayOf() else containerList.toTypedArray()
            val newPlatform = Platform(0, latitude, longitude, address, baseType, square, boolIsIncreaseble, boolWithRec, boolWithFence, fenceMat, containersArray)
            try{
                insertDataToTable(newPlatform)
                Toast.makeText(this, "Площадка успешно добавлена", Toast.LENGTH_LONG).show()
            } catch (e: Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception){
                checkPlatform()
            }
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
        containerListView = findViewById(R.id.containerListView)
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
    private class ContainerAdapter(ctx: Context) : ArrayAdapter<Container>(ctx, android.R.layout.simple_list_item_1, containerList){
        override fun getView(position: Int, ConvertView: View?, parent: ViewGroup): View {
            var convertView = ConvertView
            val container: Container? = getItem(position)
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null)
            }
            (convertView!!.findViewById<View>(android.R.id.text1) as TextView).text = container!!.RubbishType.toString() +" "+container!!.Volume.toString()
            return convertView
        }
    }
}