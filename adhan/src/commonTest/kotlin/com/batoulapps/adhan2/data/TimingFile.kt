package com.batoulapps.adhan2.data

import kotlinx.serialization.Serializable

@Serializable
data class TimingFile(
  val params: TimingParameters,
  val times: List<TimingInfo>,
  val variance: Long = 0
)