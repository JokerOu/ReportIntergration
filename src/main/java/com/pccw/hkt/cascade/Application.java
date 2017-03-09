package com.pccw.hkt.cascade;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;


//@EnableAutoConfiguration
@SpringBootApplication
//@ComponentScan(basePackages={"com.pccw.hkt.cascade.controller", "com.pccw.hkt.cascade.service"})
@MapperScan("com.pccw.hkt.cascade.dao")
public class Application {

	private static Logger logger = Logger.getLogger(Application.class);
	
	//Config DataSource
	@Bean
	@ConfigurationProperties(prefix="spring.datasource")//load the configuration witch prefix with "spring.datasource" in application.properties
	public DataSource dataSource(){
		return new org.apache.tomcat.jdbc.pool.DataSource();
	}
	
	//provide SqlSession
	@Bean
	public SqlSessionFactory sqlSessionFactoryBean() throws Exception{
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:/mybatis/*.xml"));
		return sqlSessionFactoryBean.getObject();
	}
	
	@Bean
	public PlatformTransactionManager transactionManager(){
		return new DataSourceTransactionManager(dataSource());
	}
	
	//Application stater
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		logger.info("============= SpringBoot Start Success =============");
	}
	
}
