package com.pedromateus.pix.chave_pix.lista_chaves

import com.google.protobuf.Timestamp
import com.pedromateus.pix.*
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.excecoes.ApiErrorException
import com.pedromateus.pix.shared.grpc.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Singleton
import javax.xml.validation.Validator

@Singleton
@ErrorHandler
class ListaDeChavesControlador(
    private val repository: ChavePixRepository,
) : ListaChavesPixDeUmClienteGrpc.ListaChavesPixDeUmClienteImplBase() {

    override fun listaChavePixService(
        request: ListaChavePixRequest?,
        responseObserver: StreamObserver<ListaChavePixResponse>?
    ) {
        if(request!!.clienteId.isNullOrBlank()){
            throw ApiErrorException(
                "O id do cliente deve ser informado para que as chaves sejam encotradas!",
                HttpStatus.NOT_FOUND, Status.NOT_FOUND
            )
        }
        val clienteUUID = UUID.fromString(request?.clienteId)
                val listaDeChaves=repository.findAllByClienteId(clienteUUID)
            .map {
                ListaChavePixResponse.Chave.newBuilder()
                    .setPixId(it.id.toString())
                    .setTipo(TipoDeChave.valueOf(it.tipoDeChave!!.name))
                    .setChave(it.chave)
                    .setTipoDeConta(TipoDeConta.valueOf(it.tipoDeConta!!.name))
                    .setCriadoEm(it.criadoEm.run {
                        val createdAt = atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp.newBuilder()
                            .setSeconds(createdAt.epochSecond)
                            .setNanos(createdAt.nano)
                            .build()
                    })
                    .build()
            }

        responseObserver!!.onNext(
            ListaChavePixResponse.newBuilder()
                .setClienteId(clienteUUID.toString())
                .addAllChaves(listaDeChaves)
                .build()
        )
        responseObserver.onCompleted()



    }
}