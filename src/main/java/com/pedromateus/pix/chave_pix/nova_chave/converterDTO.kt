package com.pedromateus.pix.chave_pix.nova_chave

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.dtos.NovaChavePixRequest
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun RegistraCavePixRequest.toNovaChavePixRequest(validator: Validator): NovaChavePixRequest {
    val novaChavePixRequest = NovaChavePixRequest(
        clienteId = clienteId,
        tipoDeChave = when (tipoDeChave) {
            TipoDeChave.UNKNOWN_TIPO_CHAVE -> throw IllegalStateException("Tipo de chave inválida!")
            TipoDeChave.CPF -> TipoDeChaveImpl.CPF
            TipoDeChave.CELULAR -> TipoDeChaveImpl.CELULAR
            TipoDeChave.EMAIL -> TipoDeChaveImpl.EMAIL
            TipoDeChave.ALEATORIA -> TipoDeChaveImpl.ALEATORIA
            TipoDeChave.UNRECOGNIZED -> throw IllegalStateException("Tipo de chave inválida!")
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            TipoDeConta.UNKNOWN_TIPO_CONTA -> throw IllegalStateException("Tipo de chave inválida!")
            TipoDeConta.CONTA_CORRENTE -> TipoDeContaImpl.CONTA_CORRENTE
            TipoDeConta.CONTA_POUPANCA -> TipoDeContaImpl.CONTA_POUPANCA
            TipoDeConta.UNRECOGNIZED -> throw IllegalStateException("Tipo de conta inválida!")
        }

    )
    val novaChavePixRequestValidated=validator.validate(novaChavePixRequest)
    if(novaChavePixRequestValidated.isNotEmpty()){
        throw ConstraintViolationException(novaChavePixRequestValidated)
    }
    return novaChavePixRequest
}