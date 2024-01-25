package com.github.xiaolyuh.issue;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * @author wyh
 */
public class IssueCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
        result.addElement(
                LookupElementBuilder.create("feat(web) XM2301501-7874 新增拨测\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 新增拨测接口\n" +
                        "\n" +
                        "影响：\n" +
                        "无")
                        .withTypeText("repoName", true)
        );
        result.addElement(
                LookupElementBuilder.create("fix(web) XM2301501-7874 修复拨测\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 修复拨测接口\n" +
                        "\n" +
                        "影响：\n" +
                        "无")
                        .withTypeText("repoName", true)
        );
        result.addElement(
                LookupElementBuilder.create("docs(web) XM2301501-7874 修改拨测文档\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 修改拨测文档\n" +
                        "\n" +
                        "影响：\n" +
                        "无")
                        .withTypeText("repoName", true)
        );
        result.addElement(
                LookupElementBuilder.create("style(web) XM2301501-7874 修改样式\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 修改样式\n" +
                        "\n" +
                        "影响：\n" +
                        "无")
                        .withTypeText("repoName", true)
        );
        result.addElement(
                LookupElementBuilder.create("test(web) XM2301501-7874 新增拨测单测\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 新增拨测单测\n" +
                        "\n" +
                        "影响：\n" +
                        "无")
                        .withTypeText("repoName", true)
        );
        result.addElement(
                LookupElementBuilder.create("refactor(web) XM2301501-7874 拨测重构\n" +
                        "\n" +
                        "背景：\n" +
                        "    http://jira.longhu.net/browse/XM2301501-7874\n" +
                        "修改：\n" +
                        "1. 拨测接口重构\n" +
                        "\n" +
                        "影响：\n" +
                        "1. 拨测接口")
                        .withTypeText("repoName", true)
        );
    }
}
