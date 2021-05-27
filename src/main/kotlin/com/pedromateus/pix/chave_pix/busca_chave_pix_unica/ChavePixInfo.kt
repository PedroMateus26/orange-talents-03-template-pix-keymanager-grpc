package com.pedromateus.pix.chave_pix.busca_chave_pix_unica

import com.pedromateus.pix.chave_pix.ChavePix
import com.pedromateus.pix.chave_pix.TipoDeChaveImpl
import com.pedromateus.pix.chave_pix.TipoDeContaImpl
import com.pedromateus.pix.chave_pix.nova_chave.conta_associada.ContaAssociada
import java.util.*

data class ChavePixInfo(
    val pixId: UUID?=null,
    val clienteId: UUID?=null,
    val keyType: TipoDeChaveImpl,
    val key:String,
    val banckAccountResponse: ContaAssociada,
    val tipoDeConta: TipoDeContaImpl,
    val createdAt:String
){
    companion object{
        fun of(chavePix: ChavePix): ChavePixInfo{
            return ChavePixInfo(
                pixId = chavePix.id,
                clienteId = chavePix.clienteId,
                keyType = chavePix.tipoDeChave!!,
                key=chavePix.chave!!,
                banckAccountResponse = chavePix.contaAssociada!!,
                tipoDeConta=chavePix.tipoDeConta!!,
                createdAt = chavePix.criadoEm.toString()
            )
        }
    }

}