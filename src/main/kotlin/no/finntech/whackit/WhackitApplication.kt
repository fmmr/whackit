package no.finntech.whackit

import com.pi4j.io.gpio.*
import com.pi4j.io.gpio.RaspiPin.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.pi4j.io.gpio.event.GpioPinListenerDigital


@SpringBootApplication
class WhackitApplication


fun main(args: Array<String>) {
    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
    val gpio = GpioFactory.getInstance()
    val myLED = gpio.provisionDigitalOutputPin(GPIO_27, "MyLED", PinState.LOW);
    myLED.blink(300)


    val input = gpio.provisionDigitalInputPin(GPIO_01, PinPullResistance.PULL_DOWN)
    input.addListener(GpioPinListenerDigital { event ->
        // display pin state on console
        println(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
    })

    initArray(gpio)
    runApplication<WhackitApplication>(*args)
}

private fun initArray(gpio: GpioController) {
    gpio.provisionDigitalOutputPin(GPIO_28, "MyLED 28", PinState.HIGH);
    gpio.provisionDigitalOutputPin(GPIO_23, "MyLED 23", PinState.HIGH);
    gpio.removeAllListeners()
    listOf(GPIO_24, GPIO_25).map { gpio.provisionDigitalInputPin(it, PinPullResistance.PULL_DOWN) }.forEach {
        it.addListener(GpioPinListenerDigital { event ->
            // display pin state on console
            println(" DETECTED ARRAY PRESS  " + event.pin + " = " + event.state)
            if (event.state == PinState.HIGH) {
                findButton(gpio)
            }
        })
    }
}

fun findButton(gpio: GpioController) {
    println("  find button called")
    val state24 = gpio.provisionDigitalInputPin(GPIO_24, PinPullResistance.PULL_DOWN).isHigh
    val state25 = gpio.provisionDigitalInputPin(GPIO_25, PinPullResistance.PULL_DOWN).isHigh
    println("  A: $state24 $state25")
    gpio.provisionDigitalOutputPin(GPIO_24, "MyLED 24", PinState.HIGH);
    gpio.provisionDigitalOutputPin(GPIO_25, "MyLED 25", PinState.HIGH);
    val state28 = gpio.provisionDigitalInputPin(GPIO_28, PinPullResistance.PULL_DOWN).isHigh
    val state23 = gpio.provisionDigitalInputPin(GPIO_23, PinPullResistance.PULL_DOWN).isHigh
    println("  B: $state28 $state23")
    val button = getButton(state28, state23, state24, state25)
    println("  you pressed button $button")
    initArray(gpio)
}

fun getButton(state28: Boolean, state23: Boolean, state24: Boolean, state25: Boolean): String {
    return when {
        state23 && state24 && !state25 && !state28 -> "ONE"
        state23 && !state24 && state25 && !state28 -> "TWO"
        state23 && !state24 && !state25 && state28 -> "THREE"
        !state23 && state24 && state25 && !state28 -> "FOUR"
        !state23 && state24 && !state25 && state28 -> "FIVE"
        !state23 && !state24 && state25 && state28 -> "SIX"
        !state23 && !state24 && !state25 && !state28 -> "HMMM"
        else -> "UNKNOWN"
    }

}
