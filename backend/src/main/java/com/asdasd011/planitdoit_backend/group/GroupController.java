package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController{
    private final GroupService groupService;

    public GroupController(GroupService groupService){this.groupService=groupService;}

    @GetMapping
    public List<GroupResponse> getGroups(){return groupService.getGroupsForUser(1L);}
}