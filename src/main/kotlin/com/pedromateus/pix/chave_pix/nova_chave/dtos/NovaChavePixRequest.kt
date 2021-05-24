package com.pedromateus.pix.chave_pix.nova_chave.dtos

import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
import com.pedromateus.pix.chave_pix.validacoes.ValidUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Introspected
data class NovaChavePixRequest(
    @ValidUUID
    @field:NotBlank (message = "O id do cliente é obrigatório!")
    val clienteId:String?,
    @field:NotNull(message = "Ao menos um tipo de chave deve ser informado!")
    val tipoDeChave: TipoDeChaveImpl?,
    @field:Size(max=77)
    val chave:String?,
    @field:NotNull(message = "Não há clientes sem contas associadas!")
    val tipoDeConta: TipoDeContaImpl?
){

    fun toChavePix(contaAssociada :ContaAssociada): ChavePix {
        return ChavePix(
            clienteId = UUID.fromString(clienteId),
            tipoDeChave=tipoDeChave,
            chave=chave,
            tipoDeConta=tipoDeConta,
            contaAssociada=contaAssociada

        )
    }

}