package com.pccw.hkt.cascade;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
public class LdapConfiguration {
	
	@Bean
	@ConfigurationProperties(prefix="ldap.contextSource")
	public LdapContextSource contextSource(){
		return new LdapContextSource();
	}

	@Bean
	public LdapTemplate ldapTemplate(ContextSource contextSource ){
		return new LdapTemplate(contextSource);
	}
	
}
