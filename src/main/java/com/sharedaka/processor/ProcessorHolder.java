package com.sharedaka.processor;

import com.sharedaka.processor.business.SwaggerApiControllerProcessor;
import com.sharedaka.processor.business.SwaggerApiMethodProcessor;
import com.sharedaka.processor.business.SwaggerApiModelProcessor;

/**
 * Swagger Helper现提供三个功能，其中两个与类相关，一个与方法相关，三个功能对应三个处理器
 * 1. Class相关：
 * - 实体类生成@ApiModel相关注解   - SwaggerApiModelProcessor
 * - Controller类生成@Api相关注解 - SwaggerApiControllerProcessor
 * 2. Method相关：
 * - 生成对应Controller注释       - SwaggerApiMethodProcessor
 * 该类负责对处理器进行管理
 */
public class ProcessorHolder {

    private static final class SwaggerApiControllerProcessorHolder {
        static final SwaggerApiControllerProcessor SWAGGER_API_CONTROLLER_PROCESSOR = new SwaggerApiControllerProcessor();
    }

    public static SwaggerApiControllerProcessor getSwaggerApiControllerProcessor() {
        return SwaggerApiControllerProcessorHolder.SWAGGER_API_CONTROLLER_PROCESSOR;
    }

    private static final class SwaggerApiMethodProcessorHolder {
        static final SwaggerApiMethodProcessor SWAGGER_API_METHOD_PROCESSOR = new SwaggerApiMethodProcessor();
    }

    public static SwaggerApiMethodProcessor getSwaggerApiMethodProcessor() {
        return SwaggerApiMethodProcessorHolder.SWAGGER_API_METHOD_PROCESSOR;
    }

    private static final class SwaggerApiModelProcessorHolder {
        static final SwaggerApiModelProcessor SWAGGER_API_MODEL_PROCESSOR = new SwaggerApiModelProcessor();
    }

    public static SwaggerApiModelProcessor getSwaggerApiModelProcessor() {
        return SwaggerApiModelProcessorHolder.SWAGGER_API_MODEL_PROCESSOR;
    }
}
