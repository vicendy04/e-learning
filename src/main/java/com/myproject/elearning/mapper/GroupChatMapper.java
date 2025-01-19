package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.GroupChat;
import com.myproject.elearning.dto.request.chat.GroupChatCreateReq;
import com.myproject.elearning.dto.response.chat.GroupChatRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface GroupChatMapper {
    GroupChatMapper GROUP_CHAT_MAPPER = Mappers.getMapper(GroupChatMapper.class);

    GroupChat toEntity(GroupChatCreateReq request);

    GroupChatRes toRes(GroupChat entity);
}
