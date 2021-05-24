package com.pedromateus.pix.chave_pix.removendo_chave

import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional

@Singleton
class RemoveChavePixService(val repository:ChavePixRepository) {

    fun removeChavePix(removeChvaPixRequest:RemoveChavePixRequest):RemoveChvePixResponse{

        val clinteUUID=UUID.fromString(removeChvaPixRequest.clieteId)
        val chavePixUUID=UUID.fromString(removeChvaPixRequest.chavePixId)

        repository.findByIdAndClienteId(chavePixUUID,clinteUUID)
            .orElseThrow {throw ApiErrorException("Chave informada não enconrada na base de dados", HttpStatus.NOT_FOUND,
                Status.NOT_FOUND)}.run {
                    repository.delete(this)
            }


        return RemoveChvePixResponse("Chave removida com sucesso")

    }
}