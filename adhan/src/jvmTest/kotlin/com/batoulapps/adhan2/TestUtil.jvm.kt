package com.batoulapps.adhan2

import okio.FileSystem

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem? = FileSystem.SYSTEM
  actual fun environmentVariable(name: String): String? = System.getenv(name)
}