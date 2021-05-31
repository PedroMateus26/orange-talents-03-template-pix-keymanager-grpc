package com.pedromateus.pix.busca_chave_contralador

import com.pedromateus.pix.BuscaChavePixRequest
import com.pedromateus.pix.BuscaChavePixRequest.FiltroPorPixId
import com.pedromateus.pix.BuscaChavePixRequest.newBuilder
import com.pedromateus.pix.BuscaUmaChavePiServiceGrpc
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.ChavePixRepository
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.bcb.*
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociadaResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.InstituicaoResponse
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.TitularResponse
import com.pedromateus.pix.nova_chave_controlador.NovaChaveControladorTest
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject


@MicronautTest(transactional = false)
internal class BuscaChavePixUnicaControladorTeste(
    val repository: ChavePixRepository,
    val grpcClient: BuscaUmaChavePiServiceGrpc.BuscaUmaChavePiServiceBlockingStub,
) {

    @Inject
    lateinit var bcbClient: BcbClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.save(chave(tipo = TipoDeChave.EMAIL, chave = "rafael@email.com", clienteId = CLIENTE_ID))
        repository.save(chave(tipo = TipoDeChave.CPF, chave = "01234567890", clienteId = CLIENTE_ID))
        repository.save(chave(tipo = TipoDeChave.CELULAR, chave = "+55 9 99999999", clienteId = CLIENTE_ID))
    }

    @Test
    fun `deve carregar chave pix corretamente quando o id do cliente e da chave forem fornecidos`() {
        // prepara o cenário
        val chaveExistente = repository.findByChave("rafael@email.com").get()

        //realizar as ações
        val response = grpcClient.buscaUmachavePix(
            newBuilder()
                .setPixId(
                    FiltroPorPixId.newBuilder()
                        .setChavePix(chaveExistente.id.toString())
                        .setClienteID(chaveExistente.clienteId.toString())
                        .build()
                )
                .build()
        )
        println()

        //realizar as comparações do resultados obtidos

        with(response) {
            assertEquals(chaveExistente.id.toString(), pixId)
            assertEquals(chaveExistente.clienteId.toString(), clientId)
            assertEquals(chaveExistente.tipoDeChave!!.name, chave.tipoDechave.name)
            assertEquals(chaveExistente.chave, chave.chave)
        }
    }

    @Test
    fun `não deve carregar chave quando o filtro for inválido`() {
        //cenário

        //ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.buscaUmachavePix(
                newBuilder()
                    .setPixId(
                        FiltroPorPixId.newBuilder()
                            .setChavePix("")
                            .setClienteID("")
                            .build()
                    )
                    .build()
            )
        }

        //validações

        with(thrown) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados de entrada inválidos!", status.description)
        }
    }

    @Test
    fun `não deve carregar chave pix quando não existir nenhum registro`() {
        //cenário

        val pixIdNaoExistente = UUID.randomUUID()
        val clienteIdNaoExistente = UUID.randomUUID()

        //ação

        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.buscaUmachavePix(
                newBuilder()
                    .setPixId(
                        FiltroPorPixId.newBuilder()
                            .setChavePix(pixIdNaoExistente.toString())
                            .setClienteID(clienteIdNaoExistente.toString())
                            .build()
                    )
                    .build()
            )
        }

        //validações

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }
    }

    @Test
    fun `deve carregar chave pix corretamente quando a chave for fornecida`() {
        // prepara o cenário
        val chaveExistente = repository.findByChave("rafael@email.com").get()

        //realizar as ações
        val response = grpcClient.buscaUmachavePix(
            newBuilder()
                .setChave("rafael@email.com")
                .build()
        )

        //realizar as comparações do resultados obtidos

        with(response) {
            assertEquals(chaveExistente.id.toString(), pixId)
            assertEquals(chaveExistente.clienteId.toString(), clientId)
            assertEquals(chaveExistente.tipoDeChave!!.name, chave.tipoDechave.name)
            assertEquals(chaveExistente.chave, chave.chave)
        }
    }

