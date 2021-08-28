package com.batoulapps.adhan.data

import kotlinx.serialization.Serializable

@Serializable
data class TimingInfo(
  val date: String,
  val fajr: String,
  val sunrise: String,
  val dhuhr: String,
  val asr: String,
  val maghrib: String,
  val isha: String
)