package com.sharedaka.processor;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.javadoc.PsiDocTag;
import com.intellij.psi.javadoc.PsiDocTagValue;
import com.intellij.psi.javadoc.PsiDocToken;
import com.intellij.psi.tree.IElementType;
import com.sharedaka.utils.StringUtil;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @description: Processor 辅助类，用于抽离出 Processor 之间通用的方法
 * @author: 三岁
 * @date: 2022-08-06 10:58:36
 **/
public class ProcessorHelper {

    /**
     * 传递 PisClass 得到描述信息
     *
     * @param psiDocDocument PisClass
     * @return 类的描述信息
     */
    public static String getDescription(PsiDocComment psiDocDocument) {
        if (psiDocDocument == null) {
            return "";
        }
        return joinPsiElements(psiDocDocument.getDescriptionElements());
    }

    /**
     * 解析方法上的所有 "@param" 标签，将参数名作为 key，将参数描述作为 value
     *
     * @param psiMethod Psi方法
     * @return paramNameToParamValue
     */
    public static Map<String, String> parseMethodParamTags(PsiMethod psiMethod) {
        PsiDocTag[] psiDocTags = Optional.ofNullable(psiMethod).map(PsiMethod::getDocComment).map(PsiDocComment::getTags).orElse(null);
        if (psiDocTags == null || psiDocTags.length == 0) {
            return Collections.emptyMap();
        }

        Map<String, String> paramNameToParamValue = new HashMap<>(psiDocTags.length);
        for (PsiDocTag psiDocTag : psiDocTags) {
            if (!"param".equals(psiDocTag.getName())) {
                // 只处理 "@param" 标签
                continue;
            }

            PsiDocTagValue valueElement = psiDocTag.getValueElement();
            PsiElement[] psiDataElements = psiDocTag.getDataElements();
            if (valueElement == null || psiDataElements.length == 0) {
                continue;
            }

            paramNameToParamValue.put(StringUtil.removeSpace(valueElement.getText()), joinPsiElements(psiDataElements));
        }
        return paramNameToParamValue;
    }

    private static String joinPsiElements(PsiElement[] psiElements) {
        if (psiElements == null || psiElements.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (PsiElement psiElement : psiElements) {
            if (psiElement instanceof PsiDocToken) {
                sb.append(psiElement.getText());
            }
        }
        return StringUtil.removeSpace(sb.toString());
    }
}
