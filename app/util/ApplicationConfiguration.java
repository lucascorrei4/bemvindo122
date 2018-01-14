package util;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import play.Play;

public class ApplicationConfiguration {
	private static final Logger logger = Logger.getLogger(ApplicationConfiguration.class);
	private static Properties properties;
	private static ApplicationConfiguration instance;

	@SuppressWarnings("static-access")
	public static ApplicationConfiguration getInstance() {
		if (instance == null) {
			instance = new ApplicationConfiguration();
			instance.getProperties();
		}
		return instance;
	}

	private static Properties getProperties() {
		properties = Play.configuration;
		return properties;
	}

	private String getProperty(String property) {
		if (properties != null)
			return properties.getProperty(property);
		return getProperties().getProperty(property);
	}

	private Integer getIntegerProperty(String key) {
		String property = getProperty(key);
		try {
			return Integer.valueOf(property);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public List<String> getJedisServers() {
		String servers = getProperty("app.jedis.servers");
		return Arrays.asList(servers.split(","));
	}

	public Integer getJedisMaxActive() {
		return getIntegerProperty("app.jedis.poll.maxactive");
	}

	public Integer getJedisPort() {
		return getIntegerProperty("app.jedis.port");
	}

	public Integer getJedisMinIdle() {
		return getIntegerProperty("app.jedis.poll.minidle");
	}

	public Integer getJedisMaxIdle() {
		return getIntegerProperty("app.jedis.poll.maxidle");
	}

	public Integer getJedisMaxWait() {
		return getIntegerProperty(".poll.maxwai");
	}

	public Integer getJedisTestPerEviction() {
		return getIntegerProperty("app.jedis.poll.numTestsPerEvictionRun");
	}

	public Integer getJedisTimeBetweenEviction() {
		return getIntegerProperty("app.jedis.poll.timeBetweenEvictionRunsMillis");
	}

	public Integer getJedisTimeOut() {
		return getIntegerProperty("app.jedis.timeout");
	}

	public boolean getJedisTestOnBorrow() {
		return Boolean.parseBoolean(getProperty("app.jedis.poll.testOnBorrow"));
	}

	public boolean getJedisTestOnReturn() {
		return Boolean.parseBoolean(getProperty("app.jedis.poll.testOnReturn"));
	}

	public boolean getJedisTestWhileIdle() {
		return Boolean.parseBoolean(getProperty("app.jedis.poll.testWhileIdle"));
	}

	public Integer getJedisRetries() {
		return getIntegerProperty("app.jedis.retries");
	}

	public Integer getJedisExpire() {
		return getIntegerProperty("app.jedis.expire.seconds");
	}

	public Integer getMinDelay() {
		return getIntegerProperty("app.min.delay");
	}

	public Integer getMaxDelay() {
		return getIntegerProperty("app.max.delay");
	}

	public String getMailHostName() {
		return getProperty("app.mail.hostName");
	}

	public String getMailUserName() {
		return getProperty("app.mail.userName");
	}

	public String getMailPassword() {
		return getProperty("app.mail.password");
	}

	public String getSMSUserId() {
		return getProperty("app.sms.userId");
	}

	public String getSMSPwd() {
		return getProperty("app.sms.pwd");
	}

	public String getSMSApiKey() {
		return getProperty("app.sms.apiKey");
	}

	public String getSMSApiUrl() {
		return getProperty("app.sms.url");
	}

	public String getOneSignalAppId() {
		return getProperty("onesignal.app.id");
	}

	public String getOneSignalAuthId() {
		return getProperty("onesignal.auth.id");
	}

	public String getGoogleShortenerUrlApiKey() {
		return getProperty("app.urlShortener.apiKey");
	}

	public String getSiteDomain() {
		return getProperty("site.domain");
	}
	
	public String getSiteMail() {
		return getProperty("site.mail");
	}

	public String getSiteTitle() {
		return getProperty("site.title");
	}
	
	public String getSiteName() {
		return getProperty("site.name");
	}

	public String getSiteFacebook() {
		return getProperty("site.facebook");
	}

	public String getSiteTwitter() {
		return getProperty("site.twitter");
	}

	public String getSiteTwitterShort() {
		return getProperty("site.twitter.short");
	}

	public String getSiteHeaderTitle() {
		return getProperty("site.header.title");
	}

	public String getSiteSlogan() {
		return getProperty("site.slogan");
	}
	
}
