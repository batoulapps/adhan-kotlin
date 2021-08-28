package com.batoulapps.adhan

import okio.ExperimentalFileSystem
import okio.FileSystem

@ExperimentalFileSystem
expect class TestUtil() {
  fun fileSystem(): FileSystem
}