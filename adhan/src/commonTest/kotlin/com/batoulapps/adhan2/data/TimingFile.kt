package com.batoulapps.adhan.data

import kotlinx.serialization.Serializable

@Serializable
data class TimingFile(
  val params: TimingParameters,
  val times: List<TimingInfo>,
  val variance: Long = 0
)