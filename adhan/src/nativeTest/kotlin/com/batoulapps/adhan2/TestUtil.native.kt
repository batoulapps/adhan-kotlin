package com.batoulapps.adhan2

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import okio.FileSystem
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem? = FileSystem.SYSTEM
  actual fun environmentVariable(name: String): String? = getenv(name)?.toKString()
}