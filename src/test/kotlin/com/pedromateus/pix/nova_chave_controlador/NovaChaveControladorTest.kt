package com.pedromateus.pix.nova_chave_controlador

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.RegistraChavePixServiceGrpc
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.bcb.*
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaClient
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.InstituicaoResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.TitularResponse
import com.pedromateus.pix.chave_pix.nova_chave.toNovaChavePixRequest
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.validation.Validator

@MicronautTest(transactional = false)
class NovaChaveControladorTest(
    val repository: ChavePixRepository,
    val grpcClient: RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub
) {
    @Inject
    lateinit var clieteItau: ContaAssociadaClient

    @Inject
    lateinit var bcbClient: BcbClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave pix`() {

//        criar cenário

        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())

        `when`(bcbClient.criaChavePixNoBancoCentral(createPixRequest()))
            .thenReturn(HttpResponse.created(createPixResponse()))


        //executar a ação
        val response = grpcClient.registraChavePix(
            RegistraCavePixRequest.newBuilder()
            .setClienteId(CLIENTE_ID.toString())
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setChave("rafael@email.com")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build()
        )


        //relizar as verificações

        with(response) {
            assertEquals(CLIENTE_ID.toString(), clienteID)
            assertNotNull(chavePix)
        }
    }

    @Test
    fun `não deve retornar chave já cadastrada`() {
        //preprar cenário
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())
        val chavePix = ChavePix(
            CLIENTE_ID,
            TipoDeChaveImpl.CPF,
            "02467781054",
            TipoDeContaImpl.CONTA_CORRENTE,
            dadosDaContaResponse().toContaAssociada()
        )
        repository.save(chavePix)

        //realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }


        //fazer comparações
        with(thrown) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Essa chave já existe na base da dados. Delete-a para cadastrar outra.", status.description)
        }

    }

    @Test
    fun `não deve cadastrar chave pix quando não encontrar clientes ou serviço estiver fora do ar`() {
        //criar cenãrio
//        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(),TipoDeConta.CONTA_CORRENTE))
//            .thenReturn(dadosDaContaResponse())
        //Realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //fazer comparações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(
                "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                status.description
            )
        }
    }

    @Test
    fun `não pode cadastrar chave no banco central quando houver um erro interno`() {
        //criar cenãrio
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(),TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())
        //Realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //fazer comparações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(
                "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                status.description
            )
        }
    }

    @Test
    fun `não pode cadastrar chave no banco central quando houver um erro interno relacionado a requisição`() {
        //criar cenãrio
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(),TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())
        //Realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //fazer comparações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(
                "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                status.description
            )
        }
    }

    @Test
    fun `não deve registrar chaves quando erros desconhecidos ocorrerem no sistema`() {
        //criar cenãrio
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())

        `when`(bcbClient.criaChavePixNoBancoCentral(createPixRequest()))
            .thenReturn(HttpResponse.created(createPixResponse()))
        //Realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //fazer comparações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(
                "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                status.description
            )
        }
    }

    @Test
    fun `não deve registrar chave quando os parâmetros forem inválidos`() {
        //contruindo
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(RegistraCavePixRequest.newBuilder().build())
        }

        //realizar comparações
        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals(
                "No enum constant com.pedromateus.pix.chave_pix.TipoDeChaveImpl.UNKNOWN_TIPO_CHAVE",
                status.description
            )
        }
    }

    @Test
    fun `não deve registrar chaves quando erro de conexão acontecer`() {
        //criar cenãrio
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())

        `when`(bcbClient.criaChavePixNoBancoCentral(createPixRequest()))
            .thenReturn(HttpResponse.created(createPixResponse()))
        //Realizar a ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(
                RegistraCavePixRequest.newBuilder()
                    .setClienteId(CLIENTE_ID.toString())
                    .setTipoDeChave(TipoDeChave.CPF)
                    .setChave("02467781054")
                    .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                    .build()
            )
        }

        //fazer comparações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals(
                "Não foi possível completar o serviço, algum serviço externo está fora do ar!",
                status.description
            )
        }
    }

    @MockBean(ContaAssociadaClient::class)
    fun itauClientes(): ContaAssociadaClient? {
        return Mockito.mock(ContaAssociadaClient::class.java)

    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)

    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub? {
            return RegistraChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun dadosDaContaResponse(): ContaAssociadaResponse {
        return ContaAssociadaResponse(
            tipo = "CONTA_CORRENTE",
            instituicao = InstituicaoResponse("ITAU UNIBANCO", "60701190"),
            agencia = "0001",
            numero = "291900",
            titular = TitularResponse(CLIENTE_ID.toString(), "Rafael M C Ponte", "02467781054")
        )
    }

    private fun titularRequest(): TitularRequest {
        return TitularRequest(
            TipoDoTitular.NATURAL_PERSON,
            "Rafael M C Ponte",
            "02467781054"
        )
    }

    private fun contaBancoRequest(): ContaBancoRequest {
        return ContaBancoRequest(
            "60701190",
            "0001",
            "291900",
            TipoDeContaRequest.CACC
        )
    }

    private fun createPixRequest(): CreatePixRequest {
        return CreatePixRequest(
            keyType = TipoDeChaveImpl.EMAIL.name,
            key = "rafael@email.com",
            bankAccount = contaBancoRequest(),
            owner = titularRequest()
        )
    }

    fun createPixResponse(): CreatePixResponse {
        return CreatePixResponse(
            keyType = TipoDeChaveImpl.EMAIL.name,
            key = "rafael@email.com",
            bankAccount = contaBancoRequest(),
            owner = titularRequest(),
            createdAt = LocalDateTime.now().toString()
        )
    }

}