package com.mawujun.repository.mybatis.jpa;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.mybatis.spring.boot.autoconfigure.MybatisProperties;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.mawujun.mvc.SpringContextUtils;
import com.mawujun.util.ReflectUtil;

@org.springframework.context.annotation.Configuration
@ConditionalOnClass({ SqlSessionFactory.class, SqlSessionFactoryBean.class })
@ConditionalOnBean(DataSource.class)
@EnableConfigurationProperties(MybatisProperties.class)
@AutoConfigureAfter(DataSourceAutoConfiguration.class)
@AutoConfigureBefore(MybatisAutoConfiguration.class)
@EnableTransactionManagement
//@AutoConfigureOrder(2)
public class JpaMybatisAutoConfiguration {
	  private static final Logger logger = LoggerFactory.getLogger(JpaMybatisAutoConfiguration.class);

	  private final MybatisProperties properties;

	  private final Interceptor[] interceptors;

	  private final ResourceLoader resourceLoader;

	  private final DatabaseIdProvider databaseIdProvider;

	  private final List<ConfigurationCustomizer> configurationCustomizers;

	  public JpaMybatisAutoConfiguration(MybatisProperties properties,
	                                  ObjectProvider<Interceptor[]> interceptorsProvider,
	                                  ResourceLoader resourceLoader,
	                                  ObjectProvider<DatabaseIdProvider> databaseIdProvider,
	                                  ObjectProvider<List<ConfigurationCustomizer>> configurationCustomizersProvider) {
	    this.properties = properties;
	    this.interceptors = interceptorsProvider.getIfAvailable();
	    this.resourceLoader = resourceLoader;
	    this.databaseIdProvider = databaseIdProvider.getIfAvailable();
	    this.configurationCustomizers = configurationCustomizersProvider.getIfAvailable();
	  }

	  @PostConstruct
	  public void checkConfigFileExists() {
	    if (this.properties.isCheckConfigLocation() && StringUtils.hasText(this.properties.getConfigLocation())) {
	      Resource resource = this.resourceLoader.getResource(this.properties.getConfigLocation());
	      Assert.state(resource.exists(), "Cannot find config location: " + resource
	          + " (please add config file or check your Mybatis configuration)");
	    }
	  }

	  @Bean
	  @ConditionalOnMissingBean
	  public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
	    SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
	    factory.setDataSource(dataSource);
	    factory.setVfs(SpringBootVFS.class);
	    if (StringUtils.hasText(this.properties.getConfigLocation())) {
	      factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
	    }
	    Configuration configuration = this.properties.getConfiguration();
	    if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
	      configuration = new Configuration();
	    }
	    if (configuration != null && !CollectionUtils.isEmpty(this.configurationCustomizers)) {
	      for (ConfigurationCustomizer customizer : this.configurationCustomizers) {
	        customizer.customize(configuration);
	      }
	    }
	    
	    springContextUtils();
	    JpaMapperRegistry mapperRegistry=new JpaMapperRegistry(configuration);
	    ReflectUtil.setFieldValue(configuration, "mapperRegistry", mapperRegistry);
	    if(StringUtils.hasText(properties.getTypeAliasesPackage())) {
	    	 properties.setTypeAliasesPackage("com.mawujun.repository.mybatis.typeAliases,"+properties.getTypeAliasesPackage());
	    } else {
	    	 properties.setTypeAliasesPackage("com.mawujun.repository.mybatis.typeAliases");
	    }
	    //isMapUnderscoreToCamelCase
	    String u2cc=springContextUtils().getEnvironment().getProperty("mybatis.configuration.map-underscore-to-camel-case");
	    if(u2cc==null || "".equals(u2cc)) {
	    	configuration.setMapUnderscoreToCamelCase(true);
	    }

	    
//	    Field field = configuration.getClass().getDeclaredField("mapperRegistry");
//		field.setAccessible(true);
//		field.set(configuration, mapperRegistry);
		
	    factory.setConfiguration(configuration);
	    if (this.properties.getConfigurationProperties() != null) {
	      factory.setConfigurationProperties(this.properties.getConfigurationProperties());
	    }
	    if (!ObjectUtils.isEmpty(this.interceptors)) {
	      factory.setPlugins(this.interceptors);
	    }
	    if (this.databaseIdProvider != null) {
	      factory.setDatabaseIdProvider(this.databaseIdProvider);
	    }
	    if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
	      factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
	    }
	    if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
	      factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
	    }
	    if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
	      factory.setMapperLocations(this.properties.resolveMapperLocations());
	    }
	    
	    return factory.getObject();
	  }
	  
