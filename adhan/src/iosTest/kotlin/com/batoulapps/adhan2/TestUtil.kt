package com.batoulapps.adhan2

import okio.FileSystem

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
}