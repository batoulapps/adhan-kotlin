package com.batoulapps.adhan2

import okio.ExperimentalFileSystem
import okio.FileSystem

@ExperimentalFileSystem
expect class TestUtil() {
  fun fileSystem(): FileSystem
}