package com.pedromateus.pix.chave_pix.removendo_chave

import com.pedromateus.pix.RemoveChavePixServiceGrpc
import com.pedromateus.pix.UsuarioPixRemoveRequest
import com.pedromateus.pix.UsuarioPixRemoveResponse
import com.pedromateus.pix.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemovendoChaveControlador(private val removeService: RemoveChavePixService) :
    RemoveChavePixServiceGrpc.RemoveChavePixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun removendoChavePix(
        request: UsuarioPixRemoveRequest,
        responseObserver: StreamObserver<UsuarioPixRemoveResponse>
    ) {
        var response: RemoveChvePixResponse
        request.convertToRemoveChavePixRequest().run {
            response = removeService.removeChavePix(this)
        }
        responseObserver.onNext(
            UsuarioPixRemoveResponse.newBuilder()
                .setMessage(response.mensagem)
                .build()
        )
        responseObserver.onCompleted()
    }
}