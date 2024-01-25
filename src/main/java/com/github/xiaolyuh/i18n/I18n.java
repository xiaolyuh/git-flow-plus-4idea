package com.github.xiaolyuh.i18n;

import com.github.xiaolyuh.action.options.LocalConfig;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * 国际化
 *
 * @author yuhao.wang3
 * @since 2020/4/7 19:39
 */
public class I18n {
    private static volatile Properties properties;

    public static void loadLanguageProperties(LanguageEnum language) {
        try {
            String fileName = language.getFile();
            // 加载资源文件
            try (InputStream in = I18n.class.getClassLoader().getResourceAsStream(fileName)) {
                properties = new Properties();
                properties.load(in);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getContent(String key) {
        if (Objects.isNull(properties)) {
            LanguageEnum language = LocalConfig.getInstance().language;
            loadLanguageProperties(language);
        }
        return properties.getProperty(key);
    }

}
