package json.dto;

import lombok.Data;

/***
 * Created by zhengyu.shang on 2025/06/21.
 */
@Data
public class UserProxyConfig {
    private String proxyType;
    private String proxyHost;
    private String proxyPort;
    private String proxyUser;
    private String proxyPassword;
    private String proxySoft;

    public UserProxyConfig(String proxyType, String proxyHost, String proxyPort, String proxyUser, String proxyPassword, String proxySoft) {
        this.proxyType = proxyType;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        this.proxySoft = proxySoft;
    }
}
