package com.github.xiaolyuh.action.options;

import com.github.xiaolyuh.i18n.LanguageEnum;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 插件配置持久化
 *
 * @author wyh
 */
@State(name = "GitFlowPlusLocalConfig", storages =@Storage(file = ".idea/GitFlowPlus.xml"))
public class LocalConfig implements PersistentStateComponent<LocalConfig> {

    public static LocalConfig getInstance() {
        return ServiceManager.getService(LocalConfig.class);
    }

    /**
     * 语言
     */
    public LanguageEnum language = LanguageEnum.CN;

    /**
     * 消息接收人
     */
    public String recipient = "";

    /**
     * 是否启用 Commit 信息处理器
     */
    public boolean commitCheck = true;

    /**
     * 是否启用 Commit 信息同步
     */
    public boolean commitSync = true;

    @Override
    public @Nullable LocalConfig getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull LocalConfig state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
