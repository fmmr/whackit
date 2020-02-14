package no.finntech.whackit

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.RaspiGpioProvider
import com.pi4j.io.gpio.RaspiPinNumberingScheme
import com.pi4j.io.i2c.I2CBus
import com.pi4j.io.i2c.I2CDevice
import com.pi4j.io.i2c.I2CFactory
import com.pi4j.util.Console


const val ADR_PLAYER_1 = 0x3E

const val reg_reset = 0x7D
const val reg_clock = 0x1E
const val reg_dira = 0X0F // dir a
const val reg_dirb = 0x0E // dir b
const val reg_open_drains_a = 0x0B
const val reg_pull_up_b = 0x06
const val reg_debounce_config = 0x22
const val reg_key_config_1 = 0x25
const val reg_key_config_2 = 0x26
const val reg_debounce_enabled_b = 0x23
const val reg_keydata_1 = 0x27
const val reg_keydata_2 = 0x28

// handy doc
// https://pinout.xyz/pinout/wiringpi
// https://github.com/topherCantrell/pi-io-expander/blob/84f53abdeb499959c21ed52f7d8415977fbef23b/src/hardware_scan_main.py
// https://github.com/topherCantrell/pi-io-expander/blob/84f53abdeb499959c21ed52f7d8415977fbef23b/src/SX1509.py
// https://cdn.sparkfun.com/datasheets/BreakoutBoards/sx1509.pdf

fun main(args: Array<String>) {
    val console = Console()
    console.title("<-- The Pi4J Project -->", "I2C Example")
    console.promptForExit()

    val i2c = I2CFactory.getInstance(I2CBus.BUS_1)
    val player1 = setupDevice(i2c, ADR_PLAYER_1)

    while (true) {
        val a = player1.read(reg_keydata_1)
        val b = player1.read(reg_keydata_2)
        if (a != 255 && b != 255) {  // at least one button is pressed
            println("a: $a")
            println("b: $b")

        }
    }
}

private fun setupDevice(i2c: I2CBus, address: Int): I2CDevice {
    val device = i2c.getDevice(address)
    device.write(reg_reset, 0x12)
    device.write(reg_reset, 0x34)
    Thread.sleep(100)
    device.write(reg_clock, 0b01000000)  // internal 2 MHz clock scan
    device.write(reg_dira, -0b01110000) // Lower 4 pins are outputs
    device.write(reg_open_drains_a, 0b00001111)
    device.write(reg_dirb, -0b01110111)
    device.write(reg_pull_up_b, -0b01110111)
    device.write(reg_debounce_config, 0b00000011)
    device.write(reg_debounce_enabled_b, -0b01110111)
    //  FMR: don't think this is used:
    device.write(reg_key_config_1, 0b00000100) //
    device.write(reg_key_config_2, 0b00001001) // RegKeyConfig2
    println("device $device set up")
    return device
}