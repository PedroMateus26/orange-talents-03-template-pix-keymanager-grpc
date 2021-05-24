package com.pedromateus.pix.chave_pix.nova_chave.conta_associada

import com.pedromateus.pix.TipoDeConta
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("http://localhost:9091/api/v1/clientes")
interface ContaAssociadaClient {

    @Get("/{clienteId}/contas")
    fun buscaConta(@PathVariable clienteId: String, @QueryValue tipo: TipoDeConta):ContaAssociadaResponse?
}