package com.mariuszilinskas.vsp.users.profile;

import com.mariuszilinskas.vsp.users.profile.config.AwsS3Config;
import com.mariuszilinskas.vsp.users.profile.config.FeignConfig;
import com.mariuszilinskas.vsp.users.profile.config.RabbitMQConfig;
import com.mariuszilinskas.vsp.users.profile.consumer.RabbitMQConsumer;
import com.mariuszilinskas.vsp.users.profile.controller.AvatarAdminController;
import com.mariuszilinskas.vsp.users.profile.controller.UserProfileController;
import com.mariuszilinskas.vsp.users.profile.repository.AvatarRepository;
import com.mariuszilinskas.vsp.users.profile.repository.UserProfileRepository;
import com.mariuszilinskas.vsp.users.profile.service.AwsS3ServiceImpl;
import com.mariuszilinskas.vsp.users.profile.service.AvatarServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test class for the Spring application context and bean configuration in the UserProfileService application.
 */
@SpringBootTest
class ProfileServiceApplicationTests {

    @Autowired
    private AwsS3ServiceImpl awsS3Service;

    @Autowired
    private AvatarServiceImpl avatarService;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private AvatarAdminController avatarAdminController;

    @Autowired
    private UserProfileController userProfileController;

    @Autowired
    private AwsS3Config awsS3Config;

    @Autowired
    private FeignConfig feignConfig;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private RabbitMQConsumer rabbitMQConsumer;

    @Test
    void contextLoads() {
    }

    @Test
    void awsS3ServiceBeanLoads() {
        assertNotNull(awsS3Service, "AWS S3 Service should have been auto-wired by Spring Context");
    }

    @Test
    void avatarServiceBeanLoads() {
        assertNotNull(avatarService, "Avatar Service should have been auto-wired by Spring Context");
    }

    @Test
    void avatarRepositoryBeanLoads() {
        assertNotNull(avatarRepository, "Avatar Repository should have been auto-wired by Spring Context");
    }

    @Test
    void userProfileRepositoryBeanLoads() {
        assertNotNull(userProfileRepository, "User Profile Repository should have been auto-wired by Spring Context");
    }

    @Test
    void avatarAdminControllerBeanLoads() {
        assertNotNull(avatarAdminController, "Avatar Admin Controller should have been auto-wired by Spring Context");
    }

    @Test
    void userProfileControllerBeanLoads() {
        assertNotNull(userProfileController, "User Profile Controller should have been auto-wired by Spring Context");
    }

    @Test
    void awsS3ConfigBeanLoads() {
        assertNotNull(awsS3Config, "AWS S3 Config should have been auto-wired by Spring Context");
    }

    @Test
    void feignConfigBeanLoads() {
        assertNotNull(feignConfig, "Feign Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQConfigBeanLoads() {
        assertNotNull(rabbitMQConfig, "RabbitMQ Config should have been auto-wired by Spring Context");
    }

    @Test
    void rabbitMQConsumerBeanLoads() {
        assertNotNull(rabbitMQConsumer, "RabbitMQ Consumer should have been auto-wired by Spring Context");
    }

}
