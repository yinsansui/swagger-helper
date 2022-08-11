package com.sharedaka.dispatcher;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiUtilBase;
import com.sharedaka.processor.ProcessorHolder;
import com.sharedaka.processor.business.SwaggerApiControllerProcessor;
import com.sharedaka.processor.business.SwaggerApiMethodProcessor;
import com.sharedaka.processor.business.SwaggerApiModelProcessor;
import com.sharedaka.utils.PsiElementUtil;

/**
 * Swagger-Helper由一个按钮作为统一入口
 * 根据用户光标所在位置进行进行不同的处理
 * 因此需要一个分发器进行功能判断
 * 该类负责分发功能
 *
 * @author math312
 */
public class SwaggerActionDispatcher {

    public boolean support(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return false;
        }

        int startOffset = editor.getSelectionModel().getSelectionStart();
        PsiElement selectedElement = PsiUtilBase.getElementAtOffset(psiFile, startOffset);

        PsiMethod psiMethod = null;
        PsiClass psiClass = null;
        if ((psiMethod = PsiElementUtil.lookupPsiMethod(selectedElement)) != null) {
            return ProcessorHolder.getSwaggerApiMethodProcessor().support(PsiElementUtil.lookupPsiClass(psiMethod), psiMethod);
        } else if ((psiClass = PsiElementUtil.lookupPsiClass(selectedElement)) != null) {
            return ProcessorHolder.getSwaggerApiControllerProcessor().support(psiClass) || ProcessorHolder.getSwaggerApiModelProcessor().support(psiClass);
        }

        return false;
    }

    public void dispatcher(AnActionEvent actionEvent) {
        Editor editor = actionEvent.getData(CommonDataKeys.EDITOR);
        PsiFile psiFile = actionEvent.getData(CommonDataKeys.PSI_FILE);
        if (editor == null || psiFile == null) {
            return;
        }

        // todo 细化选择控制
        doDispatcher(psiFile, editor);
    }

    private void doDispatcher(PsiFile psiFile, Editor editor) {
        int startOffset = editor.getSelectionModel().getSelectionStart();
        PsiElement selectedElement = PsiUtilBase.getElementAtOffset(psiFile, startOffset);

        PsiClass psiClass = null;
        PsiMethod psiMethod = PsiElementUtil.lookupPsiMethod(selectedElement);
        if (psiMethod != null) {
            WriteCommandAction.runWriteCommandAction(psiFile.getProject(), () -> {
                SwaggerApiMethodProcessor swaggerApiMethodProcessor = ProcessorHolder.getSwaggerApiMethodProcessor();
                if (swaggerApiMethodProcessor.support(PsiElementUtil.lookupPsiClass(psiMethod), psiMethod)) {
                    swaggerApiMethodProcessor.process(psiMethod);
                }
            });
        } else if ((psiClass = PsiElementUtil.lookupPsiClass(selectedElement)) != null) {
            PsiClass finalPsiClass = psiClass;
            WriteCommandAction.runWriteCommandAction(psiFile.getProject(), () -> {
                SwaggerApiControllerProcessor swaggerApiControllerProcessor = ProcessorHolder.getSwaggerApiControllerProcessor();
                if (swaggerApiControllerProcessor.support(finalPsiClass)) {
                    swaggerApiControllerProcessor.process(finalPsiClass);
                }
                SwaggerApiModelProcessor swaggerApiModelProcessor = ProcessorHolder.getSwaggerApiModelProcessor();
                if (swaggerApiModelProcessor.support(finalPsiClass)) {
                    swaggerApiModelProcessor.process(finalPsiClass);
                }
            });
        }

    }

}

