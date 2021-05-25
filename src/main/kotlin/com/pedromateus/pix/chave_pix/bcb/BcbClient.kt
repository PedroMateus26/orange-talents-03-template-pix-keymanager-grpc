package com.pedromateus.pix.chave_pix.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:8082")
interface BcbClient {

    @Post(value = "/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criaChavePixNoBancoCentral(@Body createPixRequest: CreatePixRequest?):HttpResponse<CreatePixResponse?>

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Delete("/api/v1/pix/keys/{key}")
    fun deltaChavePixDoBacen(@Body deletePixKeyrequest:DeletePixKeyRequest?,@PathVariable key:String?):HttpResponse<DeletePixKeyResponse?>
}