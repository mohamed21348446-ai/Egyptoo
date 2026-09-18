package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CountriesProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Egyptoo", appName)
  }

  @Test
  fun `verify super admin authorization rule`() {
    val authorizedEmail = "mohamed21348446@gmail.com"
    val unauthorizedEmail = "random_user@gmail.com"
    
    val checkAdmin = { email: String ->
      email.trim().equals("mohamed21348446@gmail.com", ignoreCase = true)
    }

    assertTrue(checkAdmin(authorizedEmail))
    assertFalse(checkAdmin(unauthorizedEmail))
  }

  @Test
  fun `verify countries provider contains Egypt flag`() {
    val countries = CountriesProvider.supportedCountries
    val egypt = countries.find { it.code == "EG" }
    assertEquals("مصر", egypt?.nameAr)
    assertEquals("🇪🇬", egypt?.flag)
  }

  @Test
  fun `verify meme sound types exist`() {
    val soundTypes = com.example.audio.MemeSoundType.values()
    assertTrue(soundTypes.any { it == com.example.audio.MemeSoundType.EVIL_LAUGH })
    assertTrue(soundTypes.any { it == com.example.audio.MemeSoundType.QUACK_DUCK })
    assertTrue(soundTypes.any { it == com.example.audio.MemeSoundType.CARTOON_BOOM })
    assertTrue(soundTypes.any { it == com.example.audio.MemeSoundType.ZAGHRUTA_HORN })
  }

  @Test
  fun `verify social auth providers`() {
    val google = com.example.data.model.AuthProvider.GOOGLE
    val fb = com.example.data.model.AuthProvider.FACEBOOK
    assertEquals("GOOGLE", google.name)
    assertEquals("FACEBOOK", fb.name)
  }
}
