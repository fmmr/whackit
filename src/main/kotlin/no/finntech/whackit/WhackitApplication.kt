package no.finntech.whackit

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.RaspiPin.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.slf4j.LoggerFactory


@SpringBootApplication
class WhackitApplication

val gpio = GpioFactory.getInstance()
val LOG = LoggerFactory.getLogger("MAIN");
val BUTTON_ARRAY = listOf(GPIO_28, GPIO_23, GPIO_24, GPIO_25)

fun main(args: Array<String>) {
    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
    setupLedAndSingleButton()
    initButtons()

    runApplication<WhackitApplication>(*args)
}

private fun setupLedAndSingleButton() {
    val myLED = gpio.provisionDigitalOutputPin(GPIO_27, "MyLED", PinState.LOW);
    myLED.blink(300)
    val input = gpio.provisionDigitalInputPin(GPIO_01, PinPullResistance.PULL_DOWN)
    input.addListener(GpioPinListenerDigital { event ->
        // display pin state on console
        println(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
    })
}

fun findButton() {
    val state24 = (gpio.getProvisionedPin(GPIO_24) as GpioPinDigital).state.isHigh
    val state25 = (gpio.getProvisionedPin(GPIO_25) as GpioPinDigital).state.isHigh
    unprovisionButtons()
    gpio.provisionDigitalOutputPin(GPIO_24, "MyLED 24", PinState.HIGH);
    gpio.provisionDigitalOutputPin(GPIO_25, "MyLED 25", PinState.HIGH);
    val state28 = gpio.provisionDigitalInputPin(GPIO_28, PinPullResistance.PULL_DOWN).isHigh
    val state23 = gpio.provisionDigitalInputPin(GPIO_23, PinPullResistance.PULL_DOWN).isHigh
    val button = getButton(state28, state23, state24, state25)
    println("  yYOU PRESSED:  $button")
    initButtons()
}

private fun unprovisionButtons() {
    val buttonArrayPins = listOf(gpio.getProvisionedPin(GPIO_28), gpio.getProvisionedPin(GPIO_23), gpio.getProvisionedPin(GPIO_24), gpio.getProvisionedPin(GPIO_25)).filterNotNull().toTypedArray()
    gpio.unprovisionPin(*buttonArrayPins)
}

fun getButton(state28: Boolean, state23: Boolean, state24: Boolean, state25: Boolean): String {
    return when {
        state23 && state24 && !state25 && !state28 -> "UPPER LEFT"
        state23 && !state24 && state25 && !state28 -> "LOWER LEFT"
        !state23 && state24 && !state25 && state28 -> "UPPER RIGHT"
        !state23 && !state24 && state25 && state28 -> "LOWER RIGHT"
        state23 && !state24 && !state25 && state28 -> "HM1"
        !state23 && state24 && state25 && !state28 -> "HM2"
        !state23 && !state24 && !state25 && !state28 -> "HM3"
        else -> "UNKNOWN"
    }
}

private fun initButtons() {
    unprovisionButtons()
    gpio.provisionDigitalOutputPin(GPIO_28, "MyLED 28", PinState.HIGH);
    gpio.provisionDigitalOutputPin(GPIO_23, "MyLED 23", PinState.HIGH);
    listOf(GPIO_24, GPIO_25).map { gpio.provisionDigitalInputPin(it, PinPullResistance.PULL_DOWN) }.forEach {
        it.addListener(GpioPinListenerDigital { event ->
            // display pin state on console
            println(" DETECTED ARRAY PRESS  " + event.pin + " = " + event.state)
            if (event.state == PinState.HIGH) {
                try {
                    findButton()
                } catch (e: Exception) {
                    LOG.error("Got exception trying to find button: ", e)
                }
            }
        })
    }
}
