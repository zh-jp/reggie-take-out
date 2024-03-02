# reggie-take-out

> 黑马程序员Java项目实战《瑞吉外卖》
>

## 目录

- `img`: 项目图片文件
- `src`: 项目源代码与资源
- `static`: 前端页面，请部署在nginx中，解压`/static.zip` 为 `static/backend` 和 `static/front`
- `target`: 生成的目标文件

> 更多配置细节如Rediss、MySQL请根据
> `src/main/resources/application.yml`文件
---

## 相关技术

`SpringBoot` `Mybatis-Plus` `MySQL` `Redis` `Docker` `Nginx`

## 实现功能

- 管理员：
  `员工登录` `员工管理` `分类管理` `套餐管理`
- 用户：
  `验证码登录` `购物车添加商品` `用户下单` `设置收货地址`

## 亮点

- 使用`Mybatis-Plus`技术简化数据库操作，提高开发效率。
- 使用`Redis`技术实现缓存功能，提高数据查询效率。
- 使用主从数据库实现读写分离，缓解数据访问压力。
- 使用`Docker`部署运行环境，方便管理。
- 前后端分离部署，便于维护
## 对比原项目的改进

0. 删改了少量代码
1. 用户界面中，用`邮箱验证登录`代替`手机验证登录`

