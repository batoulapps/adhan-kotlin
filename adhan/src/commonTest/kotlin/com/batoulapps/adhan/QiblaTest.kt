package com.batoulapps.adhan

import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertTrue

class QiblaTest {
  private fun Double.isWithin(threshold: Double) = this to threshold
  private fun Pair<Double, Double>.of(value: Double) = abs(first - value) <= second

  @Test
  fun testNorthAmerica() {
    val washingtonDC = Coordinates(38.9072, -77.0369)
    assertTrue(Qibla(washingtonDC).direction.isWithin(0.001).of(56.560))
    val nyc = Coordinates(40.7128, -74.0059)
    assertTrue(Qibla(nyc).direction.isWithin(0.001).of(58.481))
    val sanFrancisco = Coordinates(37.7749, -122.4194)
    assertTrue(Qibla(sanFrancisco).direction.isWithin(0.001).of(18.843))
    val anchorage = Coordinates(61.2181, -149.9003)
    assertTrue(Qibla(anchorage).direction.isWithin(0.001).of(350.883))
  }

  @Test
  fun testSouthPacific() {
    val sydney = Coordinates(-33.8688, 151.2093)
    assertTrue(Qibla(sydney).direction.isWithin(0.001).of(277.499))
    val auckland = Coordinates(-36.8485, 174.7633)
    assertTrue(Qibla(auckland).direction.isWithin(0.001).of(261.197))
  }

  @Test
  fun testEurope() {
    val london = Coordinates(51.5074, -0.1278)
    assertTrue(Qibla(london).direction.isWithin(0.001).of(118.987))
    val paris = Coordinates(48.8566, 2.3522)
    assertTrue(Qibla(paris).direction.isWithin(0.001).of(119.163))
    val oslo = Coordinates(59.9139, 10.7522)
    assertTrue(Qibla(oslo).direction.isWithin(0.001).of(139.027))
  }

  @Test
  fun testAsia() {
    val islamabad = Coordinates(33.7294, 73.0931)
    assertTrue(Qibla(islamabad).direction.isWithin(0.001).of(255.882))
    val tokyo = Coordinates(35.6895, 139.6917)
    assertTrue(Qibla(tokyo).direction.isWithin(0.001).of(293.021))
  }

  @Test
  fun testAfrica() {
    val capeTown = Coordinates(33.9249, 18.4241)
    assertTrue(Qibla(capeTown).direction.isWithin(0.001).of(118.004))
    val cairo = Coordinates(30.0444, 31.2357)
    assertTrue(Qibla(cairo).direction.isWithin(0.001).of(136.137))
  }
}