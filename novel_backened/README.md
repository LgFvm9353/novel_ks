# 小说平台后端（novel_backened）

## 项目架构
- 技术栈：Spring Boot 2.7、MyBatis、MySQL、Redis、JWT
- 主要包结构：
  - `user` / `auth`：用户注册登录、鉴权
  - `novel` / `author` / `chapter`：小说、作者、章节相关接口
  - `comment` / `history` / `vote`：评论、阅读历史、投票等交互
  - `admin`：运营管理接口
  - `config`：拦截器、JWT 配置等
- 数据存储：MySQL（库名示例：`novel_platform`），Redis 用于缓存/会话

## 主要功能
- 用户注册、登录、JWT 鉴权
- 小说列表、搜索、详情、章节阅读
- 评论、投票/点赞、阅读历史记录
- 作者作品管理与章节发布
- 管理后台的用户、作者及平台统计管理

## 启动方式
1. 环境准备
   - JDK 8+
   - MySQL，并创建 `novel_platform` 数据库
   - Redis 本地运行（默认 `localhost:6379`）

2. 配置数据库与 JWT（可选）
   - 修改配置文件：[application.properties](file:///d:/code/test/novel/novel_backened/src/main/resources/application.properties#L1-L16)
   - 或通过环境变量覆盖：
     - `DB_URL` / `DB_USERNAME` / `DB_PASSWORD`
     - `JWT_SECRET` / `JWT_EXPIRE_SECONDS`

3. 启动服务（项目根目录）
   - 使用 Maven Wrapper：

     ```bash
     ./mvnw spring-boot:run
     # Windows 也可以直接运行
     mvnw.cmd spring-boot:run
     ```

   - 或使用本地 Maven：

     ```bash
     mvn spring-boot:run
     ```

4. 默认访问地址
   - 接口服务：`http://localhost:8080`

