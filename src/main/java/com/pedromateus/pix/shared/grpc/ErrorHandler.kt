package com.pedromateus.pix.shared.grpc

import io.micronaut.aop.Around
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*

@Around
@MustBeDocumented
@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
annotation class ErrorHandler()
