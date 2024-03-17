package com.batoulapps.adhan2

import okio.FileSystem

expect class TestUtil() {
  fun fileSystem(): FileSystem?
  fun environmentVariable(name: String): String?
}