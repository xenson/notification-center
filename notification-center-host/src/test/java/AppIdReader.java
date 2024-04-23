import com.ctrip.framework.foundation.Foundation;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * Created by xiayx on 2020/10/13.
 */
public class AppIdReader {
    public static void main(String[] args) {
        printAppId();
        printEnv();
    }

    public static void printAppId() {
        final String APP_PROPERTIES_CLASSPATH = "/META-INF/app.properties";
        Properties m_appProperties = new Properties();
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        if (in == null) {
            in = AppIdReader.class.getResourceAsStream(APP_PROPERTIES_CLASSPATH);
        }
        try {
            m_appProperties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("App ID： " + m_appProperties.getProperty("app.id"));
    }

    public static void printEnv() {
        System.out.println(Foundation.app().getAppId());
        System.out.println(Foundation.getProperty("env", "local"));
        System.out.println(Foundation.server().getEnv());

        //获取环境家族
        System.out.println(Foundation.server().getEnvFamily());
        //获取子环境
        System.out.println(Foundation.server().getSubEnv());
        //获取集群
        System.out.println(Foundation.server().getClusterName());
    }

}
