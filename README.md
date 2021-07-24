# generate-code
@[TOC](generate-code开源的代码生成器)
#  一、背景
从开源项目：https://gitee.com/QinHaiSenLin/Jfinal-layui?_from=gitee_search
中抽取出了其中的单表代码生成，并在其基础之上做了扩展，如：通用模块生成：编辑模板，代码生成，更换数据源（暂时支持mysql、oracle切换），下载会被da成zip包等；
原项目中JFinal-Layui模板代码生成：扩展了更换数据源功能，下载会被da成zip包等等扩展功能；
编辑模板代码生成：可以通过在线编辑模板文件，新增修改删除。然后重新生成按照新模板文件的内容生成代码，更换数据源，下载会被da成zip包等等。
#  二、项目链接
FlierTeam团队贡献
GitHub：[generate-code](https://github.com/FlierTeam/generate_code)
#  三、功能介绍
## （一）通用代码生成器
### 代码生成
点击通用代码生成器，选择需要生成的数据库表，不选择更换数据源的话会按照默认的数据库源进行生成，其它配置按照需要的进行改动即可
![在这里插入图片描述](https://img-blog.csdnimg.cn/117434634ad6475b8ef7458528fd3313.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
选择数据表进行生成
![选择数据表进行生成](https://img-blog.csdnimg.cn/433cc1626c5c4857b19a019fb3df5f4a.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
*效果：*
![在这里插入图片描述](https://img-blog.csdnimg.cn/ccab8c28da254dcd96d892189740fe51.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
### 切换数据源
填好想要更换的源配置，点击更换数据源即可。
![在这里插入图片描述](https://img-blog.csdnimg.cn/753518286714493fa4ff4396e29c3537.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
### 下载
会打成zip包直接下载，也可以根据指定的本地生成路径生成，没有指定就是本地默认路径下载。
![在这里插入图片描述](https://img-blog.csdnimg.cn/08177de2a9e545ddae373e731f2bef94.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)

## （二）可修改模板的生成器
### 模板管理
点击可修改模板的生成器，点击模板管理，进行编辑模板。
![在这里插入图片描述](https://img-blog.csdnimg.cn/42d7b5bce01e4c81bbef880e8c6766a7.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
此模板文件可以根据需求任意修改（新增，修改，删除），为了保证内容正确性，此处做了校验，模板文件需要按照一定格式
![其它配置按照需要的进行改动即可，](https://img-blog.csdnimg.cn/65fc6e27a73c49b68814822918e10e6c.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
模板文件中可供渲染的变量，其实现只是通过字符串替换实现的：

```java
渲染的模板参数：
任意模板文件都可以使用
${controllerPackage}       :com.xxx.controller
${servicePackage}          :com.xxx.service
${modelName}               :设定好的模块名，按照设定的大小写
${lowercaseModelName}      :设定好的模块名，开头小写
${importModel}             :com.xxx.model
${date}
用在前端请求时，控制层路径
${actionKey}               :/model/view

前端遍历数据库字段
遍历列集合
#for(x:columnMetas)
	if判断
	#if(x.name!=primaryKey)
		字段渲染
		"info": "${info.${x.attrName}}",
	#end
#end

-----------+---------+------+-----+---------+----------------
 Field     | Type    | Null | Key | Default | Remarks
-----------+---------+------+-----+---------+----------------
 id		   | int(11) | NO	| PRI | NULL	| remarks here

name;				// 字段名
javaType;			// 字段对应的 java 类型
attrName;			// 字段对应的属性名
type;				// 字段类型(附带字段长度与小数点)，例如：decimal(11,2)
isNullable;		    // 是否允许空值
isPrimaryKey;		// 是否主键
defaultValue;		// 默认值
remarks;			// 字段备注
```

编辑模板按照以下格式：
![在这里插入图片描述](https://img-blog.csdnimg.cn/2bfac26cde17432dae294997ddacb9ad.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2x4MTMxNTk5ODUxMw==,size_16,color_FFFFFF,t_70)
#  四、总结
项目整体实现并不复杂，逻辑简单，欢迎留言讨论、指正~
