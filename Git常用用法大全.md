下面是 **Git 的常用命令大全**，按照用途分类，并附带简要说明，适合日常开发和项目管理使用。

---

##  一、基础配置

```bash
git config --global user.name "你的名字"          # 设置用户名
git config --global user.email "你的邮箱"         # 设置邮箱
git config --global core.editor code              # 设置默认编辑器（如 VS Code）
git config --global init.defaultBranch main       # 默认分支名
git config --list                                 # 查看所有配置
```


---

##  二、项目初始化与克隆

```bash
git init                                          # 初始化本地仓库
git clone <url>                                   # 克隆远程仓库
git clone <url> <folder>                          # 克隆到指定目录

```

##  三、基本操作

```bash
git status                                        # 查看状态
git add <file>                                    # 添加单个文件到暂存区
git add .                                         # 添加所有改动文件
git commit -m "提交信息"                         # 提交暂存区到本地仓库
git commit -am "提交信息"                        # 添加已追踪文件并提交
git log                                           # 查看提交历史
git log --oneline                                 # 简洁日志
git diff                                          # 查看未暂存的更改
git diff --cached                                 # 查看已暂存但未提交的更改

```


---

##  四、分支管理

```bash
git branch                                        # 查看本地分支
git branch <name>                                 # 创建新分支
git checkout <name>                               # 切换分支
git checkout -b <name>                            # 创建并切换新分支
git merge <branch>                                # 合并分支
git branch -d <name>                              # 删除本地分支
git branch -D <name>                              # 强制删除分支

```

---

##  五、远程操作

```bash
git remote -v                                     # 查看远程仓库
git remote add origin <url>                       # 添加远程仓库
git push -u origin main                           # 推送并设置上游分支
git push                                          # 推送本地提交到远程
git pull                                          # 拉取远程更新并合并
git fetch                                         # 获取远程更新但不合并
git clone <url>                                   # 克隆远程仓库

```
---

##  六、撤销与回退

```bash
git restore <file>                                # 撤销工作区修改（Git 2.23+）
git checkout -- <file>                            # 旧版本撤销修改
git reset HEAD <file>                             # 撤销已暂存（回到未暂存）
git reset --soft HEAD~1                           # 回退上一次提交（保留改动）
git reset --hard HEAD~1                           # 强制回退并删除改动
git revert <commit_id>                            # 撤销某次提交，生成新的提交

```

---

##  七、暂存与清理

```bash
git stash                                         # 暂存当前更改
git stash pop                                     # 恢复最新 stash
git stash list                                    # 查看所有 stash
git clean -fd                                     # 删除未跟踪的文件/目录
```

---

##  八、标签管理

```bash
git tag                                           # 查看标签
git tag <name>                                    # 创建标签
git tag -a <name> -m "说明"                       # 创建带说明的标签
git push origin <tag>                             # 推送标签
git push origin --tags                            # 推送所有标签

```

---

##  九、查看信息

```bash
git show <commit/tag/branch>                      # 显示详情
git blame <file>                                  # 查看每行代码提交人
git shortlog                                      # 统计提交作者
```

##  十、其他实用命令

```bash
git reflog                                        # 查看 HEAD 变动记录（救命工具）
git cherry-pick <commit_id>                       # 应用某次提交到当前分支
git bisect start                                  # 二分查错开始

```

下面是 **Git 常见场景的命令组合与操作流程**，非常适合实战中查阅：

---

## 👥 场景一：多人协作（同步远程分支）

### ✅ 目标：

- 拉取他人提交
    
- 合并更新
    
- 推送自己提交
    

### 🔁 操作流程：

```bash
git pull origin main                   # 拉取远程 main 分支（合并到本地）
git add .                              # 添加改动
git commit -m "更新功能X"
git push origin main                   # 推送本地改动到远程
```

### ⚠️ 避免冲突小技巧：

- 每次开发前先 `git pull`
    
- 提交前确认状态 `git status`

## 🔀 场景二：功能开发（使用分支）

### ✅ 目标：

- 每个功能一个分支
    
- 开发完合并回主分支
    

### 🔧 操作流程：

```bash
git checkout -b feature-login          # 创建并切换到新分支
...                                    # 编码、提交

git checkout main                      # 切换回主分支
git pull                               # 确保是最新主分支
git merge feature-login                # 合并功能分支
git push                               # 推送更新
git branch -d feature-login            # 删除本地功能分支

```

---

## ⚔️ 场景三：解决合并冲突

### ✅ 现象：

执行 `git pull` 或 `git merge` 时出现冲突

### 🛠 操作步骤：

1.打开冲突文件，定位标记：
```text
<<<<<<< HEAD
本地代码
=======
远程代码
>>>>>>> origin/main
```
ㅤ
2.**手动修改**为想要保留的版本。
3.标记为已解决并提交：
```bash
git add .
git commit -m "解决冲突"
```

---

## ♻️ 场景四：撤销操作（改错）

### 🧍‍♂️ 不小心改错了还没提交：

```bash
git restore <file>                    # 撤销工作区更改（新版本 Git）
```

### 📦 暂存区有误：

```bash
git reset HEAD <file>                 # 回退到未暂存
```

### 🧨 提交错了想撤销（保留修改）：

```bash
git reset --soft HEAD~1               # 撤销上一次提交，但保留文件更改
```

### 💣 提交错了想彻底回退：
```bash
git reset --hard HEAD~1               # 回退提交并清除改动（危险操作）
```

---

## 🧰 场景五：代码找回（救命用）

### 🧭 找回误删分支或提交

```bash
git reflog                            # 查看历史操作记录
git checkout <commit_id>              # 切换到某个提交状态
```
---

## 🎁 场景六：打标签发布版本

### 📌 标记一个稳定版本

```bash
git tag v1.0                          # 打标签
git push origin v1.0                  # 推送标签
```

### 📜 附加说明的标签

```bash
git tag -a v1.0 -m "初始版本发布"
```

---

## 🧪 场景七：测试代码在其他分支运行

### 🍒 使用 cherry-pick 挑一个提交：
```bash
git checkout test-branch
git cherry-pick <commit_id>
```

---

## 🛡 场景八：远程分支管理

```bash
git branch -r                         # 查看远程分支
git checkout -b dev origin/dev        # 跟踪远程分支
git push origin --delete dev          # 删除远程分支
```

---

## 🧼 场景九：清理垃圾文件和 stash

```bash
git stash                             # 暂存当前改动
git stash pop                         # 取出最近一次 stash
git clean -fd                         # 清除所有未跟踪的文件和目录
```