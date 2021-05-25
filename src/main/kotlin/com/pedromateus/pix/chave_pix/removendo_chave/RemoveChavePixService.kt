package com.pedromateus.pix.chave_pix.removendo_chave

import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.bcb.BcbClient
import com.pedromateus.pix.chave_pix.bcb.DeletePixKeyRequest
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.validation.Valid

@Singleton
@Validated
class RemoveChavePixService(
    val repository: ChavePixRepository,
    val bcbClient: BcbClient
) {

    fun removeChavePix(@Valid removeChvaPixRequest: RemoveChavePixRequest): RemoveChvePixResponse {

        val clinteUUID = UUID.fromString(removeChvaPixRequest.clieteId)
        val chavePixUUID = UUID.fromString(removeChvaPixRequest.chavePixId)
        repository.findByIdAndClienteId(chavePixUUID, clinteUUID)
            .orElseThrow {
                throw ApiErrorException(
                    "Chave informada não enconrada na base de dados", HttpStatus.NOT_FOUND,
                    Status.NOT_FOUND
                )
            }.run {
                val deletePixKeyRequest = DeletePixKeyRequest(tipoDeChave?.name, contaAssociada?.ispb)
                val response = bcbClient.deltaChavePixDoBacen(deletePixKeyRequest,tipoDeChave?.name)
                println(response.status)
                when (response.status) {
                    HttpStatus.OK -> repository.delete(this)
                    HttpStatus.valueOf(403) -> throw ApiErrorException(
                        "Essa operação não foi permitida",
                        HttpStatus.FORBIDDEN,
                        Status.PERMISSION_DENIED
                    )
                    HttpStatus.BAD_REQUEST -> throw ApiErrorException(
                        "Erro na requisição",
                        HttpStatus.BAD_REQUEST,
                        Status.INTERNAL
                    )
                    HttpStatus.NOT_FOUND-> throw ApiErrorException(
                        "Chave pix não encontrada no Banco Central",
                        HttpStatus.NOT_FOUND,
                        Status.NOT_FOUND
                    )
                }
            }

        return RemoveChvePixResponse("Chave removida com sucesso!")

    }
}