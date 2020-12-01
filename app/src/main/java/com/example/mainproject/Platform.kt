package com.example.mainproject

import java.io.Serializable
import java.sql.Blob

class Platform(id:Int,
               lat:Double,
               lng:Double,
               address:String,
               baseType:String,
               square:Double,
               boolIsIncreaseble:Boolean = false,
               boolWithRec:Boolean = false,
               boolWithFence:Boolean = false,
               fenceMat:String? = null,
               containersArray:MutableList<Container>? = mutableListOf(),
               userLogin:String?,
               base64images: String? = null
): Serializable {
    val Id = id
    val Lat = lat
    val Lng = lng
    val Address = address
    val BaseType = baseType
    val Square = square
    val Boolisincreaseble = boolIsIncreaseble
    val Boolwithrec = boolWithRec
    val Boolwithfence = boolWithFence
    val Fencemat = fenceMat
    val Containersarray = containersArray
    val UserLogin = userLogin
    val Base64images = base64images
}