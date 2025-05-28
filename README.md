# Hospital-appointment-system
# 医院预约与在线问诊系统
## 项目简介
本项目是一个基于Spring Boot后端与React前端的医院预约与在线问诊系统，支持用户在线预约医生、医生管理预约、在线咨询聊天、管理员管理等功能。

## 主要功能
- 用户注册、登录、预约医生、查看和取消预约
- 医生登录、查看“我的预约”、在线回复患者消息
- 管理员管理医生、用户和预约信息
- 在线消息咨询与未读消息提醒
- 预约信息导出为CSV
## 技术栈
- 后端：Java Spring Boot
- 前端：React（通过HTML+JSX+Babel）、Tailwind CSS、jQuery
- 数据库：MySQL（或兼容的关系型数据库）
## 目录结构
```
├── src/main/java/com/hospital/         
# 后端Java代码
├── src/main/resources/static/          
# 前端静态页面（HTML/JSX）
├── src/main/resources/application.
properties # 配置文件
├── pom.xml                             
# Maven依赖管理
```
## 快速开始
1. 配置数据库连接（修改 application.properties ）
2. 使用IDE或命令行运行 HospitalAppointmentApplication.java 启动后端服务
3. 访问 src/main/resources/static/ 下的HTML页面（如 index.html 、 doctor-login.html 、 admin.html 等）
## 主要页面说明
- index.html ：用户登录/注册与预约入口
- doctor-login.html ：医生登录入口
- doctor-dashboard.html ：医生工作台，查看预约与消息
- admin.html ：管理员后台
- message.html ：在线咨询聊天界面
## 注意事项
- 请确保数据库已初始化并有相应表结构
- 前端页面建议通过本地服务器或IDE插件方式访问，避免跨域问题
## 联系方式
如有问题请联系项目维护者。
