package com.example.demospbt4kt.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    @GetMapping
    fun hello(): String {
        if (System.getProperty("org.graalvm.nativeimage.imagecode") != null) {
            return "Hola desde Spring Boot con Kotlin nativo!"
        }
        return "Hola desde Spring Boot con Kotlin en JVM!"
    }
}