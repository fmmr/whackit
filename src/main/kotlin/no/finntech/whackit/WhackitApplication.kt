package no.finntech.whackit

import com.pi4j.io.gpio.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WhackitApplication

fun main(args: Array<String>) {
	GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING))
	val  gpio = GpioFactory.getInstance()
	gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_21, "MyLED", PinState.HIGH);
	runApplication<WhackitApplication>(*args)
}
