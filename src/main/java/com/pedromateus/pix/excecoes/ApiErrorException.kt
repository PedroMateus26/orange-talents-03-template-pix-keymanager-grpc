package com.pedromateus.pix.excecoes

import io.grpc.Status
import io.micronaut.http.HttpStatus

class ApiErrorException(
    mesagemDeErro: String?,
    val httpStatus:HttpStatus,
    val statusGrpc:Status,


):RuntimeException(mesagemDeErro) {
    init{

    }

}