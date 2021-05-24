package com.pedromateus.pix.removendo_chave_controlador

import com.pedromateus.pix.RemoveChavePixServiceGrpc
import com.pedromateus.pix.UsuarioPixRemoveRequest
import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaClient
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.InstituicaoResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*

@MicronautTest(transactional = false)
class RemovendoChaveControladorTest(
    val repository: ChavePixRepository,
    val grpcClient: RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub,
) {

    lateinit var CHAVE_EXISTENTE: ChavePix

    @BeforeEach
    fun setup() {
        CHAVE_EXISTENTE = repository.save(ChavePix(
            UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
            TipoDeChaveImpl.EMAIL, "rafael@email.com",
            TipoDeContaImpl.CONTA_CORRENTE,
            dadosDaContaResponse().toContaAssociada()
        ))

    }

    @AfterEach
    fun cleanSetup() {
        repository.deleteAll()
    }

    @Test
    fun `deve deletar uma chave quando existir`() {

        //preprar cenário

        //ação a ser executada-o cenário em execução
        val response=grpcClient.removendoChavePix(
            UsuarioPixRemoveRequest.newBuilder()
                .setChavePix(CHAVE_EXISTENTE.id.toString())
                .setClienteID(CHAVE_EXISTENTE.clienteId.toString())
                .build())

        //testando resultados recebidos e comparar com esperado

        with(response){
            assertEquals(response.message,"Chave removida com sucesso!")
        }
    }

    @Test
    fun `não deve deletar chave pix não cadastrada no banco de dados`(){
        //cenário
        val pixIdNaoCadastrado=UUID.randomUUID()

        //executar ação

        val thrown= org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.removendoChavePix(
                UsuarioPixRemoveRequest.newBuilder()
                    .setChavePix(pixIdNaoCadastrado.toString())
                    .setClienteID(CHAVE_EXISTENTE.clienteId.toString())
                    .build()
            )
        }

        // //testando resultados recebidos e comparar com esperado
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Chave informada não enconrada na base de dados",status.description)
        }
    }

    @MockBean(ContaAssociadaClient::class)
    fun itauClientes(): ContaAssociadaClient? {
        return Mockito.mock(ContaAssociadaClient::class.java)

    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RemoveChavePixServiceGrpc.RemoveChavePixServiceBlockingStub? {
            return RemoveChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

    fun dadosDaContaResponse(): ContaAssociadaResponse {
        return ContaAssociadaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("ITAU UNIBANCO", "60701190"),
            agencia = "1218",
            numero = "291900",
            titular = TitularResponse("c56dfef4-7901-44fb-84e2-a2cefb157890", "Rafael M C Ponte", "02467781054")
        )
    }
}