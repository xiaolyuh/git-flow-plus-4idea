# 捐赠
请作者喝杯咖啡吧！

![捐赠](https://plugins.jetbrains.com/files/14056/499-page/763c3b4b-839f-4ca8-9f5e-079d88d0ac89)

# 简介
[GitFlowPlus](https://plugins.jetbrains.com/plugin/14056-gitflowplus) 插件是一款基于[mrtf-git-flow](https://xiaolyuh.blog.csdn.net/article/details/105180250)分支管理流程的Idea插件，它最主要的作用是用来简化分支管理流程，最大限度的防止误操作。

![请添加图片描述](https://plugins.jetbrains.com/files/14056/499-page/a889c759-c6cd-45c1-84a9-c1fd5b904d8e)


主要功能如下：
- 基于主干分支快速新建开发分支和修复分支；
- 基于主干分支快速重建测试分支和发布分支；
- 开发完成后将快速将开发分支合并到测试分支；
- 开发完成后快速在IDEA发起Merge Request到远程目标分支；
- 发布完成后快速将发布分支合并到主干分支并打TAG；
- 代码提交时Commit信息规范化检查

# 主要解决的问题
1. 简化日常工作中分支操作步骤，比如新建分支、提测、发布、Merge Request等操作；
2. 降低分支操作过程中发生错误的概率；
3. 代码提交Commit 信息规范校验；

# Switch To English
![Switch To English](https://plugins.jetbrains.com/files/14056/499-page/8c471ebe-bfca-486d-92cd-2418eb28833a)

# 安装
## 在线安装
直接在IDEA插件市场搜索```GitFlowPlus```，如图：
![online_install.gif](https://plugins.jetbrains.com/files/14056/499-page/fe726aac-b30c-46e5-9403-00b7fe03682b)

## 离线安装
下载地址: [https://github.com/xiaolyuh/git-flow-plus-4idea/releases](https://github.com/xiaolyuh/git-flow-plus-4idea/releases)

![local_install.gif](https://plugins.jetbrains.com/files/14056/499-page/21713548-b0bb-4e3a-8660-c0c7dfb062c7)


# 插件入口
![插件入口.png](https://plugins.jetbrains.com/files/14056/499-page/ce775765-c3c8-4797-b994-624d91f4de04)


插件入口有2个：
1. 在Toolbar栏，这个需要显示Toolbar（View->Appearance->Toolbar）
2. 在Statusbar中

# 插件配置
每个仓库都需要进行插件初始化，配置完成后会生成一个```git-flow-plus.config```配置文件，**该文件需要添加到git版本管理中进行组内同步**，同步完成后组内成员可以共享配置。

![init.gif](https://plugins.jetbrains.com/files/14056/499-page/3992c3c0-7a84-4fd3-a9dc-f1229548a8c7)


如果配置了钉钉机器人Token，那么在MR的时候，钉钉机器人会在钉钉群发布MR的消息，格式如下：
```
@user1 @user2 【user3】发起了Merge Request请求，麻烦您CR&Merge下代码: 
http://git.xxx.net/object1/-/merge_requests/401
```

# 消息配置
1. 配置机器人&消息接收人OA
2. 在 Merge Request 时可以修改消息通知人，也可以选择是否送消息
3. 消息接受者在云图梭收到消息后可以直接点击链接地址即可以进行Code Review操作


# 新建分支
新建开发分支和修复分支都会直接从```origin/master```新建分支，新建分支后会自动切换到新建后的分支。
![new_branch.gif](https://plugins.jetbrains.com/files/14056/499-page/5272656a-3237-4b3c-b1b3-a799ce4b04d4)

执行命令：
```git
git fetch origin master:newBranchName
git checkout newBranchName --force
git push origin newBranchName:newBranchName --tag  --set-upstream
```

> 如果本地有修改文件未提交是不允许新建和重建分支的

# 重建测试分支
重建测试分支会直接从```origin/master```新建分支一个测试分支，原来的测试分支会被直接删除。

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
重建发布分支会直接从```origin/master```新建分支一个发布分支，原来的发布分支会被直接删除。
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
![merge-request](https://plugins.jetbrains.com/files/14056/499-page/7f47b6b9-1745-4fac-bf7d-650c744d271d)

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

如果配置了钉钉的机器人Token，那么还会往钉钉群发送一条MR消息，如：
```
@user1 @user2【user3】发起了Merge Request请求，麻烦您CR&Merge下代码: 
http://git.xxx.net/object/-/merge_requests/401
```


# 提测
提测会将当前分支合并到```origin/test```，在合并过程中如果出现冲突并且选择未解决，那么当前分支会切换到本地```test分支```，等待解决冲突；如果没有任何异常情况，那么合并完成后当前分支不会发生切换。
> 当前分支必须是开发分支或者修复分支时，才允许提测。
![test.gif](https://plugins.jetbrains.com/files/14056/499-page/4bbf0723-82d5-40a2-9600-d0e6fdd5e3ad)

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
开始发布会将当前分支合并到```origin/release```。

![release.gif](https://plugins.jetbrains.com/files/14056/499-page/37dc4a64-de73-4d2e-adbd-955a54f9659f)

执行命令：

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
发布完成会将发布分支合并到```origin/master```，必须打Tag；

![finish_release.gif](https://plugins.jetbrains.com/files/14056/499-page/e4aeccc0-9a48-45aa-8b85-375c957fe605)

执行命令：
主干分支在本地不存在，会在本地新建主干分支：
```
git checkout -b master origin/master
```

主干分支存在：
```git
git checkout release --force
git pull origin release:release
git checkout master --force
git pull origin master:master
git merge release   ['release_1' merge into 'refs/heads/feature/111'] (如果没解决冲突后面语句不执行)
git tag -a -f -m message tagName
git push origin master:master --tag  --set-upstream 
git checkout featureBranchName --force 
```

# 错误提示
如果你需要查看git命令或者查看插件的一些错误信息可以点击红框处

![查看错误提示文案](https://plugins.jetbrains.com/files/14056/499-page/b262197a-2598-41a4-8817-0af77507c230)
