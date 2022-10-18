package core.framework.jpa.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import core.framework.bson.ExtendCodecRegistry;
import org.bson.codecs.configuration.CodecRegistries;
import org.hibernate.ogm.cfg.spi.Hosts;
import org.hibernate.ogm.datastore.mongodb.configuration.impl.MongoDBConfiguration;
import org.hibernate.ogm.datastore.mongodb.impl.MongoDBDatastoreProvider;
import org.hibernate.ogm.datastore.mongodb.logging.impl.Log;
import org.hibernate.ogm.datastore.mongodb.logging.impl.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ebin
 */
public class HibernateMongoDBDatastoreProvider extends MongoDBDatastoreProvider {
    private static final Log log = LoggerFactory.make(MethodHandles.lookup());

    @Override
    protected MongoClient createMongoClient(MongoDBConfiguration config) {
        MongoClientOptions clientOptions = config.buildOptions();
        clientOptions = mergeMongoClientOptions(clientOptions);
        List<MongoCredential> credentials = config.buildCredentials();
        log.connectingToMongo(config.getHosts().toString(), clientOptions.getConnectTimeout());
        try {
            List<ServerAddress> serverAddresses = new ArrayList<>(config.getHosts().size());
            for (Hosts.HostAndPort hostAndPort : config.getHosts()) {
                serverAddresses.add(new ServerAddress(hostAndPort.getHost(), hostAndPort.getPort()));
            }
            return credentials == null
                    ? new MongoClient(serverAddresses, clientOptions)
                    : new MongoClient(serverAddresses, credentials, clientOptions);
        } catch (RuntimeException e) {
            throw log.unableToInitializeMongoDB(e);
        }
    }

    private MongoClientOptions mergeMongoClientOptions(MongoClientOptions clientOptions) {
        MongoClientOptions.Builder optionsBuilder = MongoClientOptions.builder();
        optionsBuilder.description(clientOptions.getDescription());
        optionsBuilder.applicationName(clientOptions.getApplicationName());
        optionsBuilder.compressorList(clientOptions.getCompressorList());
        optionsBuilder.minConnectionsPerHost(clientOptions.getMinConnectionsPerHost());
        optionsBuilder.connectionsPerHost(clientOptions.getConnectionsPerHost());
        optionsBuilder.threadsAllowedToBlockForConnectionMultiplier(clientOptions.getThreadsAllowedToBlockForConnectionMultiplier());
        optionsBuilder.serverSelectionTimeout(clientOptions.getServerSelectionTimeout());
        optionsBuilder.maxWaitTime(clientOptions.getMaxWaitTime());
        optionsBuilder.maxConnectionIdleTime(clientOptions.getMaxConnectionIdleTime());
        optionsBuilder.maxConnectionLifeTime(clientOptions.getMaxConnectionLifeTime());
        optionsBuilder.connectTimeout(clientOptions.getConnectTimeout());
        optionsBuilder.socketTimeout(clientOptions.getSocketTimeout());
        optionsBuilder.socketKeepAlive(clientOptions.isSocketKeepAlive());
        optionsBuilder.sslEnabled(clientOptions.isSslEnabled());
        optionsBuilder.sslInvalidHostNameAllowed(clientOptions.isSslInvalidHostNameAllowed());
        optionsBuilder.sslContext(clientOptions.getSslContext());
        optionsBuilder.readPreference(clientOptions.getReadPreference());
        optionsBuilder.writeConcern(clientOptions.getWriteConcern());
        optionsBuilder.retryWrites(clientOptions.getRetryWrites());
        optionsBuilder.retryReads(clientOptions.getRetryReads());
        optionsBuilder.readConcern(clientOptions.getReadConcern());
        optionsBuilder.codecRegistry(CodecRegistries.fromRegistries(ExtendCodecRegistry.codecRegistry(), clientOptions.getCodecRegistry()));
        optionsBuilder.uuidRepresentation(clientOptions.getUuidRepresentation());
        optionsBuilder.serverSelector(clientOptions.getServerSelector());
        clientOptions.getCommandListeners().forEach(optionsBuilder::addCommandListener);
        clientOptions.getConnectionPoolListeners().forEach(optionsBuilder::addConnectionPoolListener);
        clientOptions.getClusterListeners().forEach(optionsBuilder::addClusterListener);
        clientOptions.getServerListeners().forEach(optionsBuilder::addServerListener);
        clientOptions.getServerMonitorListeners().forEach(optionsBuilder::addServerMonitorListener);
        optionsBuilder.socketFactory(clientOptions.getSocketFactory());
        optionsBuilder.cursorFinalizerEnabled(clientOptions.isCursorFinalizerEnabled());
        optionsBuilder.alwaysUseMBeans(clientOptions.isAlwaysUseMBeans());
        optionsBuilder.dbDecoderFactory(clientOptions.getDbDecoderFactory());
        optionsBuilder.dbEncoderFactory(clientOptions.getDbEncoderFactory());
        optionsBuilder.heartbeatFrequency(clientOptions.getHeartbeatFrequency());
        optionsBuilder.minHeartbeatFrequency(clientOptions.getMinHeartbeatFrequency());
        optionsBuilder.heartbeatConnectTimeout(clientOptions.getConnectTimeout());
        optionsBuilder.heartbeatSocketTimeout(clientOptions.getSocketTimeout());
        optionsBuilder.localThreshold(clientOptions.getLocalThreshold());
        optionsBuilder.requiredReplicaSetName(clientOptions.getRequiredReplicaSetName());
        optionsBuilder.autoEncryptionSettings(clientOptions.getAutoEncryptionSettings());
        return optionsBuilder.build();
    }
}
