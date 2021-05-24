package com.pedromateus.pix.chave_pix.nova_chave

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.dtos.NovaChavePixRequest
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import javax.validation.ConstraintViolationException
import javax.validation.Validator


fun RegistraCavePixRequest.toNovaChavePixRequest(validator: Validator): NovaChavePixRequest {
    TipoDeChaveImpl.valueOf(tipoDeChave.name).valida(chave)
    val novaChavePixRequest = NovaChavePixRequest(
        clienteId = clienteId,
        tipoDeChave = when (tipoDeChave) {
            TipoDeChave.UNKNOWN_TIPO_CHAVE -> throw ApiErrorException("Tipo de chave não pode ser nulo!",HttpStatus.BAD_REQUEST,
                Status.FAILED_PRECONDITION)
            TipoDeChave.CPF -> TipoDeChaveImpl.CPF
            TipoDeChave.CELULAR -> TipoDeChaveImpl.CELULAR
            TipoDeChave.EMAIL -> TipoDeChaveImpl.EMAIL
            TipoDeChave.ALEATORIA -> TipoDeChaveImpl.ALEATORIA
            TipoDeChave.UNRECOGNIZED -> throw ApiErrorException("Tipo de chave inválida!",HttpStatus.BAD_REQUEST,
                Status.FAILED_PRECONDITION)
        },
        chave = chave,
        tipoDeConta = when (tipoDeConta) {
            TipoDeConta.UNKNOWN_TIPO_CONTA -> throw ApiErrorException("Tipo de conta inválida!",HttpStatus.BAD_REQUEST,
                Status.FAILED_PRECONDITION)
            TipoDeConta.CONTA_CORRENTE -> TipoDeContaImpl.CONTA_CORRENTE
            TipoDeConta.CONTA_POUPANCA -> TipoDeContaImpl.CONTA_POUPANCA
            TipoDeConta.UNRECOGNIZED -> throw ApiErrorException("Tipo de conta inválida!",HttpStatus.BAD_REQUEST,
                Status.FAILED_PRECONDITION)
        }

    )

    val novaChavePixRequestValidated = validator.validate(novaChavePixRequest)
    if (novaChavePixRequestValidated.isNotEmpty()) {
        throw ConstraintViolationException(novaChavePixRequestValidated)
    }
    return novaChavePixRequest
}
