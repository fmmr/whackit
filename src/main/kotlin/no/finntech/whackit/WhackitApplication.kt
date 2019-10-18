package no.finntech.whackit

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.RaspiPin.*
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class WhackitApplication

val gpio = GpioFactory.getInstance()
val LOG = LoggerFactory.getLogger("MAIN");
val BUTTON_ARRAY = listOf(GPIO_28, GPIO_23, GPIO_24, GPIO_25)

val pin28 = gpio.provisionDigitalOutputPin(GPIO_28, "BUTTON_28", PinState.HIGH)
val pin23 = gpio.provisionDigitalOutputPin(GPIO_23, "BUTTON_23", PinState.HIGH)
val pin24 = gpio.provisionDigitalInputPin(GPIO_24, "BUTTON_24", PinPullResistance.PULL_UP)
val pin25 = gpio.provisionDigitalInputPin(GPIO_25, "BUTTON_25", PinPullResistance.PULL_UP)

fun main(args: Array<String>) {
    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
    setupLedAndSingleButton()

    listOf(pin24, pin25).forEach {
        it.addListener(GpioPinListenerDigital { event ->
            // display pin state on console
            if (event.state == PinState.HIGH) {
                findButton(event.pin as GpioPinDigitalInput)
            }
        })
    }

    runApplication<WhackitApplication>(*args)
}

private fun findButton(provisionedPin: GpioPinDigitalInput) {
    listOf(pin28, pin23).forEach {
        it.state = PinState.LOW
        if (provisionedPin.state.isHigh) {
            val button = findbutton(it, provisionedPin)
            LOG.info("BUTTON PRESSED $button")
            while (provisionedPin.state.isHigh) {
                // do nothing
            }
        }
        it.state = PinState.HIGH
    }
}

fun findbutton(row: GpioPin, col: GpioPin): String {
    return when {
        row == pin28 && col == pin24 -> "UPPER LEFT"
        row == pin28 && col == pin25 -> "LOWER LEFT"
        row == pin23 && col == pin24 -> "UPPER RIGHT"
        row == pin23 && col == pin25 -> "LOWER RIGHT"
        else -> "UNKNOWN"
    }
}

private fun setupLedAndSingleButton() {
    val myLED = gpio.provisionDigitalOutputPin(GPIO_27, "MyLED", PinState.LOW);
    myLED.blink(300)
    val input = gpio.provisionDigitalInputPin(GPIO_01, PinPullResistance.PULL_DOWN)
    input.addListener(GpioPinListenerDigital { event ->
        // display pin state on console
        LOG.info(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
    })
}


fun getButton(state28: Boolean, state23: Boolean, state24: Boolean, state25: Boolean): String {
    return when {
        state23 && state24 && !state25 && !state28 -> "UPPER LEFT"
        state23 && !state24 && state25 && !state28 -> "LOWER LEFT"
        !state23 && state24 && !state25 && state28 -> "UPPER RIGHT"
        !state23 && !state24 && state25 && state28 -> "LOWER RIGHT"
        state23 && !state24 && !state25 && state28 -> "HM1"
        !state23 && state24 && state25 && !state28 -> "HM2"
        !state23 && !state24 && !state25 && !state28 -> "YOU LET GO - KEEP PRESSING"
        else -> "UNKNOWN"
    }
}

