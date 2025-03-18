package config;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/***
 * Created by zhengyu.shang on 2025/02/14.
 */
public class Config {
    private String url;
    private String username;
    private String password;

    public Config() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass()
                .getClassLoader()
                .getResourceAsStream("application.yml");
        Map<String, Object> obj = yaml.load(inputStream);
        Map<String, String> databaseConfig = (Map<String, String>) obj.get("database");

        this.url = databaseConfig.get("url");
        this.username = databaseConfig.get("username");
        this.password = databaseConfig.get("password");
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