//	  @Bean
//	  @ConditionalOnMissingBean
//	  public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
//	    ExecutorType executorType = this.properties.getExecutorType();
//	    if (executorType != null) {
//	      return new SqlSessionTemplate(sqlSessionFactory, executorType);
//	    } else {
//	      return new SqlSessionTemplate(sqlSessionFactory);
//	    }
//	  }
//
//	  /**
//	   * This will just scan the same base package as Spring Boot does. If you want
//	   * more power, you can explicitly use
//	   * {@link org.mybatis.spring.annotation.MapperScan} but this will get typed
//	   * mappers working correctly, out-of-the-box, similar to using Spring Data JPA
//	   * repositories.
//	   */
//	  public static class AutoConfiguredMapperScannerRegistrar
//	      implements BeanFactoryAware, ImportBeanDefinitionRegistrar, ResourceLoaderAware {
//
//	    private BeanFactory beanFactory;
//
//	    private ResourceLoader resourceLoader;
//
//	    @Override
//	    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//
//	      logger.debug("Searching for mappers annotated with @Mapper");
//
//	      ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
//
//	      try {
//	        if (this.resourceLoader != null) {
//	          scanner.setResourceLoader(this.resourceLoader);
//	        }
//
//	        List<String> packages = AutoConfigurationPackages.get(this.beanFactory);
//	        if (logger.isDebugEnabled()) {
//	          for (String pkg : packages) {
//	            logger.debug("Using auto-configuration base package '{}'", pkg);
//	          }
//	        }
//
//	        scanner.setAnnotationClass(Mapper.class);
//	        scanner.registerFilters();
//	        scanner.doScan(StringUtils.toStringArray(packages));
//	      } catch (IllegalStateException ex) {
//	        logger.debug("Could not determine auto-configuration package, automatic mapper scanning disabled.", ex);
//	      }
//	    }
//
//	    @Override
//	    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
//	      this.beanFactory = beanFactory;
//	    }
//
//	    @Override
//	    public void setResourceLoader(ResourceLoader resourceLoader) {
//	      this.resourceLoader = resourceLoader;
//	    }
//	  }
//
//	  /**
//	   * {@link org.mybatis.spring.annotation.MapperScan} ultimately ends up
//	   * creating instances of {@link MapperFactoryBean}. If
//	   * {@link org.mybatis.spring.annotation.MapperScan} is used then this
//	   * auto-configuration is not needed. If it is _not_ used, however, then this
//	   * will bring in a bean registrar and automatically register components based
//	   * on the same component-scanning path as Spring Boot itself.
//	   */
//	  @org.springframework.context.annotation.Configuration
//	  @Import({ AutoConfiguredMapperScannerRegistrar.class })
//	  @ConditionalOnMissingBean(MapperFactoryBean.class)
//	  public static class MapperScannerRegistrarNotFoundConfiguration {
//
//	    @PostConstruct
//	    public void afterPropertiesSet() {
//	      logger.debug("No {} found.", MapperFactoryBean.class.getName());
//	    }
//	  }
	
//	@Autowired
//	private SqlSessionFactory sqlSessionFactory;
//
//	//@Bean
//	 @PostConstruct
//	public void onApplicationEvent() {
//		
////		//ConfigurableApplicationContext context =event.getApplicationContext();
////		context=event.getApplicationContext();
////		
////		// TODO Auto-generated method stub
////		SqlSessionFactory  sqlSessionFactory=context.getBean(SqlSessionFactory.class);
////		if(sqlSessionFactory==null) {
////			return;
////		}
//		//首先获取Configuration，等初始化完后，然后反射
//		//替换MapperProxyFactory
//		//然后再替换MapperProxy
//		Configuration conf=sqlSessionFactory.getConfiguration();
//		MapperRegistry mapperRegistry=conf.getMapperRegistry();
//		//Map<Class<?>, MapperProxyFactory<?>> knownMappers=(Map<Class<?>, MapperProxyFactory<?>>)ReflectUtils.getFieldValue(mapperRegistry, "knownMappers");
//		Map<Class<?>, MapperProxyFactory<?>> knownMappers_ =(Map<Class<?>, MapperProxyFactory<?>>)ReflectUtil.getFieldValue (mapperRegistry, "knownMappers");
//				
//		
//		JpaMapperRegistry jpaMapperRegistry=new JpaMapperRegistry(conf);
//		jpaMapperRegistry.setKnownMappers_(knownMappers_);
//		ReflectUtil.setFieldValue(jpaMapperRegistry, "knownMappers", knownMappers_);
//		//BeanUtils.copyProperties(mapperRegistry, jpaMapperRegistry);
//		
//		ReflectUtil.setFieldValue(conf, "mapperRegistry", jpaMapperRegistry);
//		//Collection<Class<?>> list=conf.getMapperRegistry().getMappers();
//		//System.out.println(list);
//		//MetaObject.forObject(object, objectFactory, objectWrapperFactory, reflectorFactory)
//		
//		
//		for(Entry<Class<?>,MapperProxyFactory<?>> entry:knownMappers_.entrySet()) {
//			knownMappers_.put(entry.getKey(), new JpaMapperProxyFactory(entry.getValue().getMapperInterface()));
//		}
//
////		conf.getTypeAliasRegistry().registerAlias("beanmap", BeanMap.class);
//	System.out.println(conf.getTypeAliasRegistry().getTypeAliases().containsKey("beanmap"));
//	System.out.println("============================");
////		
////		SpringContextUtils.getEnvironment()
//	}
	 

	 @ConditionalOnMissingBean(JpaDao.class)
	 @Bean
	 public JpaDao jpaDao() {
		 return new JpaDao();
	 }
	 
	 @ConditionalOnMissingBean(SpringContextUtils.class)
	 @Bean
	 public SpringContextUtils springContextUtils() {
		 return new SpringContextUtils();
	 }
	

}
