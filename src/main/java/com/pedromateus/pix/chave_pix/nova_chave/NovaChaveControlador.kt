package com.pedromateus.pix.chave_pix.nova_chave

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.RegistraChavePixServiceGrpc
import com.pedromateus.pix.RegitraChavePixResponse
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaInterceptor
import com.pedromateus.pix.shared.grpc.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Validator

@Singleton
//@ErrorHandler
open class NovaChaveControlador(
    @Inject val repository: ChavePixRepository,
    @Inject val contaAssocadaInterceptor: ContaAssociadaInterceptor,
    @Inject val validator: Validator
) : RegistraChavePixServiceGrpc.RegistraChavePixServiceImplBase() {

    @Transactional
    override fun registraChavePix(
        request: RegistraCavePixRequest,
        responseObserver: StreamObserver<RegitraChavePixResponse>
    ) {

        val pixDto = request.toNovaChavePixRequest(validator)
            .run {
                val contaAssociada =
                    contaAssocadaInterceptor.buscaConta(
                        this.clienteId!!,
                        TipoDeConta.valueOf(this.tipoDeConta!!.toString())
                    ).toContaAssociada()
                val elem = repository.save(this.toChavePix(contaAssociada))
                if (this != null) println(repository.findById(elem.id))
            }


        //Retorno do códivo
        responseObserver.onNext(
            RegitraChavePixResponse
                .newBuilder()
                .setClienteID("Deu muito bom!")
                .build()
        )
        responseObserver.onCompleted()

    }


}



