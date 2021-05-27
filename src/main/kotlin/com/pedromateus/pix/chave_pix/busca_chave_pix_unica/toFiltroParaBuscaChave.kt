package com.pedromateus.pix.chave_pix.busca_chave_pix_unica

import com.pedromateus.pix.BuscaChavePixRequest
import com.pedromateus.pix.BuscaChavePixRequest.FiltroCase.*
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import javax.validation.Validator

fun BuscaChavePixRequest.toFiltroParaBuscaChave(validator: Validator): FiltroParaBuscarChavePix {
    val filtroParaBuscarChavePix = when (filtroCase) {
        PIXID -> pixId.run {
            val teste = FiltroParaBuscarChavePix.PorPixId(clienteId = clienteID, pixId = chavePix)
            println(teste)
            return teste
        }
        CHAVE -> FiltroParaBuscarChavePix.PorChave(chave = chave)
        FILTRO_NOT_SET -> throw ApiErrorException(
            "Para buscar a chave deve ser informado o id do clitente e o id da chave cadastrada ou apenas a chave",
            HttpStatus.BAD_REQUEST,
            Status.FAILED_PRECONDITION
        )
    }

    val violations = validator.validate(filtroParaBuscarChavePix)
    if (violations.isNotEmpty()) throw ApiErrorException(
        violations.toString(),
        HttpStatus.BAD_REQUEST,
        Status.FAILED_PRECONDITION
    )

    return filtroParaBuscarChavePix
}