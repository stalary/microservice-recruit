# microservice-recruit
基于微服务架构实现的智能招聘系统(用于毕业设计)

# 项目结构
![项目结构图](https://upload-images.jianshu.io/upload_images/9252736-5b380d3f10c806e8.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

# 使用说明
使用时麻烦将Nacos,ZipKin,Sentinel等组件自行搭建使用，本人服务器性能较差

存储在Nacos中的配置文件在https://github.com/stalary/pf-config

需要自行启动两个本人开源的服务

- lightmq(使用lightmq.stalary.com会导致消息被我的服务消费) https://github.com/stalary/lightMQ
- usercenter(使用usercenter.stalary.com会导致用户数据存储到我的数据库，数据泄漏) https://github.com/stalary/UserCenter

本地启动注意切换Nacos的Namespace，否则会出现本地服务与线上服务服务发现不通的情况，具体参照本人的一篇博客https://www.jianshu.com/p/5c84a1d3b2f9

# 部署方式
- docker部署：mvn docker:build & java -jar
- 物理机部署：mvn install & java -jar

# 技术栈
- Spring-Boot
- Spring-Cloud
- Spring-Cloud-Gateway
- Spring-Cloud-Sleuth(对业务侵入性较大，建议替换到 Skywalking )
- Sentinel
- Nacos
- Feign
- Docker
- Mysql
- Mongodb
- Redis
- LightMQ
- EasyDoc
- WebSocket
- Jpa
- UserCenter
- ElasticSearch

# 功能
- 上传简历||填写简历
- 投递简历
- 发布职位
- 简历打分(基于规则匹配打分)
- 消息推送
- 邮件提醒
- 推荐候选人
- 推荐职位

# 一些问题与解答
- lightmq报错：com.stalary.lightmq.url 需要使用http://开头
