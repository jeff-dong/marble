import org.springframework.remoting.rmi.RmiProxyFactoryBean;


public class AppClient {
    public static void main(String args[]){

        RmiProxyFactoryBean pfb = new RmiProxyFactoryBean();
        pfb.setServiceInterface(Application.class);
//        pfb.setServiceUrl("rmi://localhost/application");
        pfb.setServiceUrl("rmi://10.32.154.10:1099/application");
        pfb.afterPropertiesSet();
        Application app = (Application) pfb.getObject();

        System.out.println(app.isLoggingEnabled());
        app.setLoggingEnabled(false);
        System.out.println(app.isLoggingEnabled());
    }
}
