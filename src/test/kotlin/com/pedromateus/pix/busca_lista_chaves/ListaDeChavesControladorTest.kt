package com.pedromateus.pix.busca_lista_chaves

import com.pedromateus.pix.ListaChavePixRequest
import com.pedromateus.pix.ListaChavesPixDeUmClienteGrpc
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
class ListaDeChavesControladorTest(
    val repository: ChavePixRepository,
    val grpcClient: ListaChavesPixDeUmClienteGrpc.ListaChavesPixDeUmClienteBlockingStub,
) {

    @Inject
    lateinit var bcbClient: BcbClient
    lateinit var chave1: ChavePix
    lateinit var chave2: ChavePix
    lateinit var chave3: ChavePix
    lateinit var listaDeChaves: List<ChavePix>

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        chave1 = repository.save(chave(tipo = TipoDeChave.EMAIL, chave = "rafael@email.com", clienteId = CLIENTE_ID))
        chave2 = repository.save(chave(tipo = TipoDeChave.CPF, chave = "01234567890", clienteId = CLIENTE_ID))
        chave3 = repository.save(chave(tipo = TipoDeChave.CELULAR, chave = "+55 9 99999999", clienteId = CLIENTE_ID))
        listaDeChaves = listOf(chave1, chave2, chave3)
    }

    @Test
    fun `deve listar todas as chaves do cliente`() {
        //cenário

        val clienteId = CLIENTE_ID.toString()

        //ação

        val response =
            grpcClient.listaChavePixService(ListaChavePixRequest.newBuilder().setClienteId(clienteId).build())

        //comparações

        with(response.chavesList) {
            forEachIndexed{index,elem->
                assertEquals(this.get(index).chave,listaDeChaves.get(index).chave)
            }
        }

    }

    @Test
    fun `não deve listar chave quando o cliente não possuir chaves`() {
        //cenário

        val clienteSemChaves = UUID.randomUUID().toString()

        //ação

        val response =
            grpcClient.listaChavePixService(ListaChavePixRequest.newBuilder().setClienteId(clienteSemChaves).build())

        //comparações

        with(response) {
            assertEquals(0, this.chavesCount)
        }

    }

    @Test
    fun `não deve listar chave quando o cliente possuir id inválido`() {
        //cenário

        val clienteSemChaves = ""

        //ação

        val thrown =
            org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
                grpcClient.listaChavePixService(
                    ListaChavePixRequest
                        .newBuilder().setClienteId(clienteSemChaves).build()
                )
            }

        //comparações

        with(thrown) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("O id do cliente deve ser informado para que as chaves sejam encotradas!", status.description)
        }

    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): ListaChavesPixDeUmClienteGrpc.ListaChavesPixDeUmClienteBlockingStub? {
            return ListaChavesPixDeUmClienteGrpc.newBlockingStub(channel)
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