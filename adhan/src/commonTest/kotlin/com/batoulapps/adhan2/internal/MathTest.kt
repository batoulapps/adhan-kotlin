package com.batoulapps.adhan.internal

import com.batoulapps.adhan.data.CalendarUtil.roundedMinute
import com.batoulapps.adhan.data.TimeComponents
import com.batoulapps.adhan.internal.DoubleUtil.closestAngle
import com.batoulapps.adhan.internal.DoubleUtil.normalizeWithBound
import com.batoulapps.adhan.internal.DoubleUtil.unwindAngle
import com.batoulapps.adhan.internal.TestUtils.makeDate
import kotlin.math.PI
import kotlin.math.abs
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MathTest {
  private fun Double.isWithin(threshold: Double) = this to threshold
  private fun Pair<Double, Double>.of(value: Double) = abs(first - value) <= second
  private fun Pair<Double, Double>.of(value: Int) = abs(first - value) <= second

  @Test
  fun testAngleConversion() {
    assertTrue((PI.toDegrees()).isWithin(0.00001).of(180.0))
    assertTrue((PI / 2).toDegrees().isWithin(0.00001).of(90.0))
  }

  @Test
  fun testNormalizing() {
    assertTrue(normalizeWithBound(2.0, -5.0).isWithin(0.00001).of(-3))
    assertTrue(normalizeWithBound(-4.0, -5.0).isWithin(0.00001).of(-4))
    assertTrue(normalizeWithBound(-6.0, -5.0).isWithin(0.00001).of(-1))

    assertTrue(normalizeWithBound(-1.0, 24.0).isWithin(0.00001).of(23))
    assertTrue(normalizeWithBound(1.0, 24.0).isWithin(0.00001).of(1))
    assertTrue(normalizeWithBound(49.0, 24.0).isWithin(0.00001).of(1))

    assertTrue(normalizeWithBound(361.0, 360.0).isWithin(0.00001).of(1))
    assertTrue(normalizeWithBound(360.0, 360.0).isWithin(0.00001).of(0))
    assertTrue(normalizeWithBound(259.0, 360.0).isWithin(0.00001).of(259))
    assertTrue(normalizeWithBound(2592.0, 360.0).isWithin(0.00001).of(72))

    assertTrue(unwindAngle(-45.0).isWithin(0.00001).of(315))
    assertTrue(unwindAngle(361.0).isWithin(0.00001).of(1))
    assertTrue(unwindAngle(360.0).isWithin(0.00001).of(0))
    assertTrue(unwindAngle(259.0).isWithin(0.00001).of(259))
    assertTrue(unwindAngle(2592.0).isWithin(0.00001).of(72))

    assertTrue(normalizeWithBound(360.1, 360.0).isWithin(0.01).of(0.1))
  }

  @Test
  fun testClosestAngle() {
    assertTrue(closestAngle(360.0).isWithin(0.000001).of(0))
    assertTrue(closestAngle(361.0).isWithin(0.000001).of(1))
    assertTrue(closestAngle(1.0).isWithin(0.000001).of(1))
    assertTrue(closestAngle(-1.0).isWithin(0.000001).of(-1))
    assertTrue(closestAngle(-181.0).isWithin(0.000001).of(179))
    assertTrue(closestAngle(180.0).isWithin(0.000001).of(180))
    assertTrue(closestAngle(359.0).isWithin(0.000001).of(-1))
    assertTrue(closestAngle(-359.0).isWithin(0.000001).of(1))
    assertTrue(closestAngle(1261.0).isWithin(0.000001).of(-179))
    assertTrue(closestAngle(-360.1).isWithin(0.01).of(-0.1))
  }

  @Test
  fun testTimeComponents() {
    val comps1 = TimeComponents.fromDouble(15.199)
    assertNotNull(comps1)
    assertEquals(15, comps1.hours)
    assertEquals(11, comps1.minutes)
    assertEquals(56, comps1.seconds)

    val comps2 = TimeComponents.fromDouble(1.0084)
    assertNotNull(comps2)
    assertEquals(1, comps2.hours)
    assertEquals(0, comps2.minutes)
    assertEquals(30, comps2.seconds)

    val comps3 = TimeComponents.fromDouble(1.0083)
    assertNotNull(comps3)
    assertEquals(1, comps3.hours)
    assertEquals(0, comps3.minutes)

    val comps4 = TimeComponents.fromDouble(2.1)
    assertNotNull(comps4)
    assertEquals(2, comps4.hours)
    assertEquals(6, comps4.minutes)

    val comps5 = TimeComponents.fromDouble(3.5)
    assertNotNull(comps5)
    assertEquals(3, comps5.hours)
    assertEquals(30, comps5.minutes)
  }

  @Test
  fun testMinuteRounding() {
    val comps1 = makeDate(year = 2015, month = 1, day = 1, hour = 10, minute = 2, second = 29)
    val rounded1 = roundedMinute(comps1)

    assertEquals(2, rounded1.minute)
    assertEquals(0, rounded1.second)

    val comps2 = makeDate(year = 2015, month = 1, day = 1, hour = 10, minute = 2, second = 31)
    val rounded2 = roundedMinute(comps2)

    assertEquals(3, rounded2.minute)
    assertEquals(0, rounded2.second)
  }
}