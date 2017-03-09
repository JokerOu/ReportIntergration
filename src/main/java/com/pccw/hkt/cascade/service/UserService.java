package com.pccw.hkt.cascade.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Service;

import com.pccw.hkt.cascade.Application;
import com.pccw.hkt.cascade.Utils.ConstantConfig;
import com.pccw.hkt.cascade.Utils.ResponseCode;
import com.pccw.hkt.cascade.Utils.ResponseMessage;
import com.pccw.hkt.cascade.model.Team;
import com.pccw.hkt.cascade.model.User;
import com.pccw.hkt.cascade.model.response.LoginResponse;

@Service("UserService")
public class UserService {
	
	private static Logger logger = Logger.getLogger(Application.class);
	
	@Autowired
	private LdapTemplate ldapTemplate;
	@Autowired
	private LdapContextSource ldapContextSource;
	
	public LoginResponse login(String staffNo, String password, HttpSession session){
		ldapContextSource.setUserDn(staffNo + ConstantConfig.LDAP_LOGIN_USER_DN_SUFFIX);
		ldapContextSource.setPassword(password);
		ldapTemplate.setContextSource(ldapContextSource);
		
		LoginResponse res = null;
		try{
			User user = queryUser(staffNo);
			if(user != null){
				session.setAttribute(ConstantConfig.SESSION_USER_KEY, user);
				res = new LoginResponse(ResponseCode.ACC200, ResponseMessage.ACC200, true);
			}else{
				res = new LoginResponse(ResponseCode.ERR101, ResponseMessage.ERR101, false);
				logger.info("Login fail: [Staff NO.: " + staffNo + "]");
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			if(e instanceof AuthenticationException){
				res = new LoginResponse(ResponseCode.ERR101, ResponseMessage.ERR101, false);
			} else {
				res = new LoginResponse(ResponseCode.ERR999, ResponseMessage.ERR999, false);
			}
		}
		return res;
	}
	
	public User selectUser(int staffNo) {
		//User user = userMapper.selectUserInfo(staffNo);
		return queryUser("80575129");
	}

	public User queryUser(String staffNo) {
	    try {
	    	List<Name> dns = getDns(getUserFilter(staffNo));
	    	if(dns != null && dns.size() > 0){
	    		String[] attrs = new String[] { "sAMAccountName", "givenName", "displayName", "mail", "distinguishedName" };
	    		User user = (User)ldapTemplate.lookup(dns.get(0), attrs, new UserContextMapper());
	    		return user;
	    	} else {
	    		return null;
	    	}
		} catch (Exception e) {
			throw e;
		}
	}
	
	public List<Team> getAllTeams(){
		List<Team> teams = new ArrayList<Team>();
		String[] attrs = new String[] { "name", "distinguishedName" };
		try {
			List<Name> dns = getDns(getTeamFilter());
			for (Name dn : dns) {
				if(ConstantConfig.LDAP_BASE_TEAM_DN.equalsIgnoreCase(dn.toString())){//exclude the base DN
					continue;
				}
				Team team = ldapTemplate.lookup(dn, attrs, new TeamContextMapper());
				teams.add(team);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<Team>();
		}
		return teams;
	}
	
	public List<User> getTeamUsers(Team team){
		List<User> users = new ArrayList<User>();
		try {
			String[] attrs = new String[] { "sAMAccountName", "givenName", "displayName", "mail", "distinguishedName" };
			List<Name> dns = getDns(getTeamUsersFilter(), buildDn(team.getDn().replace("," + ConstantConfig.LDAP_BASE_DN, "")));
			for (Name dn : dns) {
				User user = ldapTemplate.lookup(dn, attrs, new UserContextMapper());
				String memberOf = user.getDn().replace("CN=" + user.getFullname() + ",", "");//eg. user.getDn() = "CN=Joker ZH Ou,OU=Tommy Team,OU=R&D,OU=CASCADE_GZ,OU=GZCASCADE,OU=COM"
				if(memberOf.equalsIgnoreCase(team.getDn())){//exclude the member not in the team
					users.add(user);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return users;
	}
	
	private LdapName buildDn(){
		LdapName name = LdapUtils.newLdapName(ConstantConfig.LDAP_BASE_TEAM_DN);
		return name;
	}
	
	private LdapName buildDn(String baseDn){
		LdapName name = LdapUtils.newLdapName(baseDn);
		return name;
	}

	private List<Name> getDns(Filter filter){
		List<Name> result = ldapTemplate.search(buildDn(), filter.encode(),
				SearchControls.SUBTREE_SCOPE, new AbstractContextMapper<Name>() {
					@Override
					protected Name doMapFromContext(DirContextOperations adapter) {
						return adapter.getDn();
					}
				});
		if (null == result || result.size() < 1) {
			return new ArrayList<Name>();
		} else {
			return result;
		}
	}
	
	private List<Name> getDns(Filter filter, LdapName buildDn){
		List<Name> result = ldapTemplate.search(buildDn, filter.encode(),
				SearchControls.SUBTREE_SCOPE, new AbstractContextMapper<Name>() {
					@Override
					protected Name doMapFromContext(DirContextOperations adapter) {
						return adapter.getDn();
					}
				});
		if (null == result || result.size() < 1) {
			return new ArrayList<Name>();
		} else {
			return result;
		}
	}
	
	private Filter getUserFilter(String staffNo){
		AndFilter andFilter = new AndFilter();
	    andFilter.and(new EqualsFilter("objectclass", "person"));
	    andFilter.and(new EqualsFilter("objectclass", "organizationalPerson"));
	    andFilter.and(new EqualsFilter("objectclass", "user"));
	    andFilter.and(new EqualsFilter("sAMAccountName", staffNo));
	    return andFilter;
	}
	
	private Filter getTeamUsersFilter(){
		AndFilter andFilter = new AndFilter();
	    andFilter.and(new EqualsFilter("objectclass", "person"));
	    andFilter.and(new EqualsFilter("objectclass", "organizationalPerson"));
	    andFilter.and(new EqualsFilter("objectclass", "user"));
	    return andFilter;
	}
	
	private Filter getTeamFilter(){
		AndFilter andFilter = new AndFilter();
	    andFilter.and(new EqualsFilter("objectclass", "top"));
	    andFilter.and(new EqualsFilter("objectclass", "organizationalUnit"));
	    return andFilter;
	}
	
	@SuppressWarnings("unused")
	private class AccountAttributesMapper implements AttributesMapper<User>{
		@Override
		public User mapFromAttributes(Attributes attributes) throws NamingException {
			User user = new User();
			user.setStaffNo((int)attributes.get("sAMAccountName").get());
			user.setName((String)attributes.get("givenName").get());
			user.setFullname((String)attributes.get("displayName").get());
			user.setEmail((String)attributes.get("mail").get());
			return user;
		}
	}

	private class UserContextMapper extends AbstractContextMapper<User> {
	    @Override
	    public User doMapFromContext(DirContextOperations context) {
	        User user = new User();
	        user.setStaffNo(Integer.parseInt(context.getStringAttribute("sAMAccountName")));
	        user.setName(context.getStringAttribute("givenName"));
	        user.setFullname(context.getStringAttribute("displayName"));
			user.setEmail(context.getStringAttribute("mail"));
			user.setDn(context.getStringAttribute("distinguishedName"));
	        return user;
	    }
	}
	
	private class TeamContextMapper extends AbstractContextMapper<Team> {
	    @Override
	    public Team doMapFromContext(DirContextOperations context) {
	    	Team team = new Team();
	        team.setName(context.getStringAttribute("name"));
	        //trim the base DN
	        //String teamDN = context.getStringAttribute("distinguishedName").replace(",DC=GZCASCADE,DC=COM", "");
	        team.setDn(context.getStringAttribute("distinguishedName"));
	        return team;
	    }
	}
	
}
