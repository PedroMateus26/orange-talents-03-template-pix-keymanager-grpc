package com.pedromateus.pix.chave_pix.removendo_chave

import com.pedromateus.pix.RemoveChavePixServiceGrpc
import com.pedromateus.pix.UsuarioPixRemoveReques
import com.pedromateus.pix.UsuarioPixRemoveResponse
import com.pedromateus.pix.chave_pix.ChavePixRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Singleton
class RemovendoChaveControlador (private val repository:ChavePixRepository):RemoveChavePixServiceGrpc.RemoveChavePixServiceImplBase(){
    
    private val logger=LoggerFactory.getLogger(this::class.java)

    override fun removendoChavePix(
        request: UsuarioPixRemoveReques,
        responseObserver: StreamObserver<UsuarioPixRemoveResponse>
    ) {

    }
}