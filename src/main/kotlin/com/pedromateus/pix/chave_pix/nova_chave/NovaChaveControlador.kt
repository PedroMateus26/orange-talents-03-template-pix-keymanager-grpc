package com.pedromateus.pix.chave_pix.nova_chave

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.RegistraChavePixServiceGrpc
import com.pedromateus.pix.RegitraChavePixResponse
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaClient
import com.pedromateus.pix.chave_pix.nova_chave.dtos.NovaChavePixResponse
import com.pedromateus.pix.excecoes.ApiErrorException
import com.pedromateus.pix.shared.grpc.ErrorHandler
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Validator

@Singleton
@ErrorHandler
class NovaChaveControlador(
    @Inject private val repository: ChavePixRepository,
    @Inject private val contaAssocadaClient: ContaAssociadaClient,
    @Inject private val validator: Validator,
) : RegistraChavePixServiceGrpc.RegistraChavePixServiceImplBase() {

    @Transactional
    override fun registraChavePix(
        request: RegistraCavePixRequest,
        responseObserver: StreamObserver<RegitraChavePixResponse>,
    ) {

        var novaChaveGerada:NovaChavePixResponse?
        request.toNovaChavePixRequest(validator)
            .run {

                val contaAssociada =
                    contaAssocadaClient.buscaConta(
                        this.clienteId!!,
                        TipoDeConta.valueOf(this.tipoDeConta.toString())
                    )?:throw ApiErrorException("Não foi possível completar o serviço, algum serviço externo está fora do ar!", HttpStatus.BAD_REQUEST,Status.NOT_FOUND)
                if(repository.existsByChave(request.chave)){
                    throw ApiErrorException("Essa chave já existe na base da dados. Delete-a para cadastrar outra.",HttpStatus.BAD_REQUEST,Status.ALREADY_EXISTS)
                }
                val chavePix=repository.save(this.toChavePix(contaAssociada.toContaAssociada()))
               novaChaveGerada=NovaChavePixResponse (chavePix.clieteId,chavePix.chave)
            }


        //Retorno do código
        responseObserver.onNext(
            RegitraChavePixResponse
                .newBuilder()
                .setClienteID(novaChaveGerada?.clienteID.toString())
                .setChavePix(novaChaveGerada?.chavePixGerada)
                .build()
        )
        responseObserver.onCompleted()

    }


}



