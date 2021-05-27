package com.pedromateus.pix.chave_pix.busca_chave_pix_unica

import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.bcb.BcbClient
import com.pedromateus.pix.chave_pix.validacoes.ValidUUID
import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class FiltroParaBuscarChavePix {

    abstract fun filtraChaveDeEntrada(repository: ChavePixRepository, bcbClient: BcbClient):ChavePixInfo

    @Introspected
    data class PorPixId(
        @field:NotBlank @field:ValidUUID val clienteId:String,
        @field:NotBlank @field:ValidUUID val pixId:String,

        ):FiltroParaBuscarChavePix(){

        val pixUUID= UUID.fromString(pixId)
        val clinteUUID= UUID.fromString(clienteId)

        override fun filtraChaveDeEntrada(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfo {
            return repository.findByIdAndClienteId(pixUUID, clinteUUID)
                .map(ChavePixInfo::of)
                .orElseThrow {
                    throw ApiErrorException("Chave não encontrada!", HttpStatus.NOT_FOUND, Status.NOT_FOUND)
                }
        }
    }

    @Introspected
    data class PorChave(@Size(max = 77) val chave:String):FiltroParaBuscarChavePix(){
        override fun filtraChaveDeEntrada(repository: ChavePixRepository, bcbClient: BcbClient): ChavePixInfo {
            return repository.findByChave(chave)
                .map(ChavePixInfo::of)
                .orElseGet{
                    val response=bcbClient.buscaUmaChaveNoBancoCentral(chave)
                    when(response.status){
                        HttpStatus.OK->response.body()?.toChavePixInfo()
                        else->throw ApiErrorException("Chave não encontrada!",HttpStatus.NOT_FOUND,Status.NOT_FOUND)
                    }
                }
        }

    }
}