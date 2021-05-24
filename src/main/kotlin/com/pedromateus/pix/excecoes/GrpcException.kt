package com.pedromateus.pix.excecoes

import io.grpc.Status
import io.grpc.StatusRuntimeException

enum class GrpcException {

    ConstraintViolationException {
        override fun error(exception: Exception): StatusRuntimeException? {
            return Status.FAILED_PRECONDITION
                .withDescription(exception.localizedMessage)
                .asRuntimeException()
        }
    },
    InvalidArgumentExceptions {

        override fun error(exception: Exception): StatusRuntimeException? {
            return Status.INVALID_ARGUMENT
                .withDescription(exception.message)
                .asRuntimeException()
        }
    };
    abstract fun error(exception: Exception): StatusRuntimeException?
}
