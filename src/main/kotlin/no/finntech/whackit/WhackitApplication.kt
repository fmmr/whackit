//package no.finntech.whackit
//
//import com.pi4j.io.gpio.*
//import com.pi4j.io.gpio.RaspiPin.*
//import com.pi4j.io.gpio.event.GpioPinListenerDigital
//import org.slf4j.LoggerFactory
//import org.springframework.boot.autoconfigure.SpringBootApplication
//import org.springframework.boot.runApplication
//
//@SpringBootApplication
//class WhackitApplication
//
//val gpio = GpioFactory.getInstance()
//val LOG = LoggerFactory.getLogger("MAIN");
//
//fun main(args: Array<String>) {
//    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))
//    val input = gpio.provisionDigitalInputPin(GPIO_26, PinPullResistance.PULL_DOWN)
//    input.addListener(GpioPinListenerDigital { event ->
//        // display pin state on console
//        LOG.info(" --> GPIO PIN STATE CHANGE: " + event.pin + " = " + event.state)
//    })
//    val gameChannel = gpio.provisionDigitalOutputPin(GPIO_27, "Game", PinState.LOW);
//    gameChannel.blink(1000)
//
//    runApplication<WhackitApplication>(*args)
//}
//
//
//
//
//
//
//
