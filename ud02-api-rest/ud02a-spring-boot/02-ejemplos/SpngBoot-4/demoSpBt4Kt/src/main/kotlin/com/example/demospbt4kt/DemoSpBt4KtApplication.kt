package com.example.demospbt4kt

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DemoSpBt4KtApplication

fun main(args: Array<String>) {
    val isNative = System.getProperty("org.graalvm.nativeimage.imagecode") != null
    println("¿Ejecutando como imagen nativa? $isNative")

    if (isNative) {
        System.setProperty("spring.profiles.active", "native")
    } else {
        System.setProperty("spring.profiles.active", "kotlin")
    }

    runApplication<DemoSpBt4KtApplication>(*args)
}