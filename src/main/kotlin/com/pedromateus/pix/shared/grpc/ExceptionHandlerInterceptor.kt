package com.pedromateus.pix.shared.grpc

import com.pedromateus.pix.excecoes.ApiErrorException
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {


        try {
            context.proceed() //processa o método interceptado
        } catch (e: Exception) {
            logger.error(e.message)
            e.printStackTrace()

            val erro = when (e) {
                is ApiErrorException -> e.statusGrpc
                    .withDescription(e.message)
                    .asRuntimeException()
                is IllegalArgumentException->Status.INVALID_ARGUMENT
                    .withDescription("Dados de entrada inválidos!")
                    .asRuntimeException()
                else -> Status.UNKNOWN
                    .withDescription(e.message)
                    .asRuntimeException()
            }
            val reponseError = context.parameterValues[1] as StreamObserver<*>
            reponseError.onError(erro)

        }

        return null

    }


}