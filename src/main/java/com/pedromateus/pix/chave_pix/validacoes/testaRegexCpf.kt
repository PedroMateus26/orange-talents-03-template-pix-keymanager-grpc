package com.pedromateus.pix.chave_pix.validacoes

import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.Status
import io.micronaut.http.HttpStatus
import org.hibernate.validator.constraints.br.CPF

@CPF
fun testaRegexCpf(chave:String):Boolean{
     with(chave){
          return  chave.matches("([0-9]{3}[.]?[0-9]{3}[.]?[0-9]{3}-[0-9]{2})|([0-9]{11})".toRegex())
              .or(chave.matches("^(?:(?!000\\.?000\\.?000-?00).)*$".toRegex()))
              .or(chave.matches("^(?:(?!111\\.?111\\.?111-?11).)*$".toRegex()))
              .or(chave.matches("^(?:(?!222\\.?222\\.?222-?22).)*$".toRegex()))
              .or(chave.matches("^(?:(?!333\\.?333\\.?333-?33).)*$".toRegex()))
              .or(chave.matches("^(?:(?!444\\.?444\\.?444-?44).)*$".toRegex()))
              .or(chave.matches("^(?:(?!555\\.?555\\.?555-?55).)*$".toRegex()))
              .or(chave.matches("^(?:(?!666\\.?666\\.?666-?66).)*$".toRegex()))
              .or(chave.matches("^(?:(?!777\\.?777\\.?777-?77).)*$".toRegex()))
              .or(chave.matches("^(?:(?!888\\.?888\\.?888-?88).)*$".toRegex()))
              .or(chave.matches("^(?:(?!999\\.?999\\.?999-?99).)*$".toRegex()))
    }
}