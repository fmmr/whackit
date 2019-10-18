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

val pin28 = gpio.provisionDigitalOutputPin(GPIO_28, "BUTTON_28", PinState.HIGH)
val pin23 = gpio.provisionDigitalOutputPin(GPIO_23, "BUTTON_23", PinState.HIGH)
val pin24 = gpio.provisionDigitalInputPin(GPIO_24, "BUTTON_24", PinPullResistance.PULL_UP)
val pin25 = gpio.provisionDigitalInputPin(GPIO_25, "BUTTON_25", PinPullResistance.PULL_UP)

fun main(args: Array<String>) {
    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
    setupLedAndSingleButton()



    while (true) {
        pin28.state = PinState.LOW
        if (pin24.state.isHigh) {
            LOG.info("BUTTON PRESSED UPPER LEFT")
            while(pin24.state.isHigh){
                // do nothing
            }
        }
        if (pin25.state.isHigh) {
            LOG.info("BUTTON PRESSED LOWER LEFT")
            while(pin25.state.isHigh){
                // do nothing
            }
        }
        pin28.state = PinState.HIGH

        pin23.state = PinState.LOW
        if (pin24.state.isHigh) {
            LOG.info("BUTTON PRESSED UPPER RIGHT")
            while(pin24.state.isHigh){
                // do nothing
            }
        }
        if (pin25.state.isHigh) {
            LOG.info("BUTTON PRESSED LOWER RIGHT")
            while(pin25.state.isHigh){
                // do nothing
            }
        }
        pin23.state = PinState.HIGH
    }


//    initButtons()
//    runApplication<WhackitApplication>(*args)
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

