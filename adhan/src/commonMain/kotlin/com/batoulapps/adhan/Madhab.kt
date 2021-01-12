package com.batoulapps.adhan

import com.batoulapps.adhan.internal.ShadowLength
import com.batoulapps.adhan.internal.ShadowLength.DOUBLE
import com.batoulapps.adhan.internal.ShadowLength.SINGLE

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