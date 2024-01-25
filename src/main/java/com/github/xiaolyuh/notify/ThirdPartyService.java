package com.github.xiaolyuh.notify;

import com.alibaba.fastjson.JSONObject;
import com.github.xiaolyuh.Constants;
import com.github.xiaolyuh.action.options.MergeRequestOptions;
import com.github.xiaolyuh.git.GitFlowPlus;
import com.github.xiaolyuh.i18n.I18n;
import com.github.xiaolyuh.i18n.I18nKey;
import com.github.xiaolyuh.notify.hello.Base64Utils;
import com.github.xiaolyuh.notify.hello.IsvUtil;
import com.github.xiaolyuh.utils.ConfigUtil;
import com.github.xiaolyuh.utils.StringUtils;
import git4idea.repo.GitRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

/**
 * 第三方通知服务
 *
 * @author wyh
 */
public class ThirdPartyService {
    private static ThirdPartyService thirdPartyService = new ThirdPartyService();

    /**
     * 获取实例
     *
     * @return GbmGit
     */
    @NotNull
    public static ThirdPartyService getInstance() {
        return thirdPartyService;
    }

    GitFlowPlus gitFlowPlus = GitFlowPlus.getInstance();

    /**
     * 加锁通知
     *
     * @param repository repository
     */
    public void lockNotify(GitRepository repository) {
        try {
            String dingtalkToken = ConfigUtil.getConfig(repository.getProject()).get().getDingtalkToken();
            if (StringUtils.isNotBlank(dingtalkToken)) {
                String url = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", dingtalkToken);
                String msg = gitFlowPlus.getRemoteLastCommit(repository, Constants.LOCK_BRANCH_NAME);

                msg = String.format(I18n.getContent(I18nKey.THIRD_PARTY_NOTIFY), repository.getProject().getName(), msg);
                OkHttpClientUtil.postApplicationJson(url, new DingtalkMessage(msg), "钉钉通知接口", String.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Merge Request 通知
     *
     * @param repository repository
     */
    public void mergeRequestNotify(GitRepository repository, MergeRequestOptions mergeRequestOptions, String address) {
        try {
            if (!mergeRequestOptions.isNotice() || StringUtils.isBlank(mergeRequestOptions.getRecipient())) {
                return;
            }
            String email = gitFlowPlus.getUserEmail(repository);
            if (StringUtils.isNotBlank(email)) {
                email = email.split("@")[0];
            }
            sendHelloMessage(repository, mergeRequestOptions, address, email);
            sendDingtalkMessage(repository, mergeRequestOptions, address, email);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendHelloMessage(GitRepository repository, MergeRequestOptions mergeRequestOptions, String address, String submitter) {
        String helloToken = ConfigUtil.getConfig(repository.getProject()).get().getHelloToken();
        if (StringUtils.isNotBlank(helloToken) && StringUtils.isNotBlank(mergeRequestOptions.getRecipient())) {
            String sKey = Base64Utils.encodeToString(new byte[]{125, -90, -97, -15, -67, -38, -11, -3, -68, -31, -82, -4, -9, -66, 125, -13, 77, 61, -35, -18, -34, 115, -97, 90});
            String aKey = Base64Utils.encodeToString(new byte[]{-29, -99, -67, 125, -34, -100, -33, 110, 125, -29, -114, 117, -11, -33, 120, -17, 79, 90, 123, 119, -67, 115, 70, -33});
            String gKey = Base64Utils.encodeToString(new byte[]{-27, -26, -8, 127, -113, 28, 109, -73, -68, -31, -90, -37, -29, -42, -36, 109, -65, 52, -47, -10, -37, 121, -57, -35, -19, -33, 31, -33, -122, -34});
            String url = "https://hal" + "o-ap" + "i.long" + "hu.net/im-op" + "en-api-pr" + "od/_isv/bots/v2/w" + "ebhook/group/" + helloToken;

            Map<String, String> headers = new HashMap<>(8);
            headers.put("X-G" + "aia-A" + "pi-Key", gKey.replace("bb", "-"));
            headers.put("X-I" + "M-A" + "pp-Key", aKey);
            String sign = IsvUtil.createSign(sKey, aKey);
            headers.put("X-I" + "M-I" + "sv-A" + "ccess-S" + "ign", sign);
            headers.put("Content-Type", "application/json");
            JSONObject jsonObject = new JSONObject();

            String[] recipients = mergeRequestOptions.getRecipient().split(",");
            List<String> accounts = Arrays.stream(recipients)
                    .peek(String::trim)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toList());

            String message = accounts.stream().map(account -> "@" + account).collect(Collectors.joining(" "));
            message += " 【" + submitter + "】发起了Merge Request请求，麻烦您CR&Merge下代码: \r\n" + address;
            jsonObject.put("content", message);
            Map<String, Object> param = new HashMap<>();
            param.put("objectName", "RC:TxtMsg");
            param.put("content", jsonObject.toString());
            param.put("mentionType", 2);
            param.put("mentionAccounts", accounts);

            String result = OkHttpClientUtil.postApplicationJson(url, param, headers, "请求云图梭接口", String.class);
            System.out.println(result);
        }
    }

    private void sendDingtalkMessage(GitRepository repository, MergeRequestOptions mergeRequestOptions, String address, String submitter) {
        String dingtalkToken = ConfigUtil.getConfig(repository.getProject()).get().getDingtalkToken();
        if (StringUtils.isNotBlank(dingtalkToken) && StringUtils.isNotBlank(mergeRequestOptions.getRecipient())) {
            String url = String.format("https://oapi.dingtalk.com/robot/send?access_token=%s", dingtalkToken);

            String[] recipients = mergeRequestOptions.getRecipient().split(",");
            List<String> accounts = Arrays.stream(recipients)
                    .peek(String::trim)
                    .filter(StringUtils::isNotBlank).collect(Collectors.toList());

            String message = accounts.stream().map(account -> "@" + account).collect(Collectors.joining(" "));
            message += " 【" + submitter + "】发起了Merge Request请求，麻烦您CR&Merge下代码: \r\n" + address;

            OkHttpClientUtil.postApplicationJson(url, new DingtalkMessage(message), "钉钉通知接口", String.class);
        }
    }
}
