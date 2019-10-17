package no.finntech.whackit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WhackitApplication

fun main(args: Array<String>) {
	runApplication<WhackitApplication>(*args)
}
