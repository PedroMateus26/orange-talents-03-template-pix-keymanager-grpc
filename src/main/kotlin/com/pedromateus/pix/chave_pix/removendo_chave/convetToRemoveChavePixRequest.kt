package com.pedromateus.pix.chave_pix.removendo_chave

import com.pedromateus.pix.UsuarioPixRemoveRequest

fun UsuarioPixRemoveRequest.convertToRemoveChavePixRequest():RemoveChavePixRequest{
    val clienteId=this.clienteID
    val chavepixId=this.chavePix
    return RemoveChavePixRequest(clienteId,chavepixId)
}