/**
 * 
 */
package com.polaris.lesscode.form.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.feign.ResultStatusDecoder;
import com.polaris.lesscode.jackson.DateJsonDeserializer;
import com.polaris.lesscode.jackson.DateJsonSerializer;
import com.polaris.lesscode.jackson.InstantJsonDeserializer;
import com.polaris.lesscode.jackson.InstantJsonSerializer;
import com.polaris.lesscode.web.InstantConvert;
import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.optionals.OptionalDecoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Bomb
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Autowired
    private ObjectFactory<HttpMessageConverters> messageConverters;
    
    @Bean
    public Decoder feignDecoder() {
    	Decoder decoder = new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(this.messageConverters)));
        return new ResultStatusDecoder(decoder);
    }

	@Bean
	public Encoder feignEncoder() {
		ObjectFactory<HttpMessageConverters> objectFactory = () -> new HttpMessageConverters(getMappingJackson2HttpMessageConverter());
		return new SpringEncoder(objectFactory);


		/*ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleDateFormat smt = new SimpleDateFormat(CommonConsts.DEFAULT_DATE_PATTERN);
		objectMapper.setDateFormat(smt);
		objectMapper.setTimeZone(TimeZone.getDefault());
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Instant.class, new InstantJsonDeserializer());
		module.addSerializer(Instant.class, new InstantJsonSerializer());
		module.addDeserializer(Date.class, new DateJsonDeserializer());
		module.addSerializer(Date.class, new DateJsonSerializer());

		objectMapper.registerModule(module);
		return new JacksonEncoder(objectMapper);*/
	}
	/*
	 * @Override public void extendMessageConverters(List<HttpMessageConverter<?>>
	 * converters) { }
	 */

	public MappingJackson2HttpMessageConverter getMappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = jackson2HttpMessageConverter.getObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		SimpleDateFormat smt = new SimpleDateFormat(CommonConsts.DEFAULT_DATE_PATTERN);
		objectMapper.setDateFormat(smt);
		objectMapper.setTimeZone(TimeZone.getDefault());
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Instant.class, new InstantJsonDeserializer());
		module.addSerializer(Instant.class, new InstantJsonSerializer());
		module.addDeserializer(Date.class, new DateJsonDeserializer());
		module.addSerializer(Date.class, new DateJsonSerializer());

		objectMapper.registerModule(module);
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON);
		jackson2HttpMessageConverter.setSupportedMediaTypes(list);
		jackson2HttpMessageConverter.setObjectMapper(objectMapper);
		return jackson2HttpMessageConverter;
	}
	
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new InstantConvert());
	}
}
