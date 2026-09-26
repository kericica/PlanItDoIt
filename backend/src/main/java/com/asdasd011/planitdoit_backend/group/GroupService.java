package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.user.UserRepository;
import com.asdasd011.planitdoit_backend.task.TaskRepository;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;

@Service
public class GroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public GroupService(GroupRepository groupRepository,UserRepository userRepository,TaskRepository taskRepository){
        this.groupRepository=groupRepository;
        this.userRepository=userRepository;
        this.taskRepository=taskRepository;
    }

    public List<GroupResponse> getGroupsForUser(Long userId,GroupSort sort,SortDirection direction){
        if(sort==GroupSort.INCOMPLETE_TASKS)return getGroupsSortedByIncompletedTasks(userId,direction);
        Sort springSort=buildSort("title",direction);
        return groupRepository.findByUserId(userId,springSort).stream().map(this::toResponse).toList();
    }

    public GroupResponse getGroupForUser(Long userId,Long groupId){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        return toResponse(group);
    }

    public GroupResponse createGroup(Long userId,CreateGroupRequest request){
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
        Group group=new Group();
        group.setTitle(request.title());
        group.setUser(user);
        Group savedGroup=groupRepository.save(group);
        return toResponse(savedGroup);
    }

    public GroupResponse updateGroup(Long userId,Long groupId,UpdateGroupRequest request){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        group.setTitle(request.title());
        Group updatedGroup=groupRepository.save(group);
        return toResponse(updatedGroup);
    }

    public void deleteGroup(Long userId,Long groupId){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        groupRepository.delete(group);
    }

    private List<GroupResponse> getGroupsSortedByIncompletedTasks(Long userId,SortDirection direction){
        List<Group> groups=new ArrayList<>(groupRepository.findByUserId(userId));
        var incompleteTaskCounts=taskRepository.countIncompleteTasksByGroupForUser(userId).stream().collect(java.util.stream.Collectors.toMap(
            row->(Long)row[0],row->(Long)row[1]));
        Comparator<Group> comparator=Comparator.comparing(group->incompleteTaskCounts.getOrDefault(group.getId(),0L));
        if(direction==SortDirection.DESC)comparator=comparator.reversed();
        comparator=comparator.thenComparing(Group::getTitle,String.CASE_INSENSITIVE_ORDER).thenComparing(Group::getId);
        groups.sort(comparator);
        return groups.stream().map(this::toResponse).toList();
    }

    private Sort buildSort(String property,SortDirection direction){
        Sort.Direction springDirection=direction==SortDirection.ASC?Sort.Direction.ASC:Sort.Direction.DESC;
        return Sort.by(new Sort.Order(springDirection,property),new Sort.Order(Sort.Direction.ASC,"id"));
    }

    private GroupResponse toResponse(Group group){return new GroupResponse(group.getId(),group.getTitle(),group.getUser().getId());}
}