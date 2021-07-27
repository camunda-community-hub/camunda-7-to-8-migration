public aspect UnsupportedOperationAspect {
    // This the point-cut matching calls to methods annotated with your 
    // @UnsupportedOperation annotation.
    pointcut unsupportedMethodCalls() : call(@UnsupportedOperation * *.*(..));
    
    // Declare an error for such calls. This causes a compilation error 
    // if the point-cut matches any unsupported calls.
    declare error: unsupportedMethodCalls() : "This call is not supported."
    
    // Or you can just throw an exception just before this call executed at runtime
    // instead of a compile-time error.
    before() : unsupportedMethodCalls() {
        throw new UnsupportedOperationException(thisJoinPoint.getSignature()
            .getName());
    }
}