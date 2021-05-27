package com.pedromateus.pix.chave_pix.busca_chave_pix_unica

import com.google.protobuf.Timestamp
import com.pedromateus.pix.BuscaChavePixResponse
import com.pedromateus.pix.TipoDeChave
import com.pedromateus.pix.TipoDeConta
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import java.time.LocalDateTime
import java.time.ZoneId

class ChavePixInfoToBuscaChavePIxResponse {

    fun converteParaBuscaChavePIxResponse(chavePixInfo: ChavePixInfo): BuscaChavePixResponse? {
        return BuscaChavePixResponse.newBuilder()
            .setClientId(chavePixInfo.clienteId?.toString() ?: "")
            .setPixId(chavePixInfo.pixId?.toString() ?: "")
            .setChave(BuscaChavePixResponse.ChavePix.newBuilder()
                .setTipoDechave(TipoDeChave.valueOf(chavePixInfo.keyType.name))
                .setChave(chavePixInfo.key)
                .setConta(
                    BuscaChavePixResponse.ChavePix.ContaInfo.newBuilder()
                        .setTipo(TipoDeConta.valueOf(chavePixInfo.tipoDeConta.name))
                        .setInstituicao(chavePixInfo.banckAccountResponse.instituicao)
                        .setNomeDoTitular(chavePixInfo.banckAccountResponse.nomeDoTitular)
                        .setCpfDoTitular(chavePixInfo.banckAccountResponse.cpfTitular)
                        .setAgencia(chavePixInfo.banckAccountResponse.agencia)
                        .setNumeroDeConta(chavePixInfo.banckAccountResponse.numeroDaConta)
                        .build()
                )
                .setCriadoEm(LocalDateTime.parse(chavePixInfo.createdAt).run {
                    val createdAt = atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                }
                )
                .build())
            .build()
    }
}