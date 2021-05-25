package com.pedromateus.pix.chave_pix.nova_chave

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.RegistraChavePixServiceGrpc
import com.pedromateus.pix.RegitraChavePixResponse
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.bcb.*
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaClient
import com.pedromateus.pix.chave_pix.nova_chave.dtos.NovaChavePixResponse
import com.pedromateus.pix.excecoes.ApiErrorException
import com.pedromateus.pix.shared.grpc.ErrorHandler
import com.sun.net.httpserver.Authenticator
import com.sun.net.httpserver.Authenticator.*
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpStatus
import net.bytebuddy.implementation.bytecode.Throw
import java.util.*
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
    @Inject private val bcbClient: BcbClient
) : RegistraChavePixServiceGrpc.RegistraChavePixServiceImplBase() {

    @Transactional
    override fun registraChavePix(
        request: RegistraCavePixRequest,
        responseObserver: StreamObserver<RegitraChavePixResponse>,
    ) {

        var novaChaveGerada: NovaChavePixResponse?
        request.toNovaChavePixRequest(validator)
            .run {
                val contaAssociada =
                    contaAssocadaClient.buscaConta(
                        clienteId!!,
                        TipoDeConta.valueOf(tipoDeConta.toString())
                    ) ?: throw ApiErrorException(
                        "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                        HttpStatus.NOT_FOUND,
                        Status.NOT_FOUND
                    )
                if (repository.existsByChave(request.chave)) {
                    throw ApiErrorException(
                        "Essa chave já existe na base da dados. Delete-a para cadastrar outra.",
                        HttpStatus.BAD_REQUEST,
                        Status.ALREADY_EXISTS
                    )
                }
                val chavePix =toChavePix(contaAssociada.toContaAssociada())

                with(chavePix) {
                    val titular = TitularRequest(
                        TipoDoTitular.NATURAL_PERSON,
                        chavePix.contaAssociada?.nomeDoTitular,
                        chavePix.contaAssociada?.cpfTitular
                    )
                    val contaBancoRequest = ContaBancoRequest(
                        this.contaAssociada?.ispb,
                        this.contaAssociada?.agencia,
                        this.contaAssociada?.numeroDaConta,
                        when (tipoDeConta) {
                            TipoDeContaImpl.CONTA_CORRENTE -> TipoDeContaRequest.CACC
                            else -> TipoDeContaRequest.SVGS
                        }
                    )

                    val cretePixRequest =
                        CreatePixRequest(tipoDeChave?.name, chave, contaBancoRequest, titular)
                    val response=bcbClient.criaChavePixNoBancoCentral(cretePixRequest)

//                    when(response.status){
//                        HttpStatus.CREATED-> repository.save(this).run { novaChaveGerada=NovaChavePixResponse(id.toString(),clienteId.toString())}
//                        HttpStatus.UNPROCESSABLE_ENTITY->throw ApiErrorException("Chave pix já registrada",HttpStatus.UNPROCESSABLE_ENTITY,Status.UNKNOWN )
//                        else->throw ApiErrorException("Erro desconhecido",HttpStatus.I_AM_A_TEAPOT,Status.UNKNOWN )
//                    }
//                    when(val response=bcbClient.criaChavePixNoBancoCentral(cretePixRequest)){
//                        is Success->novaChaveGerada = NovaChavePixResponse(chavePix.clienteId, chavePix.id.toString())
//                        else->throw ApiErrorException("Problema com a requsição",response!!.status,Status.UNKNOWN)
//                    }

                }

            }

        //Retorno do código
//        responseObserver.onNext(
//            RegitraChavePixResponse
//                .newBuilder()
//                .setClienteID(novaChaveGerada?.clienteId)
//                .setChavePix(novaChaveGerada?.id)
//                .build()
//        )
        responseObserver.onCompleted()

    }


}



