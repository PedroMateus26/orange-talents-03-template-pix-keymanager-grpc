package com.pedromateus.pix.nova_chave_controlador

import com.pedromateus.pix.RegistraCavePixRequest
import com.pedromateus.pix.RegistraChavePixServiceGrpc
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
class NovaChaveControladorTest(
    val repository: ChavePixRepository,
    val grpcClient: RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub,
) {
    @field:Inject
    lateinit var clieteItau:ContaAssociadaClient

    companion object {
        val CLIENTE_ID=UUID.randomUUID()
    }

    @BeforeEach
    fun setup() {
        repository.deleteAll()
    }

    @Test
    fun `deve registrar uma nova chave pix`(){
//        criar cenário
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())

        //executar a ação
        val response=grpcClient.registraChavePix(RegistraCavePixRequest.newBuilder()
            .setClienteId(CLIENTE_ID.toString())
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setChave("real@gmail.com")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build())


        //relizar as verificações

            with(response){
                assertEquals(CLIENTE_ID.toString(),clienteID)
                assertNotNull(chavePix)
            }
    }

    @Test
    fun `não deve retornar chave já cadastrada`(){
        //preprar cenário
        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(), TipoDeConta.CONTA_CORRENTE))
            .thenReturn(dadosDaContaResponse())
        val chavePix=ChavePix(CLIENTE_ID,TipoDeChaveImpl.CPF,"02467781054",TipoDeContaImpl.CONTA_CORRENTE,dadosDaContaResponse().toContaAssociada())
        repository.save(chavePix)

        //realizar a ação
        val thrown= assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(RegistraCavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("02467781054")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }


        //fazer comparações
        with(thrown){
            assertEquals(Status.ALREADY_EXISTS.code,status.code)
            assertEquals("Essa chave já existe na base da dados. Delete-a para cadastrar outra.",status.description)
        }

    }

    @Test
    fun `não deve cadastrar chave pix quando não encontrar clientes ou serviço estiver fora do ar`(){
        //criar cenãrio
//        `when`(clieteItau.buscaConta(CLIENTE_ID.toString(),TipoDeConta.CONTA_CORRENTE))
//            .thenReturn(dadosDaContaResponse())
        //Realizar a ação
        val thrown= assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(RegistraCavePixRequest.newBuilder()
                .setClienteId(CLIENTE_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setChave("02467781054")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        //fazer comparações
        with(thrown){
            assertEquals(Status.NOT_FOUND.code,status.code)
            assertEquals("Não foi possível completar o serviço, algum serviço externo está fora do ar!",status.description)
        }
    }

    @Test
    fun `não deve registrar chave quando os parâmetros forem inválidos`(){
        //contruindo
        val thrown= assertThrows<StatusRuntimeException> {
            grpcClient.registraChavePix(RegistraCavePixRequest.newBuilder().build())
        }

        //realizar comparações
        with(thrown){
            assertEquals(Status.INVALID_ARGUMENT.code,status.code)
            assertEquals("No enum constant com.pedromateus.pix.chave_pix.TipoDeChaveImpl.UNKNOWN_TIPO_CHAVE",status.description)
        }
    }

    @MockBean(ContaAssociadaClient::class)
    fun itauClientes():ContaAssociadaClient?{
        return Mockito.mock(ContaAssociadaClient::class.java)

    }


    @Factory
    class Clients {
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): RegistraChavePixServiceGrpc.RegistraChavePixServiceBlockingStub? {
            return RegistraChavePixServiceGrpc.newBlockingStub(channel)
        }
    }

    fun dadosDaContaResponse():ContaAssociadaResponse{
        return ContaAssociadaResponse(
            tipo="CONTA_CORRENTE",
            instituicao = InstituicaoResponse( "ITAU UNIBANCO", "60701190"),
            agencia = "1218",
            numero = "291900",
            titular= TitularResponse("c56dfef4-7901-44fb-84e2-a2cefb157890","Rafael M C Ponte","02467781054")
        )
    }


}