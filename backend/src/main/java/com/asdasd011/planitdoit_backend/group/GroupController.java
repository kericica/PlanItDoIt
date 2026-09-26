package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController{
    private final GroupService groupService;

    public GroupController(GroupService groupService){this.groupService=groupService;}

    @GetMapping
    public List<GroupResponse> getGroups(@RequestParam(defaultValue="TITLE") GroupSort sort,@RequestParam(defaultValue="ASC") SortDirection direction){
        return groupService.getGroupsForUser(1L,sort,direction);
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@PathVariable Long groupId) {return groupService.getGroupForUser(1L,groupId);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@RequestBody CreateGroupRequest request){return groupService.createGroup(1L,request);}

    @PutMapping("/{groupId}")
    public GroupResponse updateGroup(@PathVariable Long groupId,@RequestBody UpdateGroupRequest request){return groupService.updateGroup(1L,groupId,request);}

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@PathVariable Long groupId){groupService.deleteGroup(1L,groupId);}
}