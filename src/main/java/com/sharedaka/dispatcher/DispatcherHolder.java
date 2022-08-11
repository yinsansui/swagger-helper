package com.sharedaka.dispatcher;

public class DispatcherHolder {

    private volatile static SwaggerActionDispatcher swaggerActionDispatcher;

    private static final Object LOCK = new Object();

    public static SwaggerActionDispatcher getSwaggerApiControllerProcessor() {
        if (swaggerActionDispatcher == null) {
            synchronized (LOCK) {
                if (swaggerActionDispatcher == null) {
                    swaggerActionDispatcher = new SwaggerActionDispatcher();
                }
            }
        }
        return swaggerActionDispatcher;
    }
}
