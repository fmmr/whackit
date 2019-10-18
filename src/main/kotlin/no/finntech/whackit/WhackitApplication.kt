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

val pin28 = gpio.provisionDigitalMultipurposePin(GPIO_28, "BUTTON_28", PinMode.DIGITAL_OUTPUT)
val pin23 = gpio.provisionDigitalMultipurposePin(GPIO_23, "BUTTON_23", PinMode.DIGITAL_OUTPUT)
val pin24 = gpio.provisionDigitalMultipurposePin(GPIO_24, "BUTTON_24", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP)
val pin25 = gpio.provisionDigitalMultipurposePin(GPIO_25, "BUTTON_25", PinMode.DIGITAL_INPUT, PinPullResistance.PULL_UP)

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
        LOG.info(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
    })
}

fun findButton() {
    listOf(pin24, pin25).forEach { it.removeAllListeners() }

    val state24 = pin24.state.isHigh
    val state25 = pin25.state.isHigh
    setReadState()
    val state28 = pin28.isHigh
    val state23 = pin23.isHigh
    val button = getButton(state28, state23, state24, state25)
    LOG.info("  YOU PRESSED:  $button")
    initButtons()
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

private fun initButtons() {
    pin28.mode = PinMode.DIGITAL_OUTPUT
    pin23.mode = PinMode.DIGITAL_OUTPUT
    pin28.state = PinState.HIGH
    pin23.state = PinState.HIGH

    pin24.mode = PinMode.DIGITAL_INPUT
    pin25.mode = PinMode.DIGITAL_INPUT
    pin24.pullResistance = PinPullResistance.PULL_UP
    pin25.pullResistance = PinPullResistance.PULL_UP

    listOf(pin24, pin25).forEach {
        it.addListener(GpioPinListenerDigital { event ->
            // display pin state on console
            LOG.trace(" DETECTED ARRAY PRESS  " + event.pin + " = " + event.state)
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

private fun setReadState() {
    pin24.mode = PinMode.DIGITAL_OUTPUT
    pin25.mode = PinMode.DIGITAL_OUTPUT
    pin24.state = PinState.HIGH
    pin25.state = PinState.HIGH

    pin28.mode = PinMode.DIGITAL_INPUT
    pin23.mode = PinMode.DIGITAL_INPUT
    pin23.pullResistance = PinPullResistance.PULL_UP
    pin28.pullResistance = PinPullResistance.PULL_UP

}
