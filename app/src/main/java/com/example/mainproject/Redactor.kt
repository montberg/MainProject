package com.example.mainproject

import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URL

lateinit var listofcontainers: MutableList<Container>
class Redactor : AppCompatActivity(), DataBase {
    lateinit var txtTipMusora : AutoCompleteTextView
    lateinit var txtObjemKonteinerov : EditText
    lateinit var txtAddress: AutoCompleteTextView
    lateinit var txtTipOsnovania : AutoCompleteTextView
    lateinit var txtPloshad : EditText
    lateinit var checkUvelichitPloshad : CheckBox
    lateinit var checkSReconstrukcjei : CheckBox
    lateinit var checkOgrazhdenie : CheckBox
    lateinit var txtMaterialOgrazhdenia : AutoCompleteTextView
    lateinit var btnAddContainer : Button
    lateinit var btnCommitChanges : Button
    lateinit var containerListView: ListView
    lateinit var showOnMap: Button
    lateinit var deletePlatform:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_redactor)
        findAllElements()
        val platform = intent.getSerializableExtra("Platform") as Platform
        listofcontainers = platform.Containersarray?.toMutableList()!!
        txtAddress.setText(platform.Address)
        txtTipOsnovania.setText(platform.BaseType)
        txtPloshad.setText(platform.Square.toString())
        checkUvelichitPloshad.isChecked = platform.Boolisincreaseble
        checkSReconstrukcjei.isChecked = platform.Boolwithrec
        checkOgrazhdenie.isChecked = platform.Boolwithfence
        txtMaterialOgrazhdenia.setText(platform.Fencemat)
        val adapter = ContainerAdapter(this)
        containerListView.adapter = adapter
        if(checkUvelichitPloshad.isChecked) checkSReconstrukcjei.visibility = View.VISIBLE
        else checkSReconstrukcjei.visibility = View.GONE
        if(checkOgrazhdenie.isChecked) txtMaterialOgrazhdenia.visibility = View.VISIBLE
        else txtMaterialOgrazhdenia.visibility = View.GONE
        checkUvelichitPloshad.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) showHide(checkSReconstrukcjei)
            else showHide(checkSReconstrukcjei)
            checkSReconstrukcjei.isChecked = false
        }
        checkOgrazhdenie.setOnCheckedChangeListener{ _, isChecked ->
            if(isChecked) showHide(txtMaterialOgrazhdenia)
            else showHide(txtMaterialOgrazhdenia)
            txtMaterialOgrazhdenia.text = null
        }
        containerListView.setOnItemClickListener { _, _, position, _ ->
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setMessage("Удалить контейнер?")
                    .setCancelable(true)
                    .setPositiveButton("Да") { _, _ ->
                        listofcontainers.removeAt(position)
                        adapter.notifyDataSetChanged()
                    }
                    .setNegativeButton("Нет") { dialog, _ ->
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()
        }
        deletePlatform.setOnClickListener {
            try{
                val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                builder.setMessage("Вы уверены, что хотите удалить площадку?")
                        .setCancelable(true)
                        .setPositiveButton("Да") { _, _ ->
                            deletePlatform(platform.Id)
                            finish()
                        }
                        .setNegativeButton("Нет") { dialog, _ ->
                            dialog.dismiss()
                        }
                val alert = builder.create()
                alert.show()
                true
            }catch (e:Exception){
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
                false
            }
        }
        btnCommitChanges.setOnClickListener {
            val address = txtAddress.text.toString()
            val baseType = txtTipOsnovania.text.toString()
            val square = txtPloshad.text.toString().toDouble()
            val boolIsIncreaseble = checkUvelichitPloshad.isChecked
            val boolWithRec = checkSReconstrukcjei.isChecked
            val boolWithFence = checkOgrazhdenie.isChecked
            val fenceMat:String? = if(txtMaterialOgrazhdenia.text == null) null else txtMaterialOgrazhdenia.text.toString()
            val containersArray: Array<Container>? = if(listofcontainers.isEmpty()) arrayOf() else listofcontainers.toTypedArray()
            val lat:Double
            val lng:Double
            if(newPos!=null){
                lat = newPos!!.latitude
                lng = newPos!!.longitude

            }else{
                lat = platform.Lat
                lng = platform.Lng
            }
            val newPlatform = Platform(platform.Id, lat, lng, address, baseType, square, boolIsIncreaseble, boolWithRec, boolWithFence, fenceMat, containersArray)
            try{
                insertDataToTable(newPlatform, platform.Id)
                Toast.makeText(this, "Площадка успешно изменена", Toast.LENGTH_LONG).show()
            }
            catch (e: Exception){
                Toast.makeText(this, "Что-то пошло не так: $e", Toast.LENGTH_LONG).show()
            }
            finally {
                newPos = null
            }
        }
        btnAddContainer.setOnClickListener {
            val container = Container(txtTipMusora.text.toString(), txtObjemKonteinerov.text.toString().toDouble())
            listofcontainers.add(container)
            adapter.notifyDataSetChanged()
        }
        showOnMap.setOnClickListener {
            val i = Intent(this, ShowContainerOnMap::class.java)
            i.putExtra("lat", platform.Lat)
            i.putExtra("lng", platform.Lng)
            startActivity(i)
        }
    }
    class ContainerAdapter(ctx: Context) : ArrayAdapter<Container>(ctx, android.R.layout.simple_list_item_2, listofcontainers){
        override fun getView(position: Int, ConvertView: View?, parent: ViewGroup): View {
            var convertView = ConvertView
            val container: Container? = getItem(position)
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, null)
            }
            (convertView!!.findViewById<View>(android.R.id.text1) as TextView).text = container!!.RubbishType
            (convertView.findViewById<View>(android.R.id.text2) as TextView).text = container.Volume.toString()
            return convertView
        }
    }
    private fun showHide(view: View) {
        if (view.visibility == View.VISIBLE){
            view.visibility = View.GONE
        } else{
            view.visibility = View.VISIBLE
        }
    }
    private fun findAllElements(){
        txtAddress = findViewById(R.id.chooseAddress)
        showOnMap = findViewById(R.id.showOnMap)
        txtTipOsnovania = findViewById(R.id.autoTipOsnovania)
        txtPloshad = findViewById(R.id.txtPloshad)
        checkUvelichitPloshad = findViewById(R.id.checkUvelichitPloshad)
        checkSReconstrukcjei = findViewById(R.id.checkSReconstrukcjei)
        checkOgrazhdenie = findViewById(R.id.checkOgrazhdenie)
        txtMaterialOgrazhdenia = findViewById(R.id.autoOgrazhdenie)
        txtTipMusora = findViewById(R.id.autoTipMusora)
        txtObjemKonteinerov = findViewById(R.id.autoObjemKonteinerov)
        btnAddContainer = findViewById(R.id.addContainer)
        btnCommitChanges = findViewById(R.id.commitChanges)
        containerListView = findViewById(R.id.containerListView)
        deletePlatform = findViewById(R.id.deletePlatform)
    }
    private fun getAddressLines(RESPONSE: String) : List<String> {
        val jsonObject: JsonObject = Gson().fromJson(RESPONSE, JsonObject::class.java)
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
    override fun onResume(){
        if(newPos != null){
            val apiKey = "18e0a04c-f1d7-4665-af58-499ad280cd46"
            val geocodeURL = "https://geocode-maps.yandex.ru/1.x/?apikey=$apiKey&format=json&geocode=${newPos!!.latitude},${newPos!!.longitude}"
            val apiResponse = URL(geocodeURL).readText()
            val txtAddressArray = getAddressLines(apiResponse)
            txtAddress.setText(txtAddressArray[0])
        }
        super.onResume()
    }
}