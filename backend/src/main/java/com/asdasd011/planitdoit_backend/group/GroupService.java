package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService{
    private final GroupRepository groupRepository;

    public GroupService(GroupRepository groupRepository){this.groupRepository=groupRepository;}

    public List<GroupResponse> getGroupsForUser(Long userId){
        return groupRepository.findByUserId(userId).stream().map(group -> new GroupResponse(group.getId(),group.getTitle(),group.getUser().getId())).toList();
    }
}