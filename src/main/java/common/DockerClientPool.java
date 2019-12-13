package common;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class DockerClientPool extends GenericObjectPool<DockerClient> {
    public static final DockerClientPool Instance = new DockerClientPool();
    
    public DockerClientPool() {
        super(new DockerClientPoolFactory());
    }
    
    public static void init() {
    }
    
    public DockerClient getClient() {
        try {
            return super.borrowObject();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Fail to get a DockerClient object");
        }
    }
    
    public void returnClient(DockerClient client) {
        super.returnObject(client);
    }

    public static class DockerClientPoolFactory extends BasePooledObjectFactory<DockerClient> {
        @Override
        public DockerClient create() throws Exception {
            return DockerClientBuilder.getInstance().build();
        }

        // Use the default PooledObject implementation
        @Override
        public PooledObject<DockerClient> wrap(DockerClient t) {
            return new DefaultPooledObject<>(t);
        }

        // When an object is returned to the pool, do nothing
        @Override
        public void passivateObject(PooledObject<DockerClient> p) throws Exception {
        }
    }
}
