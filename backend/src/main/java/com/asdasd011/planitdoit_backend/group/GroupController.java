package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import com.asdasd011.planitdoit_backend.user.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController{
    private final GroupService groupService;

    public GroupController(GroupService groupService){this.groupService=groupService;}

    @GetMapping
    public List<GroupResponse> getGroups(@AuthenticationPrincipal AuthenticatedUser user,@RequestParam(defaultValue="TITLE") GroupSort sort,@RequestParam(defaultValue="ASC") SortDirection direction){
        return groupService.getGroupsForUser(user.getId(),sort,direction);
    }

    @GetMapping("/{groupId}")
    public GroupResponse getGroup(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId) {return groupService.getGroupForUser(user.getId(),groupId);}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GroupResponse createGroup(@AuthenticationPrincipal AuthenticatedUser user,@RequestBody CreateGroupRequest request){return groupService.createGroup(user.getId(),request);}

    @PutMapping("/{groupId}")
    public GroupResponse updateGroup(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId,@RequestBody UpdateGroupRequest request){return groupService.updateGroup(user.getId(),groupId,request);}

    @DeleteMapping("/{groupId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGroup(@AuthenticationPrincipal AuthenticatedUser user,@PathVariable Long groupId){groupService.deleteGroup(user.getId(),groupId);}
}