package com.pedromateus.pix.shared.grpc

import com.pedromateus.pix.chave_pix.nova_chave.NovaChaveControlador
import com.pedromateus.pix.excecoes.GrpcException
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import java.lang.Exception
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionHandlerInterceptor : MethodInterceptor<NovaChaveControlador,Any>{

    private val logger=LoggerFactory.getLogger(this.javaClass)

    override fun intercept(context: MethodInvocationContext<NovaChaveControlador, Any>?): Any? {

        //antes
        logger.info("Interceptando método: ${context?.targetMethod}")

        try {
            context?.proceed() //processa o método interceptado
        } catch (e: Exception) {
            val erro=GrpcException.valueOf(e.javaClass.simpleName).error(e)
            val reponseError= context?.parameterValues?.get(1) as StreamObserver<*>
            reponseError.onError(erro)

        }

        return null

    }


}