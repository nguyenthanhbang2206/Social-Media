package com.nguyenthanhbang.Social_media.mapper;

import com.nguyenthanhbang.Social_media.dto.request.NotificationRequest;
import com.nguyenthanhbang.Social_media.dto.response.NotificationResponse;
import com.nguyenthanhbang.Social_media.model.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Builder;

import java.util.List;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface NotificationMapper {

    NotificationResponse toNotificationResponse(Notification notification);

    List<NotificationResponse> toNotificationResponses(List<Notification> notifications);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "modifiedDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "modifiedBy", ignore = true)
    Notification toNotification(NotificationRequest request);
}
