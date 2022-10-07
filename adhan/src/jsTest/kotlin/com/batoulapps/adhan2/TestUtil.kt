package com.batoulapps.adhan2

import okio.FileSystem
import okio.NodeJsFileSystem

actual class TestUtil actual constructor() {
  actual fun fileSystem(): FileSystem = NodeJsFileSystem
}