package com.batoulapps.adhan2

import com.batoulapps.adhan2.internal.ShadowLength
import com.batoulapps.adhan2.internal.ShadowLength.DOUBLE
import com.batoulapps.adhan2.internal.ShadowLength.SINGLE

/**
 * Madhab for determining how Asr is calculated
 */
enum class Madhab {
  /**
   * Shafi Madhab
   */
  SHAFI,

  /**
   * Hanafi Madhab
   */
  HANAFI;

  val shadowLength: ShadowLength
    get() = when (this) {
      SHAFI -> SINGLE
      HANAFI -> DOUBLE
    }
}