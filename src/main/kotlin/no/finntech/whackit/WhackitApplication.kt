package no.finntech.whackit

import com.pi4j.io.gpio.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.pi4j.util.CommandArgumentParser.getPin
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import com.pi4j.io.gpio.trigger.GpioTrigger


@SpringBootApplication
class WhackitApplication

fun main(args: Array<String>) {
	GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
	val  gpio = GpioFactory.getInstance()
	val myLED = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "MyLED", PinState.LOW);
	myLED.blink(300)


	val input = gpio.provisionDigitalInputPin(RaspiPin.GPIO_01, PinPullResistance.PULL_DOWN)
	input.addListener(GpioPinListenerDigital { event ->
		// display pin state on console
		println(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
	})


	runApplication<WhackitApplication>(*args)
}
