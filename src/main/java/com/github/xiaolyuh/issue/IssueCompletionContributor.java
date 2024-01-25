package com.github.xiaolyuh.issue;

import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiPlainText;

/**
 * @author wyh
 */
public class IssueCompletionContributor extends CompletionContributor {
    public IssueCompletionContributor() {
        super.extend(CompletionType.BASIC, PlatformPatterns.psiElement(PsiPlainText.class), new IssueCompletionProvider());
    }
}