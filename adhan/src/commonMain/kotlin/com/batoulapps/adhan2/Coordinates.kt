package com.batoulapps.adhan2

/**
 * Coordinates representing a particular place
 */
class Coordinates(
  val latitude: Double,
  val longitude: Double
) {

  init {
    require(latitude in -90.0..90.0) { "Latitude must be between -90 and 90 degrees" }
    require(longitude in -180.0..180.0) { "Longitude must be between -180 and 180 degrees" }
  }
}
