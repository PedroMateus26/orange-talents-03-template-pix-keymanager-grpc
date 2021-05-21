package com.pedromateus.pix.chave_pix.removendo_chave

data class RemovePixRequest(
    val pixId:String,
    val clienteId:String
) {
}