package com.sharedaka.processor.business;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.sharedaka.constant.spring.SpringMvcAnnotations;
import com.sharedaka.constant.swagger.SwaggerAnnotations;
import com.sharedaka.entity.annotation.swagger.ApiModelEntity;
import com.sharedaka.entity.annotation.swagger.ApiModelPropertyEntity;
import com.sharedaka.parser.ParserHolder;
import com.sharedaka.processor.annotation.swagger.ApiModelProcessor;
import com.sharedaka.processor.annotation.swagger.ApiModelPropertyProcessor;
import com.sharedaka.utils.PsiAnnotationUtil;
import com.sharedaka.utils.PsiElementUtil;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class SwaggerApiModelProcessor implements ClassSupportable {

    @Override
    public boolean support(PsiClass psiClass) {
        return isApiModel(psiClass);
    }

    /**
     * 判断一个类是否是 Pojo 类
     *
     * @param psiClass 类
     * @return 是否是 pojo
     */
    private boolean isApiModel(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        // 不能是枚举和接口
        if (psiClass.isEnum() || psiClass.isInterface()) {
            return false;
        }

        // 如果声明为 Spring Bean 则也不会是 ApiModel
        for (PsiAnnotation annotation : psiClass.getAnnotations()) {
            if (SpringMvcAnnotations.WITH_COMPONENT_ANNOTATIONS.contains(annotation.getQualifiedName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void process(PsiClass psiClass) {
        Project project = psiClass.getProject();
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        processApiModel(elementFactory, psiClass);
        processApiModelProperty(elementFactory, psiClass);
    }

    private void processApiModel(PsiElementFactory elementFactory, PsiClass psiClass) {
        PsiAnnotation existedApiModel = PsiElementUtil.getAnnotation(psiClass, SwaggerAnnotations.SWAGGER_API_MODEL);
        ApiModelEntity apiModel = ApiModelProcessor.createByPsiClass(psiClass);
        if (existedApiModel != null) {
            ApiModelEntity existedApiModelEntity = (ApiModelEntity) ParserHolder.getAnnotationProcessor(SwaggerAnnotations.SWAGGER_API_MODEL).parse(existedApiModel);
            ApiModelProcessor.mergeApiAnnotation(existedApiModelEntity, apiModel);
        }
        PsiAnnotationUtil.writeAnnotation(elementFactory, "ApiModel", SwaggerAnnotations.SWAGGER_API_MODEL, ApiModelProcessor.createAnnotationString(apiModel), psiClass);
    }

    private void processApiModelProperty(PsiElementFactory elementFactory, PsiClass psiClass) {
        PsiField[] psiFields = psiClass.getFields();
        for (PsiField psiField : psiFields) {
            PsiAnnotation apiModelPropertyAnnotation = PsiElementUtil.getAnnotation(psiField, SwaggerAnnotations.SWAGGER_API_MODEL_PROPERTY);
            ApiModelPropertyEntity apiModelProperty = ApiModelPropertyProcessor.createByPsiField(psiField);
            if (apiModelPropertyAnnotation != null) {
                ApiModelPropertyEntity existedApiModelProperty = (ApiModelPropertyEntity) ParserHolder.getAnnotationProcessor(SwaggerAnnotations.SWAGGER_API_MODEL_PROPERTY).parse(apiModelPropertyAnnotation);
                ApiModelPropertyProcessor.mergeApiAnnotation(existedApiModelProperty, apiModelProperty);
            }
            PsiAnnotationUtil.writeAnnotation(elementFactory, "ApiModelProperty", SwaggerAnnotations.SWAGGER_API_MODEL_PROPERTY, ApiModelPropertyProcessor.createAnnotationString(apiModelProperty), psiField);
        }
    }
}
