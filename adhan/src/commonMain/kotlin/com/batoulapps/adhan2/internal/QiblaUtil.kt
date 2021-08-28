package com.batoulapps.adhan2.internal

import com.batoulapps.adhan2.Coordinates
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.tan

object QiblaUtil {
  private val MAKKAH = Coordinates(21.4225241, 39.8261818)

  fun calculateQiblaDirection(coordinates: Coordinates): Double {
    // Equation from "Spherical Trigonometry For the use of colleges and schools" page 50
    val longitudeDelta: Double = MAKKAH.longitude.toRadians() - coordinates.longitude.toRadians()
    val latitudeRadians: Double = coordinates.latitude.toRadians()
    val term1: Double = sin(longitudeDelta)
    val term2: Double = cos(latitudeRadians) * tan(MAKKAH.latitude.toRadians())

    val term3: Double = sin(latitudeRadians) * cos(longitudeDelta)
    val angle: Double = atan2(term1, term2 - term3)
    return DoubleUtil.unwindAngle(angle.toDegrees())
  }
}