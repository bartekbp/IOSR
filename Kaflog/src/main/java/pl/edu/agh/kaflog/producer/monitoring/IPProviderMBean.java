package pl.edu.agh.kaflog.producer.monitoring;


import com.j256.simplejmx.common.JmxAttributeField;
import com.j256.simplejmx.common.JmxAttributeMethod;
import com.j256.simplejmx.common.JmxOperation;
import com.j256.simplejmx.common.JmxResource;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@JmxResource(beanName = "IPProvider", domainName = "IPProvider")
public class IPProviderMBean {

        @JmxAttributeMethod(description = "get ip")
        public String getIP() {
            try {
                return Inet4Address.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            return "Internal error";
        }
}
