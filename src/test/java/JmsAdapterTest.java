import at.conapi.plugins.common.endpoints.jms.adapter.AbstractJMSException;


import java.util.HashMap;
import java.util.Map;

public class JmsAdapterTest {
    public static void main(String[] args) throws AbstractJMSException {
// Using JNDI
        //AbstractConnectionFactory cf1 = JmsFactory.createConnectionFactory("t3://localhost:7001", "jms/ConnectionFactory");

// Direct instantiation
        Map<String, Object> props = new HashMap<>();
        props.put("brokerURL", "tcp://localhost:61616");
        //AbstractConnectionFactory cf2 = JmsFactory.createConnectionFactory("org.apache.activemq.ActiveMQConnectionFactory", props);

// Using the connection factory
//        AbstractConnection conn = cf1.createConnection();
    }
}
