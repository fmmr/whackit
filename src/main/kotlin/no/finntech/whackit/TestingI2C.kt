package no.finntech.whackit

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiGpioProvider
import com.pi4j.io.gpio.RaspiPinNumberingScheme
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.util.Console


val gpio = GpioFactory.getInstance()
const val reg_reset = 0x7D
const val reg_clock = 0x1E
const val SX_RegDirA = 0x0F // # Data direction ...
const val reg_dira = 0X0F // dir a
const val reg_dirb = 0x0E // dir b
val reg_open_drains_a = 0x0B
val reg_pull_up_b = 0x06
val RegDebounceConfig = 0x22
val RegKeyConfig1 = 0x25
val RegDebounceEnableB = 0x23

// https://pinout.xyz/pinout/wiringpi
fun main(args: Array<String>) {
    GpioFactory.setDefaultProvider(RaspiGpioProvider(RaspiPinNumberingScheme.DEFAULT_PIN_NUMBERING))

    val PLAYER_1 = 0x3E;

    val console = Console()


    console.title("<-- The Pi4J Project -->", "I2C Example");
    console.promptForExit();
    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)

    val device = i2c.getDevice(PLAYER_1)

    device.write(reg_reset, 0x12)
    device.write(reg_reset, 0x34)
    Thread.sleep(100)

    device.write(reg_clock, 0b01000000)  // internal 2 MHz clock scan


    device.write(reg_dira, -0b01110000) // Lower 4 pins are outputs
    device.write(reg_open_drains_a, 0b00001111)

    device.write(reg_dirb, -0b01110111) // Lower 4 pins are outputs
    device.write(reg_pull_up_b, -0b01110111) // Lower 4 pins are outputs


    device.write(RegDebounceConfig, 0b00000011) // Lower 4 pins are outputs

    device.write(RegDebounceEnableB, -0b01110111) // Lower 4 pins are outputs
    device.write(RegKeyConfig1, 0b00000100) // Lower 4 pins are outputs
    device.write(0x26, 0b00001001) // Lower 4 pins are outputs
    println("device set up")


    while (true) {
        val a = device.read(0x27)
        val b = device.read(0x28)
        if (a != 255 && b != 255) {
            println("a: $a")
            println("b: $b")

        }
    }
    //while True:
    //r,c = io.get_keyboard_row_col()
    //a = io.read_register(0x27)
    //b = io.read_register(0x28)
//
    //if r!=0 and c!=0 and a!=255 and b!=255:
    //print("Pressed "+KEYMAP[str(r)+str(c)])
    //print("A: {0:b}".format(a))
    //print("B: {0:b}".format(b))
//
    //time.sleep(.03)

}