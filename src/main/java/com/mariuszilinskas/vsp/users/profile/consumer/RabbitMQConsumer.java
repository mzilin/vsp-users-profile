package com.mariuszilinskas.vsp.users.profile.consumer;

import com.mariuszilinskas.vsp.users.profile.dto.CreateUserDefaultProfileRequest;
import com.mariuszilinskas.vsp.users.profile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQConsumer.class);
    private final UserProfileService userProfileService;

    @RabbitListener(queues = "${rabbitmq.queues.profile-setup}")
    public void consumeCreateDefaultUserProfileMessage(CreateUserDefaultProfileRequest request) {
        logger.info("Received request to create user profile: {}", request);
        userProfileService.createDefaultUserProfile(request);
    }

    @RabbitListener(queues = "${rabbitmq.queues.delete-user-data}")
    public void consumeDeleteUserDataMessage(UUID userId) {
        logger.info("Received request to delete user data for User [userId: {}]", userId);
        userProfileService.deleteAllUserProfiles(userId);
    }

}
