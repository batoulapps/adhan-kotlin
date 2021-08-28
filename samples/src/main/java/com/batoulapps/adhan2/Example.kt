package com.batoulapps.adhan2

import com.batoulapps.adhan2.data.DateComponents
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

object Example {
  private fun Instant.asDate() = Date(toEpochMilliseconds())

  @JvmStatic
  fun main(args: Array<String>) {
    // get prayer times in Makkah
    val coordinates = Coordinates(21.4359571, 39.7064646)
    val dateComponents: DateComponents = DateComponents.from(System.now())
    val parameters: CalculationParameters = CalculationMethod.UMM_AL_QURA.parameters


    val prayerTimes = PrayerTimes(coordinates, dateComponents, parameters)

    val formatter = SimpleDateFormat("hh:mm a")
    formatter.timeZone = TimeZone.getTimeZone("Asia/Riyadh")

    println("Fajr: " + formatter.format(prayerTimes.fajr.asDate()))
    println("Sunrise: " + formatter.format(prayerTimes.sunrise.asDate()))
    println("Dhuhr: " + formatter.format(prayerTimes.dhuhr.asDate()))
    println("Asr: " + formatter.format(prayerTimes.asr.asDate()))
    println("Maghrib: " + formatter.format(prayerTimes.maghrib.asDate()))
    println("Isha: " + formatter.format(prayerTimes.isha.asDate()))
  }
}