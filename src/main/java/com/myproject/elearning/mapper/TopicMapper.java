package com.myproject.elearning.mapper;

import com.myproject.elearning.domain.Topic;
import com.myproject.elearning.dto.response.user.PreferenceRes;
import com.myproject.elearning.mapper.base.MapperConfig;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(config = MapperConfig.class)
public interface TopicMapper {
    TopicMapper TOPIC_MAPPER = Mappers.getMapper(TopicMapper.class);

    PreferenceRes.MyTopic toMyTopic(Topic topic);

    Set<PreferenceRes.MyTopic> toMyTopics(Set<Topic> topics);

    default PreferenceRes toRes(Set<Topic> topics) {
        var myTopics = toMyTopics(topics);
        var preferenceRes = new PreferenceRes();
        preferenceRes.setMyTopics(myTopics);
        return preferenceRes;
    }

    //    @Mapping(target = "myTopics", source = "topics")
    //    PreferenceRes toRes(Integer dummy, Set<Topic> topics);
}
