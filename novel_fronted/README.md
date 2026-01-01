# 小说平台前端（novel_fronted）

## 项目架构
- 技术栈：Vue 3 + Vite + Vue Router + Pinia + Tailwind CSS
- 主要目录：
  - `src/views`：读者端、作者端、管理端页面
  - `src/api`：axios 封装及业务接口
  - `src/store`：登录状态与用户信息
  - `src/router`：路由配置
  - `src/main.js`：应用入口

## 主要功能
- 读者端：小说列表、排行榜、小说详情、章节阅读、阅读历史
- 账户相关：注册、登录、个人中心
- 作者端：作品管理、章节管理、小说信息编辑
- 管理端：用户管理、作者管理、平台统计看板

## 启动方式
1. 安装依赖

   ```bash
   npm install
   ```

2. 本地开发

   ```bash
   npm run dev
   ```

   默认访问地址：控制台输出，一般为 `http://localhost:5173`。

3. 构建生产包

   ```bash
   npm run build
   ```
