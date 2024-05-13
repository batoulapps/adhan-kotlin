package com.batoulapps.adhan2

import okio.FileSystem

@JsModule("@js-joda/timezone")
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem? = null
  actual fun environmentVariable(name: String): String? = null
}