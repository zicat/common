## 一、 项目介绍
> Newegg Common Config提供配置文件解析，监听注册，父子继承实现。
> 主要的需求包括：1. 支持多种数据源，包括本地文件，zookeeper数据，ftp数据，http数据等。2. 支持多种格式，包括properties，json，xml，avro等。3. 支持监听变化动态更新，当一个配置文件发生变化时，能自动更新内存数据。4. 支持父子结构，在一些业务中，希望配置文件是依赖继承关系，这种关系能够使得当parent构造时，child也能进行构造。

## 二、 核心对象设计

1.1 com.newegg.ec.ncommon.config.Config 
<img src="/ncommon-config/docs/images/config.png" />
1.2 com.newegg.ec.ncommon.config.LinkedConfig
<img src="/ncommon-config/docs/images/linkedconfig.png" />
1.3 com.newegg.ec.ncommon.config.LocalConfig
<img src="/ncommon-config/docs/images/localconfig.png" />
1.4 com.newegg.ec.ncommon.config.ZookeeperConfig
<img src="/ncommon-config/docs/images/zookeeperconfig.png" />
2.1 com.newegg.ec.ncommon.config.schema.Schema
<img src="/ncommon-config/docs/images/schema.png" />
2.2 com.newegg.ec.ncommon.config.schema.InputStreamSchema
<img src="/ncommon-config/docs/images/inputstreamschema.png" />
2.3 com.newegg.ec.ncommon.config.schema.properties.PropertiesSchema
<img src="/ncommon-config/docs/images/propertiesschema.png" />
2.4 com.newegg.ec.ncommon.config.schema.json.GsonSchema
<img src="/ncommon-config/docs/images/gsonschema.png" />
2.5 com.newegg.ec.ncommon.config.schema.json.JAXBSchema
<img src="/ncommon-config/docs/images/jaxbschema.png" />

## 二、 核心对象使用方式
1.1 基于本地文件，构建Properties，JSon，xml对象
<img src="/ncommon-config/docs/images/configtest.png" />
1.2 基于Zookeeper，构建JSON对象
<img src="/ncommon-config/docs/images/zookeeperconfigtest.png" />