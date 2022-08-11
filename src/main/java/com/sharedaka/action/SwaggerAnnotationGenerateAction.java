package com.sharedaka.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.sharedaka.dispatcher.DispatcherHolder;
import org.jetbrains.annotations.NotNull;

/**
 * @description: Swagger 注解生成
 * @author: yinxiaoyang
 * @date: 2022-08-11 23:19:08
 **/
public class SwaggerAnnotationGenerateAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent anActionEvent) {
        // 判断是否需要展示 ”generate swagger annotation“ 选项
        anActionEvent.getPresentation().setEnabledAndVisible(DispatcherHolder.getSwaggerApiControllerProcessor().support(anActionEvent));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        DispatcherHolder.getSwaggerApiControllerProcessor().dispatcher(anActionEvent);
    }


}
