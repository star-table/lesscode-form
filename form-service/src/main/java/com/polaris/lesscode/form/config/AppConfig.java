/**
 * 
 */
package com.polaris.lesscode.form.config;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.polaris.lesscode.consts.CommonConsts;
import com.polaris.lesscode.jackson.DateJsonDeserializer;
import com.polaris.lesscode.jackson.DateJsonSerializer;
import com.polaris.lesscode.jackson.InstantJsonDeserializer;
import com.polaris.lesscode.jackson.InstantJsonSerializer;
import com.polaris.lesscode.util.ApplicationContextUtil;

/**
 * @author Bomb
 *
 */
@ComponentScan(basePackages = {"com.polaris.lesscode.app.internal.fallback",
		"com.polaris.lesscode.permission.internal.fallback",
		"com.polaris.lesscode.dc.internal.fallback",
		"com.polaris.lesscode.workflow.internal.fallback",
		"com.polaris.lesscode.msgsvc.internal.fallback",
		"com.polaris.lesscode.gotable.internal.fallback",
		"com.polaris.lesscode.uc.internal.fallback"})
@Configuration
public class AppConfig{
	
	@Autowired
	private ApplicationContext context;
	
	@PostConstruct
	private void init() {
		ApplicationContextUtil.setApplicationContext(context);
	}
	
	@Bean(CommonConsts.FALLBACK_MOCKED_MAP_BEAN_NAME)
	@ConditionalOnProperty(name="open",prefix = "fallback.mockflg",havingValue = "true",matchIfMissing = true)
	@ConfigurationProperties(prefix = "fallback.mockflg.apps")
	public Map<String, Boolean> getFallbackMockflgMap(){		
		return new HashMap<String, Boolean>();
	}

	/**
	 * added java bean 校验器，默认快速失败. 
	 * @return Validator
	 */
	@Bean
	public Validator getValidator() {
		ValidatorFactory validatorFactory = Validation.byProvider( HibernateValidator.class )
	            .configure()
	            .addProperty( "hibernate.validator.fail_fast", "true" )
	            .buildValidatorFactory();
	    Validator validator = validatorFactory.getValidator();
	    return validator;
	}
	
	@Bean
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
		module.addSerializer(Long.class, ToStringSerializer.instance);
		
		objectMapper.registerModule(module);
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.APPLICATION_JSON);
		jackson2HttpMessageConverter.setSupportedMediaTypes(list);
		jackson2HttpMessageConverter.setObjectMapper(objectMapper);
		return jackson2HttpMessageConverter;
	}
	
	
	
	
	
}
