package com.margic.lookup;

import java.io.IOException;
import java.util.Properties;


public class Config {
    public static Properties loadConfig(final String configFile) throws IOException {
      Properties props = new Properties();
      props.load(Config.class.getClassLoader().getResourceAsStream("app.properties"));
      return props;
    }
}
