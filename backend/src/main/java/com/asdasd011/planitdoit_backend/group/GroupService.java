package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupService{
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupService(GroupRepository groupRepository,UserRepository userRepository){
        this.groupRepository=groupRepository;
        this.userRepository=userRepository;
    }

    public List<GroupResponse> getGroupsForUser(Long userId){
        return groupRepository.findByUserId(userId).stream().map(this::toResponse).toList();
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

    private GroupResponse toResponse(Group group){return new GroupResponse(group.getId(),group.getTitle(),group.getUser().getId());}
}