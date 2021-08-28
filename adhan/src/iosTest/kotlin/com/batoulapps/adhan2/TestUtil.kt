package com.batoulapps.adhan

import okio.ExperimentalFileSystem
import okio.FileSystem

@ExperimentalFileSystem
actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
}