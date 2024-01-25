package com.github.xiaolyuh.i18n;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuhao.wang3
 * @since 2020/4/7 20:20
 */
public enum LanguageEnum {
    /**
     * Constants
     */
    CN("中文", "中文", "language_cn.properties"),
    EN("English", "英文", "language_en.properties");

    private final String language;
    private final String file;

    LanguageEnum(String language, String lable, String file) {
        this.language = language;
        this.file = file;
    }

    public static LanguageEnum getByLanguage(String language) {
        for (LanguageEnum anEnum : values()) {
            if (anEnum.getLanguage().equals(language)) {
                return anEnum;
            }
        }
        return CN;
    }

    public static String getByLanguageFile(String language) {
        for (LanguageEnum anEnum : values()) {
            if (anEnum.getLanguage().equals(language)) {
                return anEnum.getFile();
            }
        }
        return CN.getFile();
    }

    public static List<String> getAllLanguage() {
        List<String> languages = new ArrayList<>();
        for (LanguageEnum anEnum : values()) {
            languages.add(anEnum.getLanguage());
        }
        return languages;
    }

    public String getLanguage() {
        return language;
    }

    public String getFile() {
        return file;
    }
}
