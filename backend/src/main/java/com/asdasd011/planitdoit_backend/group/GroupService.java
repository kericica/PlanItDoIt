package com.asdasd011.planitdoit_backend.group;

import com.asdasd011.planitdoit_backend.group.dto.GroupResponse;
import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.group.dto.CreateGroupRequest;
import com.asdasd011.planitdoit_backend.group.dto.UpdateGroupRequest;
import com.asdasd011.planitdoit_backend.user.User;
import com.asdasd011.planitdoit_backend.user.UserRepository;
import com.asdasd011.planitdoit_backend.task.TaskRepository;
import com.asdasd011.planitdoit_backend.task.TaskStatus;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
        Map<Long,long[]>completionCounts=getCompletionCounts(userId);

        if(sort==GroupSort.INCOMPLETE_TASKS)return getGroupsSortedByIncompletedTasks(userId,direction,completionCounts);
        Sort springSort=buildSort("title",direction);
        return groupRepository.findByUserId(userId,springSort).stream().map(group->toResponse(group,completionCounts.get(group.getId()))).toList();
    }

    public GroupResponse getGroupForUser(Long userId,Long groupId){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        return toResponse(group,getCompletionCountsForGroup(groupId));
    }

    public GroupResponse createGroup(Long userId,CreateGroupRequest request){
        User user=userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("user not found"));
        Group group=new Group();
        group.setTitle(request.title());
        group.setUser(user);
        Group savedGroup=groupRepository.save(group);
        return toResponse(savedGroup,null);
    }

    public GroupResponse updateGroup(Long userId,Long groupId,UpdateGroupRequest request){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        group.setTitle(request.title());
        Group updatedGroup=groupRepository.save(group);
        return toResponse(updatedGroup,getCompletionCountsForGroup(groupId));
    }

    public void deleteGroup(Long userId,Long groupId){
        Group group=groupRepository.findByIdAndUserId(groupId,userId).orElseThrow(()->new ResourceNotFoundException("group not found"));
        groupRepository.delete(group);
    }

    private List<GroupResponse> getGroupsSortedByIncompletedTasks(Long userId,SortDirection direction,Map<Long,long[]> completionCounts){
        List<Group> groups=new ArrayList<>(groupRepository.findByUserId(userId));
        Comparator<Group> comparator=Comparator.comparing(group->getIncompleteTaskCount(completionCounts.get(group.getId())));
        if(direction==SortDirection.DESC)comparator=comparator.reversed();
        comparator=comparator.thenComparing(Group::getTitle,String.CASE_INSENSITIVE_ORDER).thenComparing(Group::getId);
        groups.sort(comparator);
        return groups.stream().map(group->toResponse(group,completionCounts.get(group.getId()))).toList();
    }

    private Sort buildSort(String property,SortDirection direction){
        Sort.Direction springDirection=direction==SortDirection.ASC?Sort.Direction.ASC:Sort.Direction.DESC;
        return Sort.by(new Sort.Order(springDirection,property),new Sort.Order(Sort.Direction.ASC,"id"));
    }

    private Map<Long,long[]> getCompletionCounts(Long userId){
        Map<Long,long[]> counts=new HashMap<>();
        taskRepository.getCompletionCountsByGroupForUser(userId,TaskStatus.COMPLETED).forEach(row->{
            Long groupId=(Long)row[0];
            long total=((Number)row[1]).longValue();
            long completed=row[2]!=null?((Number)row[2]).longValue():0L;
            counts.put(groupId,new long[]{total,completed});
        });
        return counts;
    }

    private long[] getCompletionCountsForGroup(Long groupId){
        Object[] row=taskRepository.getCompletionCountsForGroup(groupId,TaskStatus.COMPLETED);
        long total=((Number)row[0]).longValue();
        long completed=row[1]!=null?((Number)row[1]).longValue():0L;
        return new long[]{total,completed};
    }

    private long getIncompleteTaskCount(long[] counts){
        if(counts==null)return 0L;
        return counts[0]-counts[1];
    }

    private BigDecimal calculateCompletionPercentage(long total,long completed){
        if(total==0)return null;
        return BigDecimal.valueOf(completed).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(total),2,RoundingMode.HALF_UP);
    }

    private GroupResponse toResponse(Group group,long[] counts){
        long totalTasks=counts!=null?counts[0]:0L;
        long completedTasks=counts!=null?counts[1]:0L;
        BigDecimal percentage=calculateCompletionPercentage(totalTasks,completedTasks);
        return new GroupResponse(group.getId(),group.getTitle(),group.getUser().getId(),totalTasks,completedTasks,percentage);
    }
}