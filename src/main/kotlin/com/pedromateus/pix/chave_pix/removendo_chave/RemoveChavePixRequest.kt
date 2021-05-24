package com.pedromateus.pix.chave_pix.removendo_chave

import io.micronaut.core.annotation.Introspected

@Introspected
class RemoveChavePixRequest(
    val clieteId: String,
    val chavePixId: String
) {
}