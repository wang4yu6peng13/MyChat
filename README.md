# 当前版本

* 客户端可以用昵称、密码登陆进入聊天室大厅
* 输入的昵称不存在时，则为注册功能
* 在聊天室大厅，可以看到所有的聊天室列表
* 所有命令在任何状态下可以输入

|命令|说明|命令|说明|
|-|-|-|-|
|$login 用户名 密码|登录或注册|$rooms|聊天室列表|
|$logout|退出登录|$users( 房间名)|（聊天室里）在线用户列表|
|$quit|退出客户端|$xxx|在聊天室中直接发言|
|$create 房间名 房间简介|创建房间|$@昵称 xxx|对另一人密语|
|$enter 房间名|进入聊天室|$hongbao 金额,个数(,拼手气)|发（拼手气）红包|
|$exit|退出房间，回到大厅|$qiang 红包编号|抢红包|

* 聊天室信息需要存盘，服务器重启后还可以看到之前创建的所有房间，进入到房间后，可以看到聊天记录
* 账户信息需要存盘
* 使用并发（多线程）响应每个聊天室的请求
* ~~客户端可以使用Swing库绘制UI实现功能，~~ 也可以通过命令行显示信息的形式实现功能
* 发红包
    * 使用命令“$hongbao 总金额，个数”发红包
    * 使用命令“$hongbao 总金额，个数，拼手气”发拼手气的红包
    * 服务器将红包加编号后，发送到房间内
    * 使用 命令“$qiang 红包编号”抢红包，同一个编号的红包，已经抢过的玩家不允许再抢红包
    * 将抢红包的信息显示到客户端，拼手气的红包显示出手气最佳的人员昵称