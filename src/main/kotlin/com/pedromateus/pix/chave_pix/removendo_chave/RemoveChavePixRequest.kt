package com.pedromateus.pix.chave_pix.removendo_chave

import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class RemoveChavePixRequest(
    @field:NotBlank(message = "O id do cliente é obrigatório para remover a cave")
    val clieteId: String,
    @field:NotBlank(message = "O id da chave é obrigatório para remover a mesma")
    val chavePixId: String
) {
}