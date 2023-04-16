package com.batoulapps.adhan2

import okio.FileSystem
import okio.NodeJsFileSystem

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = NodeJsFileSystem
}