package com.example.mainproject

data class Res(val response: Response)
data class Response(val GeoObjectCollection: GeoObjectCollection)
data class GeoObjectCollection(val featureMember: List<FeatureMember>)
data class FeatureMember(val AddressLine: String)