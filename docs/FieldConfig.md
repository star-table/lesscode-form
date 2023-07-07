### 开始
不同类型的字段有不同的风格，所以需要每个字段都应该专属的配置，以方便程序对该字段做一些特殊处理，同时更方便做前端的展示。
 
### 配置

 - title: 标题, 类型为STRING
 - hint: 提示, 类型为STRING
 - default_value: 默认内容, 类型为MAP
 - field_ratio: 字段占比, 类型为INT
 - required: 是否必填, 类型为BOOL
 - unique: 是否唯一, 类型为STRING
 - content: 内容, 类型为STRING
 - text_length_range: 长度范围, 类型为RANGE_INT
 - date_format: 日期格式, 类型为STRING
 - sms_verify: 短信验证, 类型为BOOL
 - number_show_format: 数字显示格式, 类型为STRING
 - decimal: 小数位, 类型为INT
 - number_range: 数字范围, 类型为RANGE_INT
 - options: 选项, 类型为ARRAY_STRING
 - options_model: 选项模式, 类型为ARRAY_STRING
 - options_layout: 选项排布, 类型为ARRAY_STRING
 - options_count_limit: 选项数量限制, 类型为INT
 - file_size_limit: 文件大小限制, 类型为INT
 - file_count_limit: 文件数量限制, 类型为INT
 - file_type_limit: 文件类型限制, 类型为ARRAY_STRING
 - date_overlap: 是否允许时间重叠, 类型为BOOL
 - address_format: 地址格式, 类型为STRING
 - allowed_modify: 允许手动修改, 类型为BOOL
 - sort: 排序, 类型为MAP
 - aggregation: 聚合, 类型为MAP


### 字段模板配置
字段模板配置字段为一个json数组，每个元素对应一个配置，字段模板只需要将配置名字记录下来即可：

```
["content", "hint"]
```

### 字段配置
字段配置中会将字段模板中所有的配置赋予对应的参数：

```
{
	"title": "姓名",
	"hint": "这里填姓名",
	"default_value": {
		"relation_table_id": 1,
		"relation_field_id": 1,
		"value": "默认的"
	},
	"field_ratio": 25,
	"required": true,
	"unique": true,
	"content": "要认真填哦",
	"text_length_range": [20,30],
	"date_format": "yyyy-MM-dd HH:mm:ss",
	"sms_verify": true,
	"number_show_format": "%",
	"decimal": 10,
	"number_range": [10, 20],
	"options": ["hello", "world"],
	"options_model": ["row"],
	"options_layout": ["h"],
	"options_count_limit": 10,
	"file_size_limit": 25,
	"file_count_limit": 100,
	"file_type_limit": ["jpg", "png"],
	"date_overlap": true,
	"address_format": "s/s/q",
	"allowed_modify": true
}
```