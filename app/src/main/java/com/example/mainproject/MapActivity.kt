package com.example.mainproject



import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import java.lang.Exception


open class MapActivity : OptionsMenu(), UserLocationObjectListener, DataBase{
    private val TARGET_LOCATION = Point(45.042529, 38.975963)
    private lateinit var mapview:MapView
    private lateinit var getMyLocation:Button
    private lateinit var userLocationLayer:UserLocationLayer
    private lateinit var mypos:Point
    override fun onCreate(savedInstanceState: Bundle?) {
        MapKitFactory.setApiKey("6f2989af-38b6-4884-b695-e32115108530")
        MapKitFactory.initialize(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        actionBar?.setDisplayShowTitleEnabled(false);
        if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ){
            val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            ActivityCompat.requestPermissions(this, permissions, 0)
        }
        mypos = Point(0.0, 0.0)
        val createPoint: Button = findViewById(R.id.createPoint)
        mapview = findViewById(R.id.map)
        getMyLocation = findViewById(R.id.getMyLocation)
        mapview.map.mapObjects.clear()
//        val platforms = getPlatform()
      //  platforms.forEach{
      //     mapview.map.mapObjects.addPlacemark(Point(it.Lng, it.Lat), ImageProvider.fromResource(this, R.drawable.ic_marker_dumpster))
     //   }
        moveCamera(TARGET_LOCATION, 11F)
        createPoint.setOnClickListener{
            try{
            val lat: Double = mapview.map.cameraPosition.target.latitude
            val lng: Double = mapview.map.cameraPosition.target.longitude
            val goToPointProps = Intent(this, PointProperties::class.java)
            goToPointProps.putExtra(PointProperties.platformLAT, userLocationLayer.cameraPosition()!!.target.latitude)
            goToPointProps.putExtra(PointProperties.platformLNG, userLocationLayer.cameraPosition()!!.target.longitude)
            goToPointProps.putExtra(PointProperties.LAT, lat)
            goToPointProps.putExtra(PointProperties.LNG, lng)
            startActivity(goToPointProps)}
            catch (e:Exception){
                Toast.makeText(this, "Нужно активировать геолокацию", Toast.LENGTH_SHORT).show()
            }
        }
        userLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(mapview.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.setObjectListener(this);
        getMyLocation.setOnClickListener {
            userLocationLayer.cameraPosition()?.target?.let { it1 -> moveCamera(it1, 18F)
            }
        }
    }
    override fun onResume() {
        mapview.map.mapObjects.clear()
   //     ArrayOfPlatforms = getPlatform()
    //    ArrayOfPlatforms.forEach { p ->
    //    mapview.map.mapObjects.addPlacemark(Point(p.Lng, p.Lat), ImageProvider.fromResource(this, R.drawable.ic_marker_dumpster))
    //    }
        super.onResume()
    }
        fun moveCamera(point: Point, zoom: Float){
        mapview.map.move(
                CameraPosition(point, zoom, 0.0f, 0.0f),
                Animation(Animation.Type.LINEAR, 0.5F),
                null
        )
    }
    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.pin.setIcon(ImageProvider.fromResource(this, R.drawable.ic_marker_myposition))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this, R.drawable.ic_marker_myposition))
        userLocationView.accuracyCircle.fillColor = Color.BLUE

    }
    override fun onStop() {
        super.onStop()
        mapview.onStop()
        val point = mapview.map.cameraPosition
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }
    override fun onObjectRemoved(userLocationView: UserLocationView) {}

    override fun onObjectUpdated(userLocationView: UserLocationView, objectEvent: ObjectEvent) {}

}

