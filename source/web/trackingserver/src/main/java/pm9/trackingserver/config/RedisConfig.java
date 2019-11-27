package pm9.trackingserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

// Start redis-server command: sudo redis-server /etc/redis/redis.conf

/**
 * Contains all connection configuration related to Redis server.
 */
@Configuration
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String hostName;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.port}")
    private int port;

    /**
     * This connection factory can be used to create an instance of RedisTemplate. Jedis is a Redis client.
     * @return JedisConnectionFactory bean
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        // return new JedisConnectionFactory();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * RedisTemplate bean is used to perform Redis operations.
     * @param jedisConnectionFactory It specifies the jedisConnectionFactory.
     * @return RedisTemplate<String, Object> bean
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }


}
