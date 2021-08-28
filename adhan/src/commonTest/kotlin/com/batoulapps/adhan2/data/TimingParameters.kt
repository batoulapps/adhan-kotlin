package com.batoulapps.adhan2.data

import kotlinx.serialization.Serializable

@Serializable
data class TimingParameters(
  val latitude: Double = 0.0,
  val longitude: Double = 0.0,
  val timezone: String,
  val method: String,
  val madhab: String,
  val highLatitudeRule: String
)