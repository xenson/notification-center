# osdnotificationcenter

租车埋点消息处理服务:监控&预警&统计<br/>
[埋点流程](http://conf.ctripcorp.com/pages/viewpage.action?pageId=373318461) <br/>
[埋点查询](http://conf.ctripcorp.com/pages/viewpage.action?pageId=458624702) <br/>
<br/>
<br/>

2022.08.30 更新<br/>
1.增加CK聚合统计结果落表-ck2art
<br/> *通过规则配置实现聚合
<br/>
2.增加UBT Kafka消息解析
<br/> *通过规则配置实现解析自定义字段

2022.03.24 更新<br/>
1.增加埋点纯净器
<br/> *通过过滤规则实现纯净器
<br/>
2.增加通用过滤规则解析
<br/> *通过Qconfig配置过滤或去重规则,可实现正则匹配
<br/>
3.增加Hickwall埋点通过配置正则对请求过滤,去重
<br/>
4.Clog日志增加title tags
<br/>

2022.01.21 更新<br/>
1.修正addGuage埋点:支持使用自定义metric名称,支持多指标并发,支持单位时间(1分钟)埋点数据sum聚合后落Prometheus库
<br/> *可用于统计缓存或请求量等需要做原值统计的埋点
<br/><br/>
2.修正recordSize埋点:支持使用自定义metric名称(实时落库)
<br/> *可用于统计返回结果大小或列表size等需要做原值统计的埋点
<br/>

2022.01.05 更新<br/>
1.指定埋点(通过配置条件获取)的QMQ消息推送-可多发至不同subject
<br/> *可用于TripPal公众号等主动消息通知
<br/>

2022.12.22 更新<br/>
1.解决ClickHouse时间戳timestamp为UTC时间的问题-查询时北京时间转UTC,结果UTC转北京
<br/>

2022.11.18 更新<br/>
1.增加ClickHouses查询接口
<br/>
2.增加Hickwall查询接口
<br/>
3.增加Dashboard查询接口
<br/> *可用于CK日志聚合,TS问题场景日志查询等
<br/>

2022.10.22 更新<br/>
1.增加租车应用健康日报邮件功能:应用错误数,应用接口响应耗时,应用请求量
<br/> *用于租车应用健康监控及日报推送
<br/><br/>