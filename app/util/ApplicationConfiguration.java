package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ApplicationConfiguration {
	private static final Logger logger = Logger.getLogger(ApplicationConfiguration.class);
	private static String confPath;
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
		logger.debug("####### ");
		logger.info("Loading Bemvi App Properties.");
		logger.debug("####### ");
		if (properties == null) {
			try {
				properties = new Properties();
				confPath = "C:\\Users\\Lucas\\Google Drive\\Lucas\\dev";
				File propertyFile = new File(confPath + "\\bemvindo.properties");
				FileInputStream inputStream = new FileInputStream(propertyFile);
				properties.load(inputStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
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
}
