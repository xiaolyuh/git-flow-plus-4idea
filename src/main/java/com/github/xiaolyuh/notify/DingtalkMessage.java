package com.github.xiaolyuh.notify;

/**
 * @author yuhao.wang3
 * @since 2020/3/27 16:21
 */
public class DingtalkMessage {

    public DingtalkMessage(String content) {
        this.text.setContent(content);
    }

    private String msgtype;
    private Text text = new Text();
    private At at = new At();

    public String getMsgtype() {
        return "text";
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public At getAt() {
        return at;
    }

    public static class Text {
        /**
         * content : test
         */

        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class At {
        /**
         * isAtAll : false
         */

        private boolean isAtAll = false;

        public boolean isIsAtAll() {
            return isAtAll;
        }
    }
}
