package com.pedromateus.pix.chave_pix.bcb

import com.pedromateus.pix.chave_pix.busca_chave_pix_unica.ChavePixInfo
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client( "http://localhost:8082")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun criaChavePixNoBancoCentral(@Body createPixRequest: CreatePixRequest):HttpResponse<CreatePixResponse>?

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Delete("/api/v1/pix/keys/{key}")
    fun deltaChavePixDoBacen(@Body deletePixKeyrequest:DeletePixKeyRequest,@PathVariable key:String):HttpResponse<DeletePixKeyResponse>

    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    @Get("/api/v1/pix/keys/{key}")
    fun buscaUmaChaveNoBancoCentral(@PathVariable key:String):HttpResponse<PixKeyDetailsResponse>
}