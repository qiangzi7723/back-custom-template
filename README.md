## 脚手架

关键的配置，`application.properties`已被隐藏，不推到公开代码仓库上。需要运营此仓库，需自行添加数据库等配置

---

### 目前已经集成以下功能

#### Docker打包 + Jenkins集成

需要修改Jenkinsfile里面的仓库地址，即`def name`变量，需要联系运维提前在阿里云上创建好仓库。然后方可修改`def name`变量，并且推代码到仓库上，并在`Jenkins`上创建`Pipeline`进行打包。

---

#### 抽奖功能

调用`raffleService.raffle(actvityId, uid)`方法即可。需要提前创建好抽奖的数据表，然后配置好抽奖活动，以及调用`addGiftToken`接口，提前增加Redis中的奖品Token。

核心代码位置：`RaffleServiceImpl.java`

---

#### 自动拉黑

拉黑原理：每次触发后端的CommonJsonException，都会统计当前用户的UID以及IP地址，记录起来。

所以后端需要谨慎使用CommonJsonException中的错误，并且与前端做好约定，前端也要需要参考后端的限制，不给用户突破限制。如果前端做好了这些限制，那么能触发CommonJsonException错误的，都是脚本或者直接调用接口的非法用户。

需要限制非法用户时，调用`limitIllegal(int limit)`方法即可，传入`limit`数值，该数值表示被拉黑的次数，超过了这个数值，就无法通过该接口请求。

核心代码位置：`GlobalExceptionHandler.java` `IllegalRequestServiceImpl.java`

---

#### 接口加锁

可以配置接口，在白名单中的接口，会共享一个锁，每个用户，只能同时请求一个白名单中的接口。

应用场景：捐步数，或者提交分数等，都可以直接加上这个锁，不需要在接口中，再额外加锁。好处是，防止某些用户，使用脚本，同时并发多个请求了，突破后端限制，从而重复提交了很多轮分数，造成其他影响。

核心代码位置：`UserLockInterceptor.java`

---

#### 集成数据库

已经集成了数据库，包括`mysql`和`redis`，并且开启了`mybatis`下划线转驼峰的映射，不需要再写`BaseResultMap`进行重复映射

---

#### 错误上报

集成了Sentry的错误上报，代码有异常是，会自动上报到该地址：[Sentry.io]，其中各个项目也可以单独定义自己的Sentry仓库地址

后台的账号密码联系运维

核心代码位置：`SentryConfig.java`

---

#### 权限控制

集成了JWT，进行权限管理。各个项目的JWT秘钥可在环境变量中配置。

核心代码位置：`JwtInterceptor.java`

---

#### 上下文变量

用户的JWT信息，以及接口参数都会自动保存在上下文变量`Context`中，不需要每次都从`HttpServletRequest`获取参数

核心代码位置：`Context.java` 

---

#### 次数控制

应用场景：如需要控制某个功能，每日的次数。比如每天只能兑换三次奖励，就可以应用此模块

核心代码位置：`CommonService.java` 

---

#### 排行榜与提交分数

包含开始游戏，提交分数，排行榜三个接口

核心代码位置：`RankService.java` 

---


#### 微信小程序授权与微信支付

引入了第三方开源模块，实现微信相关功能

核心代码位置：`wechat`文件夹 

---