//    @Test
//    fun `deve carregar a chave quando não existir localmente mas existe no banco central`(){
//        //cenário
//        val bcbResponse= pixkeyDetailResponse()
//        `when`(bcbClient.buscaUmaChaveNoBancoCentral("outro.banco.banco-roxo"))
//            .thenReturn(HttpResponse.ok(pixkeyDetailResponse()))
//        //ação
//        val response = grpcClient.buscaUmachavePix(BuscaChavePixRequest.newBuilder()
//            .setChave("outro.banco.banco-roxo")
//            .build())
//
//        //validações
//        with(response){
//            assertEquals(bcbResponse.key,chave.chave)
//            assertEquals(bcbResponse.keyType.name,chave.tipoDechave.name)
//        }
//    }

    @Test
    fun `não deve carregar a chave quando não existir localmente nem no banco central`() {
        //cenário

        `when`(bcbClient.buscaUmaChaveNoBancoCentral("outro.banco.banco-roxo"))
            .thenReturn(HttpResponse.notFound())
        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.buscaUmachavePix(
                BuscaChavePixRequest.newBuilder()
                    .setChave("outro.banco.banco-roxo")
                    .build()
            )
        }
        //validações
        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave não encontrada!", status.description)
        }
    }

//    @Test
//    fun `não deve carregar a chave quando o filtro da chave estiver inválido`() {
//        //cenário
//
//        //ação
//        val thrown = assertThrows<StatusRuntimeException> {
//            grpcClient.buscaUmachavePix(
//                BuscaChavePixRequest.newBuilder()
//                    .setChave("")
//                    .build()
//            )
//        }
//        //validações
//        with(thrown) {
//            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
//            assertEquals("Dados de entrada inválidos!", status.description)
//        }
//    }

    @Test
    fun `não deve carregar chave quando o filtro de chave estiver inválido`(){
        //cenário

        //ação
        val thrown = assertThrows<StatusRuntimeException> {
            grpcClient.buscaUmachavePix(
                BuscaChavePixRequest.newBuilder().build()
            )
        }
        //validações
        with(thrown) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Para buscar a chave deve ser informado o id do clitente e o id da chave cadastrada ou apenas a chave", status.description)
        }

    }


        @MockBean(BcbClient::class)
        fun bcbClient(): BcbClient {
            return Mockito.mock(BcbClient::class.java)

        }

        @Factory
        class Clients {
            @Bean
            fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): BuscaUmaChavePiServiceGrpc.BuscaUmaChavePiServiceBlockingStub? {
                return BuscaUmaChavePiServiceGrpc.newBlockingStub(channel)
            }
        }

        private fun chave(
            tipo: TipoDeChave,
            chave: String = UUID.randomUUID().toString(),
            clienteId: UUID = UUID.randomUUID()
        ): ChavePix {
            return ChavePix(
                clienteId = clienteId,
                tipoDeChave = TipoDeChaveImpl.valueOf(tipo.name),
                chave = chave,
                tipoDeConta = TipoDeContaImpl.CONTA_CORRENTE,
                contaAssociada = contaAssociada()
            )
        }

        private fun contaAssociada(): ContaAssociada {
            return ContaAssociadaResponse(
                tipo = "CONTA_CORRENTE",
                instituicao = InstituicaoResponse("ITAU UNIBANCO", "60701190"),
                agencia = "0001",
                numero = "291900",
                titular = TitularResponse(
                    NovaChaveControladorTest.CLIENTE_ID.toString(),
                    "Rafael M C Ponte",
                    "02467781054"
                )
            ).toContaAssociada()
        }

        private fun pixkeyDetailResponse(): PixKeyDetailsResponse {
            return PixKeyDetailsResponse(
                keyType = TipoDeChaveImpl.EMAIL,
                key = "rafael@email.com",
                bankAccountResponse = bankaccount(),
                owner = titularResponse(),
                createdAt = LocalDateTime.now().toString()
            )
        }

        private fun bankaccount(): ContaBancoRequest {
            return ContaBancoRequest(
                participant = "60701190",
                branch = contaAssociada().agencia!!,
                accountNumber = contaAssociada().numeroDaConta!!,
                accountType = TipoDeContaRequest.CACC
            )
        }

        private fun titularResponse(): TitularRequest {
            return TitularRequest(
                type = TipoDoTitular.NATURAL_PERSON,
                name = "Rafael M C Ponte",
                taxIdNumber = "123456789"
            )
        }
    }