# 简介
[GitFlowPlus](https://plugins.jetbrains.com/plugin/14056-gitflowplus) 插件是一款基于[mrtf-git-flow](https://xiaolyuh.blog.csdn.net/article/details/105180250)分支管理流程的Idea插件，它最主要的作用是用来简化分支管理流程，最大限度的防止误操作。

![mrrtf.png](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/mrrtf.png)

主要功能如下：
- 基于主干分支快速新建开发分支和修复分支；
- 基于主干分支快速重建测试分支和发布分支；
- 开发完成后将快速将开发分支合并到测试分支；
- 开发完成后快速在IDEA发起Merge Request到远程目标分支；
- 发布完成后快速将发布分支合并到主干分支并打TAG；

# 主要解决的问题
1. 简化日常工作中分支操作步骤，比如新建分支、提测、发布、Merge Request等操作；
2. 降低分支操作过程中发生错误的概率；
3. 通过新增临时分支策略来解决Merge Request冲突问题；


# Switch To English
![switch_to_english.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/switch_to_english.gif)

# 安装
## 在线安装
直接在IDEA插件市场搜索```GitFlowPlus```，如图：

![local_install.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/online_install.gif)

## 离线安装
下载地址: [https://github.com/xiaolyuh/mrtf-git-flow-4idea/releases](https://github.com/xiaolyuh/mrtf-git-flow-4idea/releases)

![local_install.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/local_install.gif)

# 插件入口
![插件入口.png](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/main.png)

插件入口有2个：
1. 在Toolbar栏，这个需要显示Toolbar（View->Appearance->Toolbar）
2. 在Statusbar中

# 插件配置
每个仓库都需要进行插件初始化，配置完成后会生成一个```git-flow-plus.config```配置文件，**该文件需要添加到git版本管理中进行组内同步**，同步完成后组内成员可以共享配置。

> 1. 必须配置**主干分支**、**发布分支**、**测试分支**，这三个分支在仓库中已经存在，且不能是同一个分支。
> 2. 必须配置git全局用户信息
```git
git config --global user.name "hitsjt"
git config --global user.email "hitsjt@gmail.com"
```


![init.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/init.gif)


如果配置了钉钉机器人Token，那么在点击[开始发布]的时候，钉钉机器人会在钉钉群发布一条发布分支被锁定的消息，格式如下：
```
xxx 服务发布分支已被锁定，最后一次操作：

  操作人: yuhao.wang3@xxx.com;

  时间: 2020-03-27_16:38:09;

  Message: 初始化插件配置 ;

如需强行发布，请先点[发布失败]解除锁定，再点[开始发布]。
```

# 新建分支
新建开发分支和修复分支都会直接从最新的**主干**分支新切一个开发分支出来。

![new_branch.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/new_branch.gif)

执行命令：
```git
git fetch origin master:newBranchName
git checkout newBranchName --force
git push origin newBranchName:newBranchName --tag  --set-upstream
```

> **如果本地有未提交文件是不允许新建和重建分支的**


# 重建测试分支
重建测试分支会直接从最新的**主干**分支新切一个测试分支出来，原来的测试分支会被直接删除。

> 如果当前的发布分支处于锁定状态，那么将不允许重建发布分支。

执行命令：
```git
git checkout master --force
git push origin --delete rebuildBranchName
git branch -D rebuildBranchName

git fetch origin master:rebuildBranchName
git checkout rebuildBranchName --force
git push origin rebuildBranchName:rebuildBranchName --tag  --set-upstream
```

# 重建发布分支
重建发布分支会直接从最新的**主干**分支新切一个发布分支出来，原来的发布分支会被直接删除。

> 如果当前的发布分支处于锁定状态，那么将不允许重建发布分支。

执行命令和重建测试分支一样。

# Merge Request
```Merge Request```会提取最后一次提交信息作为```Merge Request```的信息，且**会根据当前开发分支新建一个临时用于MR操作**。提交代码规范可以参考：
```
feat(web) XM2231501-5401【公务用车】包车预约时间取消司机端限制

背景:
解决订单自动收车漏洞
修改：
1. 新增包车白名单
影响：
1、包车下单
```

![merge-request.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/merge-request.gif)

执行命令：
```git
git -c core.quotepath=false -c log.showSignature=false checkout release --force
git -c core.quotepath=false -c log.showSignature=false push origin --delete release_mr
git -c core.quotepath=false -c log.showSignature=false branch -D release_mr
git -c core.quotepath=false -c log.showSignature=false checkout release --force
git -c core.quotepath=false -c log.showSignature=false branch release_mr
git -c core.quotepath=false -c log.showSignature=false checkout release_mr --force
git -c core.quotepath=false -c log.showSignature=false checkout master --force
git -c core.quotepath=false -c log.showSignature=false pull origin master:master
git -c core.quotepath=false -c log.showSignature=false checkout release_mr --force
git -c core.quotepath=false -c log.showSignature=false merge master -m "Merge branch 'master' into release_mr"
git -c core.quotepath=false -c log.showSignature=false checkout master --force
git -c core.quotepath=false -c log.showSignature=false push origin release_mr:release_mr --set-upstream -o merge_request.create -o merge_request.target=master -o merge_request.remove_source_branch -o merge_request.label=feat -o "merge_request.title=feat(web) 插件初始化" -o "merge_request.description=
背景：
我团收藏&推荐列表能查看自己
修改：
1. 我团收藏&推荐列表能查看自己
影响：
无
"

git -c core.quotepath=false -c log.showSignature=false checkout release --force
git -c core.quotepath=false -c log.showSignature=false branch -D release_mr
```

# 云图梭消息配置
1. 配置云图梭机器人&消息接收人OA（群主三连击群名称三个字可以获取机器人token）
2. 在 Merge Request 时可以修改消息通知人，也可以选择是否送消息
3. 消息接受者在云图梭收到消息后可以直接点击链接地址即可以进行Code Review操作

![hello.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/hello.gif)


# 提测
提测会将当前分支合并到**测试**分支，在合并过程中如果出现冲突并且选择未解决，那么当前分支会切换到本地```test分支```，等待解决冲突；如果没有任何异常情况，那么合并完成后当前分支不会发生切换。

> **当前分支必须是开发分支或者修复分支时，才允许提测。**

![test.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/test.gif)

执行命令：
测试分支在远程和本地都不存在，会新建测试分支：
```
git fetch origin master:test
git checkout test --force
git push origin test:test --tag  --set-upstream
```
测试分支在本地不存在，会在本地新建测试分支：
```
git checkout -b test origin/test
```

测试分支存在：
```git
git checkout test --force
git pull origin test:test
git megre featureBranchName (如果没解决冲突后面语句不执行)
git push origin test:test --tag  --set-upstream
git checkout featureBranchName --force
```

# 开始发布
开始发布会将当前分支合并到**发布分支**，如果配置了钉钉的机器人Token，那么还会往钉钉群发送一条发布分支锁定消息。

发布分支一旦锁定后，其他人将不能再进行发布，如果确实需要发布有两种解决方式：
1. 让第一个发布人点发布完成，发布完成会将发布分支合并到```origin/master```，并解除发布分支锁定。
2. 让第一个发布人点发布失败，发布失败将直接解除发布分支锁定。

![release.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/release.gif)

执行命令：
加锁
```git
git push origin featureBranchName:GFP_LOCK_BRANCH_NAME  (返回 new branch 则成功获取锁)
```
获取最后一次操作人：
```git
git show  origin/release -s --format=Author:%ae-Date:%ad-Message:%s --date=format:%Y-%m-%d_%H:%M:%S
```

发布分支在远程和本地都不存在，会新建发布分支：
```
git fetch origin master:release
git checkout release --force
git push origin release:release --tag  --set-upstream
```
发布分支在本地不存在，会在本地新建发布分支：
```
git checkout -b release origin/release
```

发布分支存在：
```git
git checkout release --force 
git pull origin release:release
git megre featureBranchName (如果没解决冲突后面语句不执行)
git push origin release:release --tag  --set-upstream
git checkout featureBranchName --force
```

# 发布完成
发布完成会将发布分支合并到```origin/master```，并解除发布分支锁定，必须打Tag；
> 只有发布分支处于锁定状态，该按钮才可用

![finish_release.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/finish_release.gif)

执行命令：
先fetch在判断发布分支是否处于锁定状态：
```git
git fetch origin 
```

主干分支在本地不存在，会在本地新建主干分支：
```
git checkout -b master origin/master
```

主干分支存在：
```git
git checkout master --force
git pull origin master:master
git megre featureBranchName (如果没解决冲突后面语句不执行)
git tag -a -f -m message tagName
git push origin master:master --tag  --set-upstream 
git checkout featureBranchName --force 
git push origin --delete GFP_LOCK_BRANCH_NAME（解锁）
```

# 发布失败
直接解除发布分支锁定。
> 只有发布分支处于锁定状态，该按钮才可用

![failure_release.gif](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/failure_release.gif)

执行命令：
先fetch在判断发布分支是否处于锁定状态：
```git
git fetch origin （更新本地仓库缓存）
```

解锁
```git
git push origin --delete GFP_LOCK_BRANCH_NAME（解锁）
```

# 注意事项
> 插件初始化时必须配置**主干分支**、**发布分支**、**测试分支**，主干分支必须存在且不能和发布分支是同一个分支。
> 每次使用插件操作分支时需保证本地文件均一提交。
> 需要如下命令 ```git config pull.rebase false```。


# 作者信息
作者博客：[https://xiaolyuh.blog.csdn.net/](https://xiaolyuh.blog.csdn.net/)
作者邮箱： xiaolyuh@163.com  
github 地址：https://github.com/wyh-chenfeng/layering-cache


# 捐赠
项目的发展离不开你的支持，请作者喝杯咖啡吧！

![donate.png](https://github.com/xiaolyuh/mrtf-git-flow-4idea/blob/master/images/donate.png)


# 技术支持
添加微信记得备注 ```GitFlowPlus```。

![微信](https://github.com/xiaolyuh/layering-cache/blob/master/images/wechat.png)







