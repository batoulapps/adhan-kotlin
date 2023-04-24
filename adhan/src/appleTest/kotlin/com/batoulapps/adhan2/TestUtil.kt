package com.batoulapps.adhan2

import kotlinx.cinterop.toKString
import okio.FileSystem
import platform.posix.getenv

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
  actual fun environmentVariable(name: String): String? = getenv(name)?.toKString()
}