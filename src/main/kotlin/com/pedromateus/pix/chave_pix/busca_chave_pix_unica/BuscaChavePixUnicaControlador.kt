package com.pedromateus.pix.chave_pix.busca_chave_pix_unica

import com.pedromateus.pix.BuscaChavePixRequest
import com.pedromateus.pix.BuscaChavePixResponse
import com.pedromateus.pix.BuscaUmaChavePiServiceGrpc
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.bcb.BcbClient
import com.pedromateus.pix.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class BuscaChavePixUnicaControlador(
   @Inject private val validator:Validator,
   @Inject private val repository:ChavePixRepository,
   @Inject private val bcbClient: BcbClient
):BuscaUmaChavePiServiceGrpc.BuscaUmaChavePiServiceImplBase() {

    override fun buscaUmachavePix(
        request: BuscaChavePixRequest,
        responseObserver: StreamObserver<BuscaChavePixResponse>
    ) {
        val filtro=request.toFiltroParaBuscaChave(validator)
        val chavePixInfo=filtro.filtraChaveDeEntrada(repository,bcbClient)

        responseObserver.onNext(ChavePixInfoToBuscaChavePIxResponse().converteParaBuscaChavePIxResponse(chavePixInfo))
        responseObserver.onCompleted()
    }
}