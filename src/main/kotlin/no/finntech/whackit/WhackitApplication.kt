package no.finntech.whackit

import com.pi4j.io.gpio.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WhackitApplication

fun main(args: Array<String>) {
	GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
	val  gpio = GpioFactory.getInstance()
	val myLED = gpio.provisionDigitalOutputPin(RaspiBcmPin.GPIO_29, "MyLED", PinState.LOW);
	myLED.blink(300)
	runApplication<WhackitApplication>(*args)
}
