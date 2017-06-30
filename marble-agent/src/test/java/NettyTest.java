import com.github.jxdong.marble.agent.entity.Result;
import com.github.jxdong.marble.agent.common.server.global.ThreadPool;
import com.github.jxdong.marble.agent.common.server.netty.client.NettyClientManager;
import com.github.jxdong.marble.agent.common.server.netty.server.NettyServer;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="djx_19881022@163.com">jeff</a>
 * @version 2016/5/23 16:06
 */
public class NettyTest{

    @Test
    public void testNettyServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //端口 9000 打开
                NettyServer.getInstance().run(9003);
            }
        }).start();

    }

    public static void main(String args[]) {
        ThreadPool.getFixedInstance().execute("", new Runnable() {
            @Override
            public void run() {
                Map<String, Object> data = new HashMap<>();
                data.put("EXECESULT", Result.SUCCESS(new HashMap<String, Object>()));
                NettyClientManager.getInstance().serviceInvoke("10000", "10.32.154.19", 9003, data);
            }
        });

    }

}
