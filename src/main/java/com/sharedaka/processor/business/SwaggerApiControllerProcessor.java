package com.sharedaka.processor.business;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.sharedaka.constant.spring.SpringMvcAnnotations;
import com.sharedaka.constant.swagger.SwaggerAnnotations;
import com.sharedaka.entity.annotation.swagger.ApiEntity;
import com.sharedaka.parser.ParserHolder;
import com.sharedaka.processor.annotation.swagger.ApiProcessor;
import com.sharedaka.utils.PsiAnnotationUtil;
import com.sharedaka.utils.PsiElementUtil;

public class SwaggerApiControllerProcessor implements ClassSupportable {

    @Override
    public boolean support(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }
        // 包含 RestController 或者 Controller 任何一个注解就支持
        return PsiElementUtil.hasAnnotation(psiClass, SpringMvcAnnotations.REST_CONTROLLER_ANNOTATION_NAME) ||
                PsiElementUtil.hasAnnotation(psiClass, SpringMvcAnnotations.CONTROLLER_ANNOTATION_NAME);
    }

    @Override
    public void process(PsiClass psiClass) {
        Project project = psiClass.getProject();
        PsiElementFactory elementFactory = JavaPsiFacade.getElementFactory(project);
        processSwaggerApiAnnotation(elementFactory, psiClass);
    }

    private static void processSwaggerApiAnnotation(PsiElementFactory elementFactory, PsiClass psiClass) {
        PsiAnnotation psiAnnotation = PsiElementUtil.getAnnotation(psiClass, SwaggerAnnotations.SWAGGER_API_ANNOTATION_NAME);
        ApiEntity apiAnnotation = ApiProcessor.createByPsiClass(psiClass);
        if (psiAnnotation != null) {
            ApiEntity existedApiAnnotation = (ApiEntity) ParserHolder.getAnnotationProcessor(SwaggerAnnotations.SWAGGER_API_ANNOTATION_NAME).parse(psiAnnotation);
            ApiProcessor.mergeApiAnnotation(existedApiAnnotation, apiAnnotation);
        }
        String apiAnnotationStr = ApiProcessor.createAnnotationString(apiAnnotation);
        PsiAnnotationUtil.writeAnnotation(elementFactory, "Api", "io.swagger.annotations.Api", apiAnnotationStr, psiClass);
    }
}
