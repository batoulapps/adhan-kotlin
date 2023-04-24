package com.batoulapps.adhan2

import okio.FileSystem
import okio.NodeJsFileSystem

@JsModule("@js-joda/timezone")
@JsNonModule
external object JsJodaTimeZoneModule

private val jsJodaTz = JsJodaTimeZoneModule

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = NodeJsFileSystem
  actual fun environmentVariable(name: String): String? = js("globalThis.process.env[name]") as String?
}