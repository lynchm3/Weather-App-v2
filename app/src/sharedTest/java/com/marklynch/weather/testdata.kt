package com.marklynch.weather

import com.marklynch.weather.di.testWeatherResponse
import com.marklynch.weather.model.WeatherResponse
import com.marklynch.weather.utils.randomAlphaNumeric
import com.squareup.moshi.Moshi
import kotlin.random.Random

var testLon = Random.nextDouble(-180.0, 180.0)
var testLat = Random.nextDouble(-90.0, 90.0)
var testLocationName = randomAlphaNumeric(5)
var testDescription = randomAlphaNumeric(5)
var testTemperature = Random.nextDouble(253.0, 323.0)
var testHumidity = Random.nextDouble(0.0, 100.0)
var testTemperatureMin = Random.nextDouble(253.0, 323.0)
var testTemperatureMax = Random.nextDouble(253.0, 323.0)
var testWindSpeed = Random.nextDouble(0.0, 100.0)
var testWindDeg = Random.nextDouble(0.0, 359.0)
var testCloudiness = Random.nextDouble(0.0, 100.0)

fun randomiseTestWeatherData() {
    testLon = Random.nextDouble(-180.0, 180.0)
    testLat = Random.nextDouble(-90.0, 90.0)
    testLocationName = randomAlphaNumeric(5)
    testDescription = randomAlphaNumeric(5)
    testTemperature = Random.nextDouble(253.0, 323.0)
    testHumidity = Random.nextDouble(0.0, 100.0)
    testTemperatureMin = Random.nextDouble(253.0, 323.0)
    testTemperatureMax = Random.nextDouble(253.0, 323.0)
    testWindSpeed = Random.nextDouble(0.0, 100.0)
    testWindDeg = Random.nextDouble(0.0, 359.0)
    testCloudiness = Random.nextDouble(0.0, 100.0)
    testWeatherResponse = generateGetWeatherResponse()
    println("randomiseTestWeatherData() - testWeatherResponse = $testWeatherResponse")
}




fun generateGetWeatherResponse(): WeatherResponse? = Moshi.Builder().build().adapter(
    WeatherResponse::class.java).fromJson(
    """{
   "coord":{
      "lon":$testLon,
      "lat":$testLat
   },
   "weather":[
      {
         "id":800,
         "main":"Clear",
         "description":"$testDescription",
         "icon":"01d"
      }
   ],
   "base":"stations",
   "main":{
      "temp":$testTemperature,
      "pressure":1017,
	  "humidity":$testHumidity,
	  "temp_min":$testTemperatureMin,
	  "temp_max":$testTemperatureMax
   },
   "visibility":16093,
   "wind":{
	  "speed":$testWindSpeed,
	  "deg":$testWindDeg
   },
   "clouds":{
      "all":$testCloudiness
   },
   "dt":1563212122,
   "sys":{
      "type":1,
      "id":5122,
      "message":0.0134,
      "country":"US",
      "sunrise":1563195547,
      "sunset":1563247741
   },
   "timezone":-25200,
   "id":5375480,
   "name":"$testLocationName",
   "cod":200
}""")