package com.example

import com.example.crypto.CryptoManager
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun crypto_safetyNumberGeneration_is60DigitsIn12Blocks() {
    val blocks = CryptoManager.generateSafetyNumber("room_test_123")
    assertEquals(12, blocks.size)
    blocks.forEach { block ->
      assertEquals(5, block.length)
      assertTrue(block.all { it.isDigit() })
    }
  }

  @Test
  fun crypto_fingerprint_isNotEmpty() {
    val fp = CryptoManager.getHexFingerprint("room_test_123")
    assertTrue(fp.isNotEmpty())
    assertTrue(fp.contains(":"))
  }
}
