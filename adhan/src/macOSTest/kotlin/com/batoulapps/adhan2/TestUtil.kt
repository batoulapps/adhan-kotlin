package com.batoulapps.adhan2

import okio.ExperimentalFileSystem
import okio.FileSystem

@ExperimentalFileSystem
actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
}