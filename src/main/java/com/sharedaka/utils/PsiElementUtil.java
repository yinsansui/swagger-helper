package com.sharedaka.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PsiElementUtil {

    public static void importPackage(PsiElementFactory elementFactory, PsiFile file, Project project, String className) {
        if (!(file instanceof PsiJavaFile)) {
            return;
        }
        final PsiJavaFile javaFile = (PsiJavaFile) file;
        final PsiImportList importList = javaFile.getImportList();
        if (importList == null) {
            return;
        }
        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project).getClassesByName(className, GlobalSearchScope.allScope(project));
        // todo 导入类类名重复问题
        PsiClass waiteImportClass = psiClasses[0];
        for (PsiImportStatementBase is : importList.getAllImportStatements()) {
            String impQualifiedName = is.getImportReference().getQualifiedName();
            if (waiteImportClass.getQualifiedName().equals(impQualifiedName)) {
                // 已经导入
                return;
            }
        }
        importList.add(elementFactory.createImportStatement(waiteImportClass));
    }

    /**
     * 获取指定的注解
     *
     * @param psiModifierListOwner PsiClass 或者 PsiMethod
     * @param fullQualifiedName    注解的全类名
     * @return 注解
     */
    public static PsiAnnotation getAnnotation(PsiModifierListOwner psiModifierListOwner, String fullQualifiedName) {
        return psiModifierListOwner == null ? null : psiModifierListOwner.getAnnotation(fullQualifiedName);
    }

    /**
     * 是否包含指定注解
     *
     * @param psiModifierListOwner PsiClass 或者 PsiMethod
     * @param fullQualifiedName    注解的全类名
     * @return 是否包含
     */
    public static boolean hasAnnotation(PsiModifierListOwner psiModifierListOwner, String fullQualifiedName) {
        return psiModifierListOwner != null && psiModifierListOwner.hasAnnotation(fullQualifiedName);
    }

    public static boolean isPipeline(PsiCallExpression callExpression) {
        PsiElement[] children = callExpression.getChildren();
        for (PsiElement child : children) {
            for (PsiElement psiElement : child.getChildren()) {
                if (psiElement instanceof PsiMethodCallExpression) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isComplexCall(PsiCallExpression callExpression) {
        PsiExpressionList argumentList = callExpression.getArgumentList();
        if (argumentList != null) {
            PsiExpression[] expressions = argumentList.getExpressions();
            for (PsiExpression expression : expressions) {
                if (expression instanceof PsiCallExpression) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 一直向上查找直到查找到第一个 PsiClass
     *
     * @param element 元素
     * @return 查找到的第一个 PsiClass
     */
    public static PsiClass lookupPsiClass(PsiElement element) {
        if (element instanceof PsiClass) {
            return (PsiClass) element;
        }
        PsiElement psiClass = element.getParent();
        while (psiClass != null && !(psiClass instanceof PsiClass)) {
            psiClass = psiClass.getParent();
        }
        return (PsiClass) psiClass;
    }

    /**
     * 向上查找直到查找第一个 PsiMethod
     *
     * @param element 元素
     * @return 查找到的第一个 PsiMethod
     */
    public static PsiMethod lookupPsiMethod(PsiElement element) {
        if (element instanceof PsiMethod) {
            return (PsiMethod) element;
        }
        PsiElement psiMethod = element.getParent();
        while (psiMethod != null && !(psiMethod instanceof PsiMethod)) {
            psiMethod = psiMethod.getParent();
        }
        return (PsiMethod) psiMethod;
    }

    public static boolean isAbstractMethod(PsiMethod psiMethod) {
        return (psiMethod.getContainingClass() != null && psiMethod.getContainingClass().isInterface()) || psiMethod.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT);
    }


}
