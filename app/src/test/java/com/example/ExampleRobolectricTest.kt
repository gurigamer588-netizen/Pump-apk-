package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FuelType
import com.example.data.model.NozzleReading
import com.example.util.DensityCalculatorHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("PetrolPump Pro", appName)
  }

  @Test
  fun `test density calculator at 15C for petrol`() {
    val converted = DensityCalculatorHelper.calculateDensityAt15C(
      observedDensity = 735.0,
      observedTemp = 29.5,
      fuelType = FuelType.PETROL
    )
    assertTrue("Density at 15C should be greater than observed when temp > 15C", converted > 735.0)

    val tolerance = DensityCalculatorHelper.checkTolerance(converted, 744.0)
    assertTrue("Should be permissible within +/- 3.0 kg/m3", tolerance.isPermissible)
  }

  @Test
  fun `test nozzle reading calculation`() {
    val nozzle = NozzleReading(
      nozzleName = "Nozzle 1",
      fuelType = FuelType.PETROL,
      openingReading = 1000.0,
      closingReading = 1500.0,
      testingLitres = 5.0
    )
    assertEquals(500.0, nozzle.grossLitres, 0.001)
    assertEquals(495.0, nozzle.netLitres, 0.001)
    assertEquals(495.0 * 96.72, nozzle.calculateTotalAmount(96.72), 0.01)
  }
}
