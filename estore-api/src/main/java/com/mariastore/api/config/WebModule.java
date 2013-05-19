package com.mariastore.api.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MarshallingHttpMessageConverter;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = { WebModule.WEB_PACKAGE })
//@EnableAspectJAutoProxy(proxyTargetClass=true)
public class WebModule extends WebMvcConfigurerAdapter
{
	public static final String BASE_PACKEAGE = "com.mariastore";
	public static final String WEB_PACKAGE = 	BASE_PACKEAGE + ".api.web";
	public static final String CORE_PACKAGE = 	BASE_PACKEAGE + ".core.domain";
	public static final String API_PACKAGE = 	BASE_PACKEAGE + ".api.domain";
	
	public WebModule() {
		super();
	}

	@Bean
	public RequestMappingHandlerMapping handlerMapping(){
		return new RequestMappingHandlerMapping();
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry aRegistry)
	{
		//aRegistry.addResourceHandler("/s/*").addResourceLocations("classpath:/META-INF/webapp/WEB-INF/view/scripts/*");
		//aRegistry.addResourceHandler("/WEB-INF/view/*").addResourceLocations("classpath:/META-INF/webapp/WEB-INF/view/*");
		aRegistry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/site/images/favicon.ico");
	}
	
	protected HttpMessageConverter<Object> createXMLConverter() {
		final MarshallingHttpMessageConverter xmlConverter = new MarshallingHttpMessageConverter();
    	final Jaxb2Marshaller jaxbMarshaller = new Jaxb2Marshaller();
    	jaxbMarshaller.setPackagesToScan(new String[] { WebModule.CORE_PACKAGE, WebModule.API_PACKAGE });
    	jaxbMarshaller.setCheckForXmlRootElement(false);
    	try {
    		jaxbMarshaller.afterPropertiesSet();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	xmlConverter.setMarshaller(jaxbMarshaller);
    	xmlConverter.setUnmarshaller(jaxbMarshaller);
    	
    	List<MediaType> mediatypes = new ArrayList<MediaType>();
		mediatypes.add(MediaType.APPLICATION_XML);
		mediatypes.add(MediaType.TEXT_XML);
		xmlConverter.setSupportedMediaTypes(mediatypes);
		
		return xmlConverter;
	}
	
	protected HttpMessageConverter<Object> createJSONConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.setAnnotationIntrospector(new JaxbAnnotationIntrospector(mapper.getTypeFactory()));
    	converter.setObjectMapper(mapper);
    	
    	List<MediaType> mediatypes = new ArrayList<MediaType>();
		mediatypes.add(MediaType.APPLICATION_JSON);
		converter.setSupportedMediaTypes(mediatypes);
		
		return converter;
	}

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
    	
    	messageConverters.add(createXMLConverter());
        messageConverters.add(createJSONConverter());

        super.configureMessageConverters(messageConverters);
    }
}
