import at.conapi.oss.jms.adapter.*;

import javax.naming.Context;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class JmsClient {
    private final JmsFactory jmsFactory;

    public JmsClient(String providerJarPath) throws Exception {
        // enumerate all jars in the given path and add them to the classloader
        List<URL> urls = new ArrayList<>();
        Path path = Paths.get(providerJarPath);
        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        URL url = file.toUri().toURL();
                        urls.add(url);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                });

        this.jmsFactory = new JmsFactory(urls.toArray(new URL[0]));

    }

    public void test(String message, String dest) throws Exception {
        // Direct instantiation
        Map<String, String> props = new HashMap<>();
        props.put("connectionURLs", "tcp://localhost:2506");
        props.put("defaultUser", "Administrator");
        props.put("defaultPassword", "Administrator");
        AbstractConnectionFactory cf = jmsFactory.createConnectionFactory("progress.message.jclient.ConnectionFactory", props);
        AbstractConnection conn = cf.createConnection();
  /*      progress.message.jclient.ConnectionFactory pcf = new progress.message.jclient.ConnectionFactory();
        pcf.setFaultTolerant(true);
        pcf.setPingInterval(30);
        pcf.setSocketConnectTimeout(3000);
*/
        conn.start();

        AbstractSession session = conn.createSession();
        AbstractDestination destination = session.createDestination(dest);

        AbstractConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(msg -> {
            System.out.println("Received message: " + msg.getText());
        //    msg.acknowledge();
        });
        AbstractMessage msg = session.createMessage("Hello", Map.of("key", "value"));
        session.createProducer(destination).send(msg);

        Thread.sleep(3000);

        session.close();

        conn.stop();
        conn.close();
    }

    public void jndiTest(String message, String dest) throws Exception {
        // JNDI instantiation
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sonicsw.jndi.mfcontext.MFContextFactory");
        env.put(Context.PROVIDER_URL, "localhost:2506");
        env.put(Context.SECURITY_PRINCIPAL, "Administrator");
        env.put(Context.SECURITY_CREDENTIALS, "Administrator");
        env.put("com.sonicsw.jndi.mfcontext.domain", "Domain1");

        AbstractConnectionFactory cf = jmsFactory.lookupConnectionFactory(env, "myCF" );
        AbstractConnection conn = cf.createConnection("Administrator", "Administrator");

        conn.start();

        AbstractSession session = conn.createSession();
        AbstractDestination destination = jmsFactory.lookupDestination(env, "myTopic");
//        AbstractDestination destination = session.createDestination("test");

        AbstractConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(msg -> {
            System.out.println("Received message: " + msg.getText());
            //    msg.acknowledge();
        });

        AbstractMessage msg = session.createMessage("Hello", null);
        session.createProducer(destination).send(msg);
        Thread.sleep(3000);

        session.close();

        conn.stop();
        conn.close();
    }

    public static void main(String[] args) throws Exception {
        JmsClient jmsClient = new JmsClient("C:\\sonic\\12.0.2\\MQ12.0\\lib\\");
        jmsClient.test("Hello","topic://test");
        jmsClient.jndiTest("Hello 2","jndi://testTopic");
    }
}
