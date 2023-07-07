package com.polaris.lesscode.form.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

//@Configuration
@Deprecated
public class JsonConfig {

	@Bean
    public HttpMessageConverters fastJsonHttpMessageConverters(){
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
      
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteMapNullValue);
        
        //date format
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        
        //脱敏
        fastJsonConfig.setSerializeFilters(new LongFilter());
        
        //utf8
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        
        //添加配置
        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        HttpMessageConverter<?> converter = fastJsonHttpMessageConverter;
        return new HttpMessageConverters(converter);
    }
	

	public static class LongFilter implements ValueFilter{

	@Override
	public Object process(Object object, String name, Object value) {
		if(value != null && (Long.class.equals(value.getClass()) || long.class.equals(value.getClass()))) {
			value = value.toString();
		}
		return value;
	}
	
}

}


