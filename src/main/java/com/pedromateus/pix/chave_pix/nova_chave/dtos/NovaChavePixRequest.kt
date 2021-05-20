package com.pedromateus.pix.chave_pix.nova_chave.dtos

import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
import com.pedromateus.pix.chave_pix.validacoes.ValidPixKey
import com.pedromateus.pix.chave_pix.validacoes.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size


@Introspected
@ValidPixKey
data class NovaChavePixRequest(
    @ValidUUID
    @field:NotBlank(message = "O id do cliente é obrigatório!")
    val clienteId:String?,
    //@field:NotNull(message = "Ao menos um tipo de chave deve ser informado!")
    val tipoDeChave: TipoDeChaveImpl?,
    @field:Size(max=77)
    val chave:String?,
    //@field:NotNull(message = "Não há clientes sem contas associadas!")
    val tipoDeConta: TipoDeContaImpl?
){

    fun toChavePix(contaAssociada :ContaAssociada): ChavePix {
        return ChavePix(
            clieteId = UUID.fromString(clienteId),
            tipoDeChave=tipoDeChave,
            chave=chave,
            tipoDeConta=tipoDeConta,
            contaAssociada=contaAssociada

        )
    }

}