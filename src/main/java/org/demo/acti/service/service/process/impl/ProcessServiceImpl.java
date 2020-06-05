package org.demo.acti.service.service.process.impl;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowNode;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.RepositoryServiceImpl;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.demo.acti.service.model.process.dto.CompleteTaskDto;
import org.demo.acti.service.model.process.dto.StartProcessDto;
import org.demo.acti.service.model.process.pojo.ProcessDefEntity;
import org.demo.acti.service.model.process.pojo.TaskDetailEntity;
import org.demo.acti.service.model.process.pojo.TaskEntity;
import org.demo.acti.service.service.BaseServiceImpl;
import org.demo.acti.service.service.process.IProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl extends BaseServiceImpl implements IProcessService {

    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private ProcessDiagramGenerator processDiagramGenerator;
    @Autowired
    private ManagementService managementService;

    @Override
    public List<ProcessDefEntity> processDefs() {
        List<ProcessDefinition> defs = repositoryService.createProcessDefinitionQuery().latestVersion().list();
        return defs.stream().map(def -> {
            ProcessDefEntity pd = new ProcessDefEntity();
            pd.setProcessKey(def.getKey());
            pd.setProcessName(def.getName());
            return pd;
        }).collect(Collectors.toList());
    }

    @Override
    public TaskEntity startProcess(StartProcessDto dto) {
        UserDetails userDetails = getUserDetails();
        Map<String, Object> variables = new HashMap<>();
        variables.put("title", dto.getTitle());
        // 注入流程变量
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(dto.getProcessKey(), variables);
        // 根据进程实例ID查询任务
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        TaskEntity taskEntity = new TaskEntity();
        if (Objects.nonNull(task)) {
            task.setOwner(String.valueOf(dto.getUserId()));
            task.setAssignee(String.valueOf(dto.getUserId()));
            task.setDescription(dto.getTitle());
            taskService.saveTask(task);
            taskEntity.setTaskId(task.getId());
            taskEntity.setTaskName(task.getName());
            taskEntity.setProcessIntanceId(processInstance.getId());
            taskEntity.setDescription(task.getDescription());
        } else {
            Execution execution = runtimeService.createExecutionQuery().processInstanceId(processInstance.getId()).activityId("_3").singleResult();
            if (Objects.nonNull(execution)) {
                taskEntity.setTaskId(execution.getId());
                taskEntity.setTaskName(execution.getName());
                taskEntity.setProcessIntanceId(processInstance.getId());
                taskEntity.setDescription(execution.getDescription());
            }
        }
        // 完成第一级节点到第二级节点任务
        completeTask(new CompleteTaskDto(task.getProcessInstanceId(), dto.getUserId(), "申请", dto.getData()));
        return taskEntity;
    }


    @Override
    public List<TaskEntity> agenda(String userId) {
        List<Task> tasks = taskService.createTaskQuery()
                .taskCandidateOrAssigned(userId).list();
        return tasks.stream().map(task -> {
            TaskEntity taskEntity = new TaskEntity();
            taskEntity.setTaskId(task.getId());
            taskEntity.setTaskName(task.getName());
            taskEntity.setProcessIntanceId(task.getProcessInstanceId());
            taskEntity.setDescription(task.getDescription());
            return taskEntity;
        }).collect(Collectors.toList());
    }

    @Override
    public TaskDetailEntity taskDetail(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        Map<String, Object> variables = taskService.getVariables(taskId);
        TaskDetailEntity detailEntity = new TaskDetailEntity();
        detailEntity.setTaskId(task.getId());
        detailEntity.setTaskName(task.getName());
        detailEntity.setProcessIntanceId(task.getProcessInstanceId());
        detailEntity.setDescription(task.getDescription());
        detailEntity.setVariables(variables);
        return detailEntity;
    }

    @Override
    public void completeTask(CompleteTaskDto dto) {
        Task task = taskService.createTaskQuery().processInstanceId(dto.getProcessIntanceId())
                .taskCandidateOrAssigned(String.valueOf(dto.getUserId())).singleResult();
        if (Objects.isNull(task)) {
            throw new IllegalArgumentException();
        }
        String title = (String) taskService.getVariable(task.getId(), "title");
        // 添加批注时候的审核人，通常应该从session获取
        Authentication.setAuthenticatedUserId("刘立");
        // 添加备注
        taskService.addComment(task.getId(), dto.getProcessIntanceId(), dto.getComment());

        taskService.complete(task.getId(), dto.getData());
        List<Task> nextTasks = taskService.createTaskQuery().processInstanceId(dto.getProcessIntanceId()).list();
        if (!CollectionUtils.isEmpty(nextTasks)) {
            for (Task nextTask : nextTasks) {
                nextTask.setOwner(task.getOwner());
                nextTask.setDescription(title);
                taskService.saveTask(nextTask);
            }
        }
    }


    @Override
    public InputStream generateDiagram(String processIntanceId) {
        //查询流程操作历史
        List<HistoricActivityInstance> activityInstances = porcessOrderedHistory(processIntanceId);
        if (CollectionUtils.isEmpty(activityInstances)) {
            throw new IllegalArgumentException("没有对应的流程实例：" + processIntanceId);
        }
        //获取流程定义信息和定义文件
        String processDefinitionId = activityInstances.get(0).getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        ProcessDefinitionEntity processDefinition = (ProcessDefinitionEntity)
                ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(processDefinitionId);
        //已执行的流程ID
        List<String> executedActivityInstanceIds = executedActivityInstanceIds(activityInstances);
        //已执行的流程连线
        List<String> flowIds = IProcessService.getHighLightedFlowIds(bpmnModel, processDefinition, activityInstances);
        //生成流程图
        return processDiagramGenerator.generateDiagram(bpmnModel, executedActivityInstanceIds, flowIds,
                "宋体", "微软雅黑", "黑体", true, "png");
    }

    @Override
    public byte[] generateDiagramBytes(String processIntanceId) {
        try {
            InputStream imageSteam = generateDiagram(processIntanceId);
            byte[] temp = new byte[512];
            int len;
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                while ((len = imageSteam.read(temp)) > 0) {
                    baos.write(temp, 0, len);
                }
                return baos.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<HistoricActivityInstance> porcessOrderedHistory(String processIntanceId) {
        //查询流程操作历史
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processIntanceId).orderByHistoricActivityInstanceStartTime().asc().list();

        HistoricActivityInstance a = historyService.createHistoricActivityInstanceQuery()//
                .processInstanceId(processIntanceId)//
                .unfinished()//未完成的活动(任务)
                .singleResult();

        boolean hasEndEvent = false;
        boolean isFinished = true;
        for (HistoricActivityInstance inst : activityInstances) {
            if ("endEvent".equals(inst.getActivityType())) {
                hasEndEvent = true;
            }
            if (Objects.isNull(inst.getEndTime())) {
                isFinished = false;
            }
        }
        if (!isFinished && hasEndEvent) {
            activityInstances = activityInstances.stream()
                    .filter(inst -> !"endEvent".equals(inst.getActivityType())).collect(Collectors.toList());
        }
        return activityInstances;
    }

    @Override
    public void deleteProcDeployment(String deploymentName) throws Exception {
        //找出部署的流程
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentName(deploymentName).singleResult();
        try {
            //删除流程定义，流程部署，以及二进制的资源记录
            repositoryService.deleteDeployment(deployment.getId());
            // 强制删除流程
            // repositoryService.deleteDeployment(deployment.getId(), true);
        }catch(Exception e){
            throw new Exception("还有流程实例在执行, 请执行结束后删除此流程");
        }
    }

    private List<String> executedActivityInstanceIds(List<HistoricActivityInstance> activityInstances) {
        List<String> executedActivityInstanceIds = new ArrayList<>();
        for (HistoricActivityInstance activityInstance : activityInstances) {
            if (Objects.isNull(activityInstance.getEndTime())) {
                continue;
            }
            executedActivityInstanceIds.add(activityInstance.getActivityId());
        }
        return executedActivityInstanceIds;
    }

    @Override
    public void jump(String curTaskId, String targetFlowNodeId) {
        // 当前任务
        Task currentTask = taskService.createTaskQuery().taskId(curTaskId).singleResult();
        // 获取流程定义
        Process process = repositoryService.getBpmnModel(currentTask.getProcessDefinitionId()).getMainProcess();
        // 获取目标节点定义
        FlowNode targetNode = (FlowNode) process.getFlowElement(targetFlowNodeId);
        // 删除当前运行任务
        String executionEntityId = managementService.executeCommand(new DeleteTaskCmd(currentTask.getId()));
        // 流程执行到来源节点
        managementService.executeCommand(new JumpCmd(targetNode, executionEntityId));
    }

    @Override
    public void backTaskDealer(String curTaskId, String processIntanceId, String targetFlowNodeId) {
        // 退回指定节点
        jump(curTaskId, targetFlowNodeId);
        // 节点赋值
        List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery() // 创建历史任务实例查询
                .processInstanceId(processIntanceId) // 用流程实例id查询
                .taskDefinitionKey(targetFlowNodeId)
                .finished() // 查询已经完成的任务
                .list();
        if (list != null && list.size() > 0) {
            // 查询回退后的节点正在运行的任务
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(processIntanceId).taskDefinitionKey(targetFlowNodeId).active().list();
            // 同一节点下有多个任务，则认定为会签任务setTaskDealerByTaskId
            for (int i = 0; i < taskList.size(); i++) {
                // 设置会签任务处理人（处理人顺序不管）
                taskList.get(i).setDescription(list.get(i).getDescription());
                taskList.get(i).setAssignee(list.get(i).getAssignee());
                taskService.saveTask(taskList.get(i));
            }
        }
    }

    public List<Map<String, Object>> historyAndComment(String processIntanceId) {
        List<Map<String, Object>> list = new ArrayList();
        Map<String, Object> map = new HashMap<>();

        //使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//历史任务表查询
                .processInstanceId(processIntanceId)//使用流程实例ID查询
                .finished() // 查询已经完成的任务
                .list();
        //遍历集合，获取每个任务ID
        if(htiList!=null && htiList.size()>0){
            for(HistoricTaskInstance hti:htiList){
                map = new HashMap<>();
                //任务ID
                String htaskId = hti.getId();
                //获取批注信息
                List taskList = taskService.getTaskComments(htaskId);//对用历史完成后的任务ID

                map.put("history", hti);
                map.put("comment", taskList);
                list.add(map);
            }
        }

        //list = taskService.getProcessInstanceComments(processInstanceId); 查询所有审批意见

        return list;
    }

    public void setRuntimeService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public void setTaskService(TaskService taskService) {
        this.taskService = taskService;
    }

    public void setProcessDiagramGenerator(ProcessDiagramGenerator processDiagramGenerator) {
        this.processDiagramGenerator = processDiagramGenerator;
    }

    public void setRepositoryService(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    public void setHistoryService(HistoryService historyService) {
        this.historyService = historyService;
    }

    public TaskService getTaskService() {
        return taskService;
    }
}
