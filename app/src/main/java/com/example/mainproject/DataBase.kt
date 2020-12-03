package com.example.mainproject

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.util.*

var URL = "jdbc:firebirdsql://62.33.36.198:4100/c:/data/DATABASE.FDB" //URL до бд в виде jdbc:firebirdsql://<IP-адрес либо сайт>:<порт>/<полный путь до БД>

interface DataBase {
    fun getConnectionProperties():Properties{
        Class.forName("org.firebirdsql.jdbc.FBDriver")
        val props = Properties()
        props.setProperty("user", "SYSDBA")
        props.setProperty("password", "masterkey")
        props.setProperty("encoding", "WIN1251")
        return props
    }

    fun checkUser(login: String, password: String): Boolean {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val query = "select * from users where login='$login'"
        val statement: Statement = connection.createStatement()
        val dataBaseResponse: ResultSet = statement.executeQuery(query)
        while(dataBaseResponse.next()) {
            try {
                if (dataBaseResponse.getString("login") == login && dataBaseResponse.getString("password")  == password) return true
            } catch (e: Exception) {
                return false
            }
        }
        connection.close()
        return false
    }

    fun insertDataToTable(platform: Platform) {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        var lastID = 0
        val statement: Statement = connection.createStatement()
        var strSQL = "INSERT INTO platform(lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount, userlogin, boolwithcanopy, boolkgo) " +
                "VALUES(${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}, '${platform.UserLogin}', ${platform.BoolNaves}, ${platform.BoolKGO}) returning id;"
            val dataBaseResponse: ResultSet = statement.executeQuery(strSQL)
            while(dataBaseResponse.next()) {
                lastID = dataBaseResponse.getInt(1)
            }
        if(platform.Containersarray!!.isNotEmpty()) {
            platform.Containersarray.forEach { c ->
                strSQL = "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
                statement.execute(strSQL)
            }
        }
        if(platform.Base64images!!.isNotEmpty()){
            platform.Base64images.forEach { c ->
                strSQL = "insert into pictures(picture, parent_id) values('${c.joinToString(", ")}', ${lastID});"
                statement.execute(strSQL)
            }
        }
            connection.close()
    }
    fun deletePlatform(id: Int){
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        var deleteQuery = "delete from platform where id = $id"
        statement.execute(deleteQuery)
        deleteQuery = "delete from pictures where parent_id = $id"
        statement.execute(deleteQuery)
        connection.close()
    }
    fun insertDataToTable(platform: Platform, id: Int){
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val lastID:Int = id
        val statement: Statement = connection.createStatement()
        var strSQL = "UPDATE OR INSERT INTO platform(id, lat, lng, address, basetype, square, boolisinc, boolwithrec, boolwithfence, fencemat, containeramount, userlogin, boolwithcanopy, boolkgo) " +
                "VALUES(${lastID}, ${platform.Lat}, ${platform.Lng}, '${platform.Address}', '${platform.BaseType}', ${platform.Square}, ${platform.Boolisincreaseble}," +
                " ${platform.Boolwithrec}, ${platform.Boolwithfence}, '${platform.Fencemat}', ${platform.Containersarray?.size}, '${platform.UserLogin}', ${platform.BoolNaves}, ${platform.BoolKGO}) MATCHING(id);"
        statement.execute(strSQL)
        strSQL = "delete from container where parent_id = $lastID;"
        statement.execute(strSQL)
        platform.Containersarray?.forEach { c ->
            strSQL = "insert into container(rubbishtype, volume, parent_id) values('${c.RubbishType}', ${c.Volume}, ${lastID});"
            statement.execute(strSQL)
        }
        strSQL = "delete from pictures where parent_id = $lastID;"
        statement.execute(strSQL)
        platform.Base64images.forEach { c ->
            strSQL = "insert into pictures(picture, parent_id) values('${c.joinToString(", ")}', ${lastID});"
            statement.execute(strSQL)
        }
        connection.close()
    }
    fun getPlatform(): MutableList<PlatformInfo> {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val getPlatforms = "select id, lat, lng, address from platform"
        val response:ResultSet = statement.executeQuery(getPlatforms)
        val PlatformArray:MutableList<PlatformInfo> = arrayListOf()
        while(response.next()){
            val id = response.getInt(1)
            val Lat = response.getFloat(2)
            val Lng =  response.getFloat(3)
            val Address = response.getString(4)
            val tempPlatform = PlatformInfo(id, Lat.toDouble(), Lng.toDouble(), Address.toString())
            PlatformArray.add(tempPlatform)
        }
        connection.close()

        return PlatformArray
    }
    fun getFullPlatformInfo(id: Int): Platform {
        val connection: Connection = DriverManager.getConnection(URL, getConnectionProperties())
        val statement: Statement = connection.createStatement()
        val getPlatforms = "select * from platform where id = $id"
        val response:ResultSet = statement.executeQuery(getPlatforms)
        while(response.next()){
            val Id = response.getInt(1)
            val Lat = response.getFloat(2)
            val Lng =  response.getFloat(3)
            val Address = response.getString(4)
            val BaseType = response.getString(5)
            val Square = response.getString(6)
            val Boolisincreaseble = response.getBoolean(7)
            val Boolwithrec = response.getBoolean(8)
            val Boolwithfence = response.getBoolean(9)
            val Fencemat = response.getString(10)
            val UserLogin = response.getString(11)
            val Boolwithcanopy = response.getBoolean(13)
            val Boolkgo = response.getBoolean(14)
            val getContainersQuery = "select * from container where parent_id = '${id}'"
            val statement2: Statement = connection.createStatement()
            val containersListResponse = statement2.executeQuery(getContainersQuery)
            val tempContainerList:MutableList<Container> = arrayListOf()
            while(containersListResponse.next()) {
                val rubbishtype = containersListResponse.getString(2)
                val volume = containersListResponse.getString(3)
                val container = Container(rubbishtype, volume.toDouble())
                tempContainerList.add(container)
            }
            val pictures = mutableListOf<ByteArray>()
            val picturesQuery = "select * from pictures where parent_id = '${id}'"
            val statement3: Statement = connection.createStatement()
            val picturesResponse = statement3.executeQuery(picturesQuery)
            while(picturesResponse.next()){
                val platform = picturesResponse.getString(2)
                val imageBase64lists = platform.split(", ")
                val e = mutableListOf<Byte>()
                imageBase64lists.forEach {
                    e.add(it.toByte(10))
                }
                pictures.add(e.toByteArray())
            }
            val tempPlatform = Platform(Id, Lat.toDouble(), Lng.toDouble(), Address.toString(), BaseType.toString(), Square.toDouble(), Boolisincreaseble, Boolwithrec, Boolwithfence, Boolwithcanopy, Boolkgo, Fencemat, tempContainerList, UserLogin.toString(), pictures)
            connection.close()
            return tempPlatform
        }
        return Platform(-1, 0.0, 0.0, "", "", 0.0, false, false, false, false, false, null, mutableListOf(), null)
    }
}